package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Entity.UnitStats;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {
    private Main game;
    private Stage stage;

    public GameOverScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. Background Hitam Pekat (atau Merah Gelap)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0f, 0f, 1f); // Merah sangat gelap
        pixmap.fill();
        Image bg = new Image(new Texture(pixmap));
        bg.setFillParent(true);
        stage.addActor(bg);

        // 2. Table Utama
        Table table = new Table();
        table.setFillParent(true);

        // 3. Label "GAME OVER"
        Label title = new Label("GAME OVER", skin);
        title.setFontScale(3.0f); // Besar
        title.setColor(Color.RED);

        Label subtitle = new Label("Your journey ends here...", skin);
        subtitle.setColor(Color.GRAY);

        // 4. Tombol "TRY AGAIN" (Load Last Save)
        TextButton retryBtn = new TextButton("LOAD LAST SAVE", skin);
        retryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Logic: Load data dari Server (Database), lalu masuk Map lagi
                System.out.println("Loading last save...");

                com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                    .loadPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.LoadCallback() {
                        @Override
                        public void onSuccess(UnitStats stats) {
                            game.playerStats = stats; // Restore stats
                            game.setScreen(new WorldMapScreen(game)); // Balik ke Map
                        }

                        @Override
                        public void onFail(String msg) {
                            System.out.println("Load failed: " + msg);
                        }
                    });
            }
        });

        // 5. Tombol "MAIN MENU"
        TextButton menuBtn = new TextButton("RETURN TO TITLE", skin);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Susun Layout
        table.add(title).padBottom(10).row();
        table.add(subtitle).padBottom(50).row();
        table.add(retryBtn).width(250).height(50).padBottom(15).row();
        table.add(menuBtn).width(250).height(50).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
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
