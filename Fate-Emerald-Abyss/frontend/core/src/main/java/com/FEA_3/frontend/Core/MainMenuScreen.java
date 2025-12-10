package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Main game; // Referensi ke class Main untuk ganti screen
    private Stage stage;
    private Texture backgroundTexture;
    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        ResourceManager.getInstance().loadAssets();
        backgroundTexture = ResourceManager.getInstance().getTexture("Background/MainMenu.png");
        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();
        Table table = new Table();
        table.setFillParent(true);

        // Tombol Start
        Label startBtn = new Label("Click Screen to Play", skin);
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Pindah ke Layar Narasi (Cerita), bukan langsung Battle
                game.setScreen(new NarrativeScreen(game));
            }
        });

        // Susun Layout
        table.bottom();
        table.add(startBtn).padBottom(Gdx.graphics.getHeight() / 64f);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0, 0, 1); // Merah gelap
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
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
