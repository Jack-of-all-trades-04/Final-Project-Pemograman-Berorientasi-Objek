package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Music bgm;

    // UI Components
    private Table menuTable;      // Wadah tombol-tombol
    private Label clickToPlayLbl; // Teks kedap-kedip
    private Image overlay;        // Lapisan hitam transparan (Efek Blur/Dim)

    private float stateTime = 0f; // Timer untuk animasi kedap-kedip

    // State Management
    private enum MenuState {
        PRESS_TO_START, // Kondisi awal (Belum diklik)
        SHOW_MENU       // Kondisi setelah diklik (Menu muncul)
    }
    private MenuState currentState = MenuState.PRESS_TO_START;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        ResourceManager.getInstance().loadAssets();
        bgm = ResourceManager.getInstance().getMusic("Soundtrack/MainMenu.mp3");
        bgm.setLooping(true);
        bgm.play();

        // Load Background (Pastikan path benar)
        try {
            backgroundTexture = new Texture(Gdx.files.internal("Background/MainMenu.png"));
        } catch (Exception e) {
            // Fallback jika gambar belum ada
            backgroundTexture = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");
        }

        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. BUAT OVERLAY (Efek Gelap/Blur)
        // Kita buat kotak hitam 1x1 pixel lalu ditarik memenuhi layar
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f); // Hitam, Opacity 70%
        pixmap.fill();
        overlay = new Image(new Texture(pixmap));
        overlay.setFillParent(true);
        overlay.setVisible(false); // Awalnya sembunyi
        overlay.getColor().a = 0f; // Transparan total di awal

        // 2. LABEL "CLICK SCREEN TO PLAY"
        clickToPlayLbl = new Label("- Click Screen to Play -", skin);
        clickToPlayLbl.setAlignment(Align.center); // Pastikan teks rata tengah

        // Buat Table khusus untuk Label ini agar selalu di tengah layar
        Table labelTable = new Table();
        labelTable.setFillParent(true);// Memenuhi layar
        // PERUBAHAN DISINI:
        // .bottom() -> Paksa isi table turun ke bawah layar
        labelTable.bottom();

        // .padBottom(30) -> Beri jarak 30 pixel dari tepi bawah layar (bisa disesuaikan)
        // Ini akan menempatkannya di bawah tulisan "Explore..." background
        labelTable.add(clickToPlayLbl).padBottom(10);

        // 3. TABLE MENU (Tombol-tombol)
        menuTable = new Table();
        menuTable.setFillParent(true);
        menuTable.setVisible(false); // Awalnya sembunyi

        // Bikin Tombol-Tombol
        TextButton btnNewGame = createMenuButton("NEW GAME", skin, () -> {
            System.out.println("Deleting old data...");

            // 1. HAPUS DATA LAMA DULU
            com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                .resetPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.ResetCallback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Old data deleted. Creating new save...");

                        // 2. SETELAH HAPUS, MINTA DATA BARU (LOAD)
                        // Karena di database kosong, Backend otomatis akan bikin Level 1 baru
                        com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                            .loadPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.LoadCallback() {
                                @Override
                                public void onSuccess(UnitStats stats) {
                                    // 3. UPDATE STATS GLOBAL
                                    game.playerStats = stats;

                                    // 4. MASUK KE CERITA (Bukan Map, karena New Game biasanya mulai dari Intro)
                                    game.setScreen(new NarrativeScreen(game, 1));
                                }

                                @Override
                                public void onFail(String msg) {
                                    System.err.println("Error creating new game: " + msg);
                                }
                            });
                    }

                    @Override
                    public void onFail(String msg) {
                        System.err.println("Failed to delete save data. Connection error?");
                    }
                });
        });

        TextButton btnLoadGame = createMenuButton("LOAD GAME", skin, () -> {
            // Tampilkan feedback "Loading..."
            System.out.println("Connecting to server...");

            // Panggil Network Manager
            com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                .loadPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.LoadCallback() {
                    @Override
                    public void onSuccess(UnitStats stats) {
                        System.out.println("Data Loaded: " + stats.getName() + " Lvl " + stats.getLevel());

                        // 1. Update Global Player Stats di Main
                        game.playerStats = stats;

                        // 2. Masuk ke World Map
                        game.setScreen(new WorldMapScreen(game));
                    }

                    @Override
                    public void onFail(String msg) {
                        System.err.println("Load Failed: " + msg);
                        // Nanti bisa munculin Dialog error disini
                    }
                });
        });

        TextButton btnSettings = createMenuButton("SETTINGS", skin, () -> {
            System.out.println("Masuk ke Settings Screen...");
        });

        TextButton btnCredits = createMenuButton("CREDITS", skin, () -> {
            System.out.println("Credit: Created by You");
        });

        TextButton btnQuit = createMenuButton("QUIT GAME", skin, () -> {
            Gdx.app.exit(); // Keluar aplikasi
        });

        // Susun Tombol di Tengah
        float btnWidth = 250f;
        float btnHeight = 50f;
        float pad = 10f;

        menuTable.add(btnNewGame).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnLoadGame).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnSettings).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnCredits).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnQuit).width(btnWidth).height(btnHeight).pad(pad).row();

        // 4. PASANG LISTENER KLIK LAYAR (Untuk transisi awal)
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Hanya bereaksi jika masih di state awal
                if (currentState == MenuState.PRESS_TO_START) {
                    transitionToMenu();
                }
            }
        });

        // Tambahkan ke Stage (Urutan Render: Overlay -> Label -> Menu)
        stage.addActor(overlay);      // Layer 1
        stage.addActor(labelTable);   // Layer 2 (Label Kedap-Kedip - REVISI)
        stage.addActor(menuTable);      // Layer 3

    }

    // Helper membuat tombol menu
    private TextButton createMenuButton(String text, Skin skin, Runnable action) {
        TextButton btn = new TextButton(text, skin);
        btn.addListener(new SoundListener());
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Play sound click disini jika ada
                action.run();
            }
        });
        return btn;
    }

    // LOGIKA TRANSISI (Magic Happens Here)
    private void transitionToMenu() {
        currentState = MenuState.SHOW_MENU;

        // 1. Hilangkan Teks "Click to Play"
        clickToPlayLbl.addAction(Actions.fadeOut(0.5f));

        // 2. Munculkan Overlay Gelap (Fade In)
        overlay.setVisible(true);
        overlay.addAction(Actions.fadeIn(0.5f));

        // 3. Munculkan Menu Tombol (Fade In + Move Up sedikit biar elegan)
        menuTable.setVisible(true);
        menuTable.getColor().a = 0f;
        menuTable.setPosition(0, -50); // Mulai dari agak bawah

        menuTable.addAction(Actions.parallel(
            Actions.fadeIn(0.5f),
            Actions.moveTo(0, 0, 0.5f, com.badlogic.gdx.math.Interpolation.swingOut)
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;

        // ANIMASI KEDAP-KEDIP (Blinking Text)
        if (currentState == MenuState.PRESS_TO_START) {
            // Gunakan Sinus Wave untuk Alpha (0.0 sampai 1.0)
            float alpha = (MathUtils.sin(stateTime * 5f) + 1) / 2f;
            clickToPlayLbl.setColor(1, 1, 1, alpha);
        }

        stage.getBatch().begin();
        // Gambar Background paling belakang
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    // Boilerplate
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        bgm.stop();
    }
}
