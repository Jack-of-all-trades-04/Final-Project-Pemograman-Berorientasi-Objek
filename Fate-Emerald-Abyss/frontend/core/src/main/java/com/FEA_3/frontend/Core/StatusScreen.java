package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StatusScreen implements Screen {
    private Main game;
    private Stage stage;
    private UnitStats stats; // Referensi ke data player

    public StatusScreen(Main game) {
        this.game = game;
        this.stats = game.playerStats; // Ambil data dari Main Global

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. Main Table (Layout Utama)
        Table root = new Table();
        root.setFillParent(true);
        // Background Gelap Transparan (Opsional, atau pakai gambar)
        // root.setBackground(...);

        // 2. Judul Screen
        Label title = new Label("PLAYER STATUS", skin);
        title.setFontScale(2.0f);
        title.setColor(Color.GOLD);

        // 3. Panel Info Kiri (Gambar & Nama)
        Table leftPanel = new Table();
        Texture charTex = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");
        Image charImage = new Image(charTex);

        Label nameLbl = new Label(stats.getName(), skin);
        Label jobLbl = new Label("Class: Saber", skin);

        // PERBAIKAN DISINI: Set warna langsung ke Labelnya
        jobLbl.setColor(Color.LIGHT_GRAY);

        leftPanel.add(charImage).size(200, 200).row();
        leftPanel.add(nameLbl).padTop(10).row();
        leftPanel.add(jobLbl); // Tidak perlu .color() lagi disini

        // 4. Panel Info Kanan (Statistik Angka)
        Table rightPanel = new Table();
        rightPanel.defaults().left().pad(5); // Default rata kiri & padding

        // Helper method biar codingan rapi
        addStatRow(rightPanel, "Level:", String.valueOf(stats.getLevel()), Color.CYAN, skin);
        addStatRow(rightPanel, "EXP:", stats.getCurrentExp() + " / " + stats.getMaxExp(), Color.WHITE, skin);
        rightPanel.add().height(20).row(); // Spacer

        addStatRow(rightPanel, "HP:", stats.getCurrentHp() + " / " + stats.getMaxHp(), Color.GREEN, skin);
        addStatRow(rightPanel, "MP:", stats.getCurrentMp() + " / " + stats.getMaxMp(), Color.BLUE, skin);
        addStatRow(rightPanel, "Wallet:", stats.getManaCrystals() + " Crystals", Color.GOLD, skin);
        rightPanel.add().height(20).row(); // Spacer

        addStatRow(rightPanel, "Attack:", String.valueOf(stats.getAttackPower()), Color.RED, skin);
        addStatRow(rightPanel, "Defense:", String.valueOf(stats.getDefense()), Color.ORANGE, skin);
        addStatRow(rightPanel, "Speed:", String.valueOf(stats.getSpeed()), Color.YELLOW, skin);
        addStatRow(rightPanel, "Crit Chance:", stats.getCritChance() + "%", Color.PURPLE, skin);
        addStatRow(rightPanel, "Accuracy:", stats.getAccuracy() + "%", Color.PINK, skin);

        // 5. Tombol Close (Kembali ke Map)
        TextButton closeBtn = new TextButton("CLOSE", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Kembali ke WorldMapScreen
                game.setScreen(new WorldMapScreen(game));
            }
        });

        // 6. Menyusun Semuanya ke Root
        root.add(title).colspan(2).padBottom(50).row();
        root.add(leftPanel).padRight(50);
        root.add(rightPanel).row();
        root.add(closeBtn).colspan(2).padTop(50).width(200).height(50);

        stage.addActor(root);
    }

    // Helper untuk menambah baris teks stat
    private void addStatRow(Table t, String label, String value, Color valColor, Skin skin) {
        t.add(new Label(label, skin)).width(150);
        Label valLbl = new Label(value, skin);
        valLbl.setColor(valColor);
        t.add(valLbl).row();
    }

    @Override
    public void render(float delta) {
        // Background Gelap
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void dispose() { stage.dispose(); }
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
