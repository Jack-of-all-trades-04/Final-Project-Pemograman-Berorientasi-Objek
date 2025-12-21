package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private Texture backgroundTexture;
    private com.badlogic.gdx.audio.Music bgm;

    private Table menuTable;
    private Label clickToPlayLbl;
    private Image overlay;

    private float stateTime = 0f;

    private enum MenuState { PRESS_TO_START, SHOW_MENU }
    private MenuState currentState = MenuState.PRESS_TO_START;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        ResourceManager.getInstance().loadAssets();
        bgm = ResourceManager.getInstance().getMusic("Soundtrack/MainMenu.mp3");
        bgm.setLooping(true);
        bgm.play();

        try {
            backgroundTexture = new Texture(Gdx.files.internal("Background/MainMenu.png"));
        } catch (Exception e) {
            backgroundTexture = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");
        }

        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        overlay = new Image(new Texture(pixmap));
        overlay.setFillParent(true);
        overlay.setVisible(false);
        overlay.getColor().a = 0f;

        clickToPlayLbl = new Label("- Click Screen to Play -", skin);
        clickToPlayLbl.setAlignment(Align.center);
        Table labelTable = new Table();
        labelTable.setFillParent(true);
        labelTable.bottom();
        labelTable.add(clickToPlayLbl).padBottom(10);

        menuTable = new Table();
        menuTable.setFillParent(true);
        menuTable.setVisible(false);

        TextButton btnNewGame = createMenuButton("NEW GAME", skin, () -> {
            com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                .resetPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.ResetCallback() {
                    @Override public void onSuccess() {
                        com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                            .loadPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.LoadCallback() {
                                @Override public void onSuccess(UnitStats stats) {
                                    game.playerStats = stats;
                                    game.setScreen(new NarrativeScreen(game, 1));
                                }
                                @Override public void onFail(String msg) {
                                    System.err.println("Error creating new game: " + msg);
                                }
                            });
                    }
                    @Override public void onFail(String msg) {
                        System.err.println("Failed to delete save data. Connection error?");
                    }
                });
        });

        TextButton btnLoadGame = createMenuButton("LOAD GAME", skin, () -> {
            com.FEA_3.frontend.Utils.NetworkManager.getInstance()
                .loadPlayer("User1", new com.FEA_3.frontend.Utils.NetworkManager.LoadCallback() {
                    @Override public void onSuccess(UnitStats stats) {
                        game.playerStats = stats;
                        game.setScreen(new WorldMapScreen(game));
                    }
                    @Override public void onFail(String msg) {
                        System.err.println("Load Failed: " + msg);
                    }
                });
        });

        TextButton btnSettings = createMenuButton("SETTINGS", skin, () -> {
            game.setScreen(new SettingsScreen(game, this));
        });

        TextButton btnCredits = createMenuButton("CREDITS", skin, () -> {
            game.setScreen(new CreditsScreen(game, this));
        });

        TextButton btnQuit = createMenuButton("QUIT GAME", skin, () -> Gdx.app.exit());

        float btnWidth = 250f, btnHeight = 50f, pad = 10f;
        menuTable.add(btnNewGame).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnLoadGame).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnSettings).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnCredits).width(btnWidth).height(btnHeight).pad(pad).row();
        menuTable.add(btnQuit).width(btnWidth).height(btnHeight).pad(pad).row();

        ClickListener initialClickListener = new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentState == MenuState.PRESS_TO_START) {
                    transitionToMenu();
                    stage.removeListener(this);
                }
            }
        };
        stage.addListener(initialClickListener);

        stage.addActor(overlay);
        stage.addActor(labelTable);
        stage.addActor(menuTable);
    }

    private TextButton createMenuButton(String text, Skin skin, Runnable action) {
        TextButton btn = new TextButton(text, skin);
        btn.addListener(new SoundListener());
        btn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { action.run(); }
        });
        return btn;
    }

    private void transitionToMenu() {
        currentState = MenuState.SHOW_MENU;
        clickToPlayLbl.addAction(Actions.fadeOut(0.5f));
        overlay.setVisible(true);
        overlay.addAction(Actions.fadeIn(0.5f));
        menuTable.setVisible(true);
        menuTable.getColor().a = 0f;
        menuTable.setPosition(0, -50);
        menuTable.addAction(Actions.parallel(
            Actions.fadeIn(0.5f),
            Actions.moveTo(0, 0, 0.5f, com.badlogic.gdx.math.Interpolation.swingOut)
        ));
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += delta;
        if (currentState == MenuState.PRESS_TO_START) {
            float alpha = (MathUtils.sin(stateTime * 5f) + 1) / 2f;
            clickToPlayLbl.setColor(1, 1, 1, alpha);
        }
        stage.getBatch().begin();
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
    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { bgm.stop(); }
}
