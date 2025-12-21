// java
package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Main game;
    private final Screen returnScreen;
    private final Stage stage;

    public SettingsScreen(Main game, Screen returnScreen) {
        this.game = game;
        this.returnScreen = returnScreen;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Skin skin = ResourceManager.getInstance().getSkin();
        Table root = new Table();
        root.setFillParent(true);
        root.defaults().pad(10);

        Label title = new Label("Settings", skin);
        title.setFontScale(1.2f);

        // --- BGM SLIDER ---
        Slider bgmSlider = new Slider(0f, 1f, 0.05f, false, skin);
        // Ambil nilai awal dari Global Settings
        bgmSlider.setValue(game.settings.getBgmVolume());

        bgmSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float v = bgmSlider.getValue();

                // 1. Update Global Settings Object
                game.settings.setBgmVolume(v);

                // 2. Simpan ke File (Lewat Manager agar konsisten "FEA_Settings")
                SettingsManager.save(game.settings);

                // 3. Update Musik yang SEDANG main sekarang (Realtime)
                ResourceManager.getInstance().setGlobalMusicVolume(v);
            }
        });

        // --- SFX SLIDER ---
        Slider sfxSlider = new Slider(0f, 1f, 0.05f, false, skin);
        sfxSlider.setValue(game.settings.getSfxVolume());

        sfxSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float v = sfxSlider.getValue();
                game.settings.setSfxVolume(v);
                SettingsManager.save(game.settings);

                // TAMBAHAN: Update ResourceManager agar SoundListener tahu volume baru
                ResourceManager.getInstance().setGlobalSfxVolume(v);
            }
        });

        Label bgmLbl = new Label("BGM Volume", skin);
        Label sfxLbl = new Label("SFX Volume", skin);

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(returnScreen);
            }
        });

        root.add(title).colspan(2).padBottom(20).row();
        root.add(bgmLbl).left();
        root.add(bgmSlider).width(250).row();
        root.add(sfxLbl).left();
        root.add(sfxSlider).width(250).row();
        root.add(backBtn).colspan(2).padTop(20);

        stage.addActor(root);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
