package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Main game; // Referensi ke class Main untuk ganti screen
    private Stage stage;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        ResourceManager.getInstance().loadAssets();

        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();
        Table table = new Table();
        table.setFillParent(true); // Tengah layar

        // Judul (Sementara pakai Button style label atau Label biasa)
        TextButton title = new TextButton("FATE / EMERALD ABYSS", skin);
        title.setDisabled(true); // Biar gak bisa diklik, cuma buat judul

        // Tombol Start
        TextButton startBtn = new TextButton("START GAME", skin);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Pindah ke Layar Narasi (Cerita), bukan langsung Battle
                game.setScreen(new NarrativeScreen(game));
            }
        });

        // Susun Layout
        table.add(title).padBottom(50).row();
        table.add(startBtn).width(200).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0, 0, 1); // Merah gelap
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    // Method dispose dll wajib ada
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
