package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.NetworkManager;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WorldMapScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture mapTexture;
    private Music bgm;

    // Texture untuk masing-masing jenis Node
    private Texture battleNodeTex;
    private Texture storyNodeTex;
    private Texture shopNodeTex;

    // Enum untuk Jenis Node
    public enum NodeType {
        MAIN_STORY,
        RANDOM_BATTLE,
        SHOP
    }

    public WorldMapScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        bgm = ResourceManager.getInstance().getMusic("Soundtrack/WorldMap.mp3");

        try {
            mapTexture = new Texture(Gdx.files.internal("Background/WorldMap.png"));
        } catch (Exception e) {
            // Fallback jika gambar belum ada, pakai warna solid
            Pixmap p = new Pixmap(1,1, Pixmap.Format.RGBA8888);
            p.setColor(Color.DARK_GRAY); p.fill();
            mapTexture = new Texture(p);
        }

        // Generate Texture Node Secara Coding (Biar gak perlu aset png dulu)
        battleNodeTex = createNodeTexture(Color.RED);   // Merah = Berantem
        storyNodeTex = createNodeTexture(Color.GOLD);   // Emas = Cerita Utama
        shopNodeTex = createNodeTexture(Color.CYAN);    // Biru = Toko

        setupUI();
    }

    // Helper membuat lingkaran warna
    private Texture createNodeTexture(Color color) {
        Pixmap pixmap = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(20, 20, 20);
        // Tambah border putih biar jelas
        pixmap.setColor(Color.WHITE);
        pixmap.drawCircle(20, 20, 20);
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    private void setupUI() {
        // 1. Background Peta
        Image bgImage = new Image(mapTexture);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Skin skin = ResourceManager.getInstance().getSkin();

        float screenH = Gdx.graphics.getHeight();
        float screenW = Gdx.graphics.getWidth();

        // 1. TOMBOL STATUS (Kiri Atas) - Sudah ada
        TextButton statusBtn = new TextButton("STATUS", skin);
        statusBtn.setPosition(20, screenH - 60);
        statusBtn.setSize(100, 40);
        statusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StatusScreen(game));
            }
        });
        statusBtn.addListener(new SoundListener());
        stage.addActor(statusBtn);

        // 2. TOMBOL SKILLS (Sebelah Status)
        TextButton skillBtn = new TextButton("SKILLS", skin);
        skillBtn.setPosition(130, screenH - 60); // Geser X +110
        skillBtn.setSize(100, 40);
        skillBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SkillScreen(game));
            }
        });
        skillBtn.addListener(new SoundListener());
        stage.addActor(skillBtn);

        // 3. TOMBOL MAIN MENU (Kanan Atas)
        TextButton menuBtn = new TextButton("MENU", skin);
        menuBtn.setPosition(screenW - 120, screenH - 60); // Pojok Kanan
        menuBtn.setSize(100, 40);
        menuBtn.setColor(Color.SALMON); // Warna beda biar mencolok (misal mau keluar)
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Konfirmasi keluar? Atau langsung aja
                // Jangan lupa save otomatis kalau mau, atau peringatkan user
                bgm.stop();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        menuBtn.addListener(new SoundListener());
        stage.addActor(menuBtn);

        // Posisi: x=20, y=TinggiLayar - TinggiTombol - Margin
        statusBtn.setPosition(20, Gdx.graphics.getHeight() - 60);
        statusBtn.setSize(100, 40);

        statusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Pindah ke Status Screen
                game.setScreen(new StatusScreen(game));
            }
        });

        stage.addActor(statusBtn);

        // ==========================================
        // KONFIGURASI NODE-NODE DI PETA
        // ==========================================

        // --- 1. NODE MAIN STORY (Istana) ---
        addNode(NodeType.MAIN_STORY, 600, 400, "Royal Capital (Prologue)", skin, () -> {
            // Logika: Masuk ke NarrativeScreen dengan ID 1
            game.setScreen(new NarrativeScreen(game,1));
        }, 1);

        addNode(NodeType.MAIN_STORY, 300, 500, "Hidden Library (Ch 2)", skin, () -> {
            // Panggil NarrativeScreen dengan ID 2
            game.setScreen(new NarrativeScreen(game, 2));
        }, 2);

        addNode(NodeType.MAIN_STORY, 880, 420, "Mage Academy (Ch.3)", skin, () -> {
            game.setScreen(new NarrativeScreen(game, 3));
        }, 3);

        addNode(NodeType.MAIN_STORY, 680, 100, "Old Battlefield (Ch.4)", skin, () -> {
            game.setScreen(new NarrativeScreen(game, 4));
        }, 4);

        addNode(NodeType.MAIN_STORY, 1150, 550, "Dragon Shrine (Final)", skin, () -> {
            game.setScreen(new NarrativeScreen(game, 5));
        }, 5);

        // --- 2. NODE RANDOM BATTLE (Hutan&Ruins Grinding) ---
        addNode(NodeType.RANDOM_BATTLE, 250, 350, "Bobota Forest", skin, () -> {
            // Logika: Random Enemy (50% Skeleton, 50% Slime)
            EnemyType randomEnemy = Math.random() > 0.5 ? EnemyType.ANOMIMUS : EnemyType.BEELING;

            game.setScreen(new BattleScreen(game,
                "Background/Bobota Forest.png",
                randomEnemy,
                () -> game.setScreen(new WorldMapScreen(game)) // Callback: Balik ke Map setelah menang
            ));
        });
        addNode(NodeType.RANDOM_BATTLE, 500, 50, "Kalimba Forest", skin, () -> {
            // Logika: Random Enemy (50% Skeleton, 50% Slime)
            EnemyType randomEnemy = Math.random() > 0.5 ? EnemyType.ANOMIMUS : EnemyType.BEELING;

            game.setScreen(new BattleScreen(game,
                "Background/Kalimba Forest.png",
                randomEnemy,
                () -> game.setScreen(new WorldMapScreen(game)) // Callback: Balik ke Map setelah menang
            ));
        });
        addNode(NodeType.RANDOM_BATTLE, 950, 300, "Aliz Ruins", skin, () -> {
            // Logika: Random Enemy (50% Skeleton, 50% Slime)
            EnemyType randomEnemy = Math.random() > 0.5 ? EnemyType.SLIME : EnemyType.GOLEM;

            game.setScreen(new BattleScreen(game,
                "Background/Aliz Ruins.png",
                randomEnemy,
                () -> game.setScreen(new WorldMapScreen(game)) // Callback: Balik ke Map setelah menang
            ));
        });
        addNode(NodeType.RANDOM_BATTLE, 100, 600, "Kazak Ruins", skin, () -> {
            // Logika: Random Enemy (50% Skeleton, 50% Slime)
            EnemyType randomEnemy = Math.random() > 0.5 ? EnemyType.SLIME : EnemyType.GOLEM;

            game.setScreen(new BattleScreen(game,
                "Background/Kazak Ruins.png",
                randomEnemy,
                () -> game.setScreen(new WorldMapScreen(game)) // Callback: Balik ke Map setelah menang
            ));
        });

        // --- 3. NODE BOSS HUTAN (FARHAT - Level 10+) ---
        // Ubah EnemyType ke FARHAT (sesuai Enum baru Anda)
        addNode(NodeType.RANDOM_BATTLE, 800, 600, "Babatan Forest (Boss)", skin, () -> {

            // CEK LEVEL PLAYER
            if (game.playerStats.getLevel() < 10) {
                showLevelLockedDialog("Farhat", 10);
            } else {
                // Level Cukup -> Mulai Battle Lawan Boss
                game.setScreen(new BattleScreen(
                    game,
                    "Background/Bobota Forest.png",
                    EnemyType.FARHAT, // Pastikan EnemyType.FARHAT ada
                    () -> {
                        // Reward khusus Boss bisa ditaruh disini atau di UnitStats
                        game.setScreen(new WorldMapScreen(game));
                    }
                ));
            }
        });

        // --- 4. NODE BOSS RUINS (MANDA - Level 10+) ---
        // Anggap ini node baru atau mengganti yang lama
        addNode(NodeType.RANDOM_BATTLE, 1200, 200, "Malaketh Ruins (Boss)", skin, () -> {

            if (game.playerStats.getLevel() < 10) {
                showLevelLockedDialog("Manda", 10);
            } else {
                game.setScreen(new BattleScreen(
                    game,
                    "Background/Kazak Ruins.png",
                    EnemyType.MANDA, // Pastikan EnemyType.MANDA ada
                    () -> game.setScreen(new WorldMapScreen(game))
                ));
            }
        });

        // --- 5. NODE SHOP (Desa Pedagang) ---
        addNode(NodeType.SHOP, 450, 200, "Merchant Village", skin, () -> {
            System.out.println("Masuk ke Shop Screen...");
            game.setScreen(new ShopScreen(game));
            bgm.play();
        });
    }

    private void showLevelLockedDialog(String bossName, int reqLevel) {
        Skin skin = ResourceManager.getInstance().getSkin();
        com.badlogic.gdx.scenes.scene2d.ui.Dialog d = new com.badlogic.gdx.scenes.scene2d.ui.Dialog("WARNING", skin);

        d.text("DANGER! This area is controlled by " + bossName + ".\n" +
            "You need Level " + reqLevel + " to enter.");
        d.button("OK");
        d.show(stage);
        bgm.play();
    }

    private void addNode(NodeType type, float x, float y, String name, Skin skin, Runnable action, int reqChapter) {
        // Pilih Texture berdasarkan Tipe
        TextureRegionDrawable drawable = null;
        switch (type) {
            case MAIN_STORY: drawable = new TextureRegionDrawable(storyNodeTex); break;
            case RANDOM_BATTLE: drawable = new TextureRegionDrawable(battleNodeTex); break;
            case SHOP: drawable = new TextureRegionDrawable(shopNodeTex); break;
        }

        // Buat Tombol Node
        ImageButton nodeBtn = new ImageButton(drawable);
        nodeBtn.setPosition(x, y);

        // Load progress chapter
        int playerProgress = game.playerStats.getUnlockedChapter();

        if (playerProgress < reqChapter) {
            // JIKA BELUM UNLOCK:
            nodeBtn.setColor(Color.DARK_GRAY); // Gelapkan tombol visualnya
            nodeBtn.setDisabled(true);         // Matikan fungsi tombol (opsional di level Actor)

            // Listener Khusus untuk menampilkan pesan "LOCKED"
            nodeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Tampilkan Dialog Bahwa Chapter Terkunci
                    showLockedDialog(reqChapter);
                }
            });
        } else {
            // JIKA SUDAH UNLOCK: Normal
            nodeBtn.setColor(Color.WHITE);
            nodeBtn.addListener(new SoundListener());
            nodeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    bgm.stop();
                    action.run();
                }
            });
        }

        // Tambahkan Label Nama Tempat di bawah Node
        Label nameLabel = new Label(name, skin);
        nameLabel.setFontScale(0.8f);
        // Posisikan label di tengah bawah tombol
        nameLabel.setPosition(x + (nodeBtn.getWidth() - nameLabel.getWidth()) / 2, y - 20);

        stage.addActor(nodeBtn);
        stage.addActor(nameLabel);
    }

    private void showLockedDialog(int reqChapter) {
        com.badlogic.gdx.scenes.scene2d.ui.Dialog d = new com.badlogic.gdx.scenes.scene2d.ui.Dialog("LOCKED", ResourceManager.getInstance().getSkin());
        d.text("Complete Chapter " + (reqChapter - 1) + " first!");
        d.button("OK");
        d.show(stage);
    }

    private void addNode(NodeType type, float x, float y, String name, Skin skin, Runnable action) {
        addNode(type, x, y, name, skin, action, 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if(mapTexture != null) mapTexture.dispose();
        battleNodeTex.dispose();
        storyNodeTex.dispose();
        shopNodeTex.dispose();
    }

    // Boilerplate standard
    @Override public void show() {
        Gdx.input.setInputProcessor(stage);

        if (bgm != null && !bgm.isPlaying()) {
            bgm.play();
        }

        // LOAD ULANG DATA DARI SERVER (Agar Uang Sinkron setelah dari Shop)
        System.out.println("Refreshing Player Data from Server...");
        NetworkManager.getInstance().loadPlayer("User1", new NetworkManager.LoadCallback() {
            @Override
            public void onSuccess(UnitStats stats) {
                // Update variable global 'playerStats' di Main
                game.playerStats = stats;
                System.out.println("Data Synced! Money: " + stats.getManaCrystals());
            }

            @Override
            public void onFail(String msg) {
                System.err.println("Gagal Sync Data: " + msg);
            }
        });
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
