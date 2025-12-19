package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import java.util.Random;

public class WorldMapScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture mapTexture;

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

        // --- TAMBAHAN TOMBOL STATUS (POJOK KIRI ATAS) ---
        TextButton statusBtn = new TextButton("STATUS", skin);

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
        addNode(NodeType.MAIN_STORY, 600, 400, "Royal Capital", skin, () -> {
            // Logika: Masuk ke NarrativeScreen
            // Nanti NarrativeScreen perlu dimodifikasi agar bisa load "Chapter" tertentu
            game.setScreen(new NarrativeScreen(game));
        });

        // --- 2. NODE RANDOM BATTLE (Hutan Grinding) ---
        addNode(NodeType.RANDOM_BATTLE, 250, 300, "Dark Forest", skin, () -> {
            // Logika: Random Enemy (50% Skeleton, 50% Slime)
            EnemyType randomEnemy = Math.random() > 0.5 ? EnemyType.SKELETON : EnemyType.SLIME;

            game.setScreen(new BattleScreen(game,
                "Background/bg_forest.png",
                randomEnemy,
                () -> game.setScreen(new WorldMapScreen(game)) // Callback: Balik ke Map setelah menang
            ));
        });

        // --- 3. NODE RANDOM BATTLE (Gunung Berapi - Hard) ---
        addNode(NodeType.RANDOM_BATTLE, 800, 600, "Mt. Doom", skin, () -> {
            // Logika: Lawan Boss atau Musuh Kuat
            game.setScreen(new BattleScreen(game,
                "Background/Lava.png",
                EnemyType.DRAGON_BOSS,
                () -> game.setScreen(new WorldMapScreen(game))
            ));
        });

        // --- 4. NODE SHOP (Desa Pedagang) ---
        addNode(NodeType.SHOP, 450, 200, "Merchant Village", skin, () -> {
            System.out.println("Masuk ke Shop Screen...");
            // game.setScreen(new ShopScreen(game)); // Nanti dibuat
        });
    }

    private void addNode(NodeType type, float x, float y, String name, Skin skin, Runnable action) {
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

        // Tambahkan Label Nama Tempat di bawah Node
        Label nameLabel = new Label(name, skin);
        nameLabel.setFontScale(0.8f);
        // Posisikan label di tengah bawah tombol
        nameLabel.setPosition(x + (nodeBtn.getWidth() - nameLabel.getWidth()) / 2, y - 20);

        // Listener Klik
        nodeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        stage.addActor(nodeBtn);
        stage.addActor(nameLabel);
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
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
