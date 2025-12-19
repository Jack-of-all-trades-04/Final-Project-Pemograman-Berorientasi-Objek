package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WorldMapScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture mapTexture;
    private Texture nodeTexture; // Gambar titik merah/pin

    public WorldMapScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Load Gambar Peta
        // Pastikan Anda sudah load ini di ResourceManager atau load manual disini
        try {
            mapTexture = new Texture(Gdx.files.internal("Background/WorldMap.png"));
        } catch (Exception e) {
            System.err.println("Gambar map tidak ketemu, pastikan path benar!");
        }

        // 2. Bikin Texture Titik Merah (Node) secara coding (biar gak perlu cari aset lagi)
        createNodeTexture();

        setupUI();
    }

    private void createNodeTexture() {
        // Membuat lingkaran merah kecil ukuran 32x32
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillCircle(16, 16, 16);
        nodeTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void setupUI() {
        // A. Pasang Background Peta
        if (mapTexture != null) {
            Image bgImage = new Image(mapTexture);
            bgImage.setFillParent(true); // Peta memenuhi layar
            stage.addActor(bgImage);
        }

        // B. Pasang Node-Node (Lokasi)

        // NODE 1: HUTAN (Battle)
        // Koordinat misal: x=200, y=300
        addNode(200, 300, "Hutan Terlarang", () -> {
            // Aksi: Masuk ke Battle Screen
            // Parameter terakhir adalah logic: "Kalau menang, balik ke layar peta INI lagi"
            game.setScreen(new BattleScreen(
                "Background/bg_forest.png",
                EnemyType.SKELETON,
                () -> game.setScreen(WorldMapScreen.this) // CALLBACK: Balik sini lagi
            ));
        });

        // NODE 2: ISTANA (Narrative)
        addNode(600, 400, "Istana Raja", () -> {
            // Aksi: Masuk ke Cerita
            // NarrativeScreen juga harus diedit nanti kalau mau balik ke Map otomatis
            game.setScreen(new NarrativeScreen(game));
        });

        // NODE 3: GUNUNG BERAPI (Battle Boss)
        addNode(800, 600, "Gunung Bahamut", () -> {
            game.setScreen(new BattleScreen(
                "Background/Lava.png", // Asumsi ada gambar ini
                EnemyType.DRAGON_BOSS,
                () -> game.setScreen(WorldMapScreen.this)
            ));
        });
    }

    // Method helper untuk membuat Tombol Node
    private void addNode(float x, float y, String locationName, Runnable onClickAction) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(nodeTexture);
        ImageButton nodeBtn = new ImageButton(drawable);

        nodeBtn.setPosition(x, y);

        // Listener saat node diklik
        nodeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pergi ke: " + locationName);
                // Jalankan aksi pindah layar
                onClickAction.run();
            }
        });

        stage.addActor(nodeBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void dispose() {
        stage.dispose();
        if(mapTexture!=null) mapTexture.dispose();
        if(nodeTexture!=null) nodeTexture.dispose();
    }

    // Boilerplate
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
