package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Factory.UIFactory;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class NarrativeScreen implements Screen {
    private Main game;
    private Stage stage;
    private Label nameLabel;
    private Label dialogLabel;

    // Data Cerita Sederhana (Array)
    private String[] script = {
        "Commander: 'Dimana... aku?'",
        "Artoria: 'Master! Akhirnya Anda sadar.'",
        "Commander: 'Artoria? Kenapa kita ada di hutan ini?'",
        "Artoria: 'Hati-hati Master! Saya merasakan hawa membunuh...'",
        "Artoria: 'Musuh datang! Bersiaplah!'"
    };
    private int scriptIndex = 0;

    // Asset Gambar Karakter untuk VN
    private Texture characterImg;
    private Texture backgroundTexture;
    private Music bgm;

    public NarrativeScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        // Ambil musik tapi JANGAN di-play di constructor
        bgm = ResourceManager.getInstance().getMusic("Audio/Music/Battle_Music.wav");

        // Setting agar musik mengulang (Looping)
        bgm.setLooping(true);
        bgm.setVolume(0.5f); // Volume 50%

        characterImg = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");

        // PERUBAHAN DISINI: Ambil texture background dari Resource Manager
        backgroundTexture = ResourceManager.getInstance().getTexture("Background/Temps.png");

        setupUI();
        updateText();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // 1. Buat Container Tabel Utama
        Table dialogBox = UIFactory.createDialogBox(skin);
        dialogBox.setSize(w - 40, h * 0.3f);
        dialogBox.setPosition(20, 20);

        // PENTING: Setting alignment tabel agar isi mulai dari kiri atas
        dialogBox.top().left().pad(20);

        // 2. Setup Label Nama
        nameLabel = new Label("Name", skin); // Jangan pakai UIFactory dulu biar wrap-nya mati
        nameLabel.setColor(Color.GOLD);
        nameLabel.setWrap(false); // PENTING: Matikan wrap agar nama tidak vertikal!

        // 3. Setup Label Dialog
        dialogLabel = new Label("...", skin);
        dialogLabel.setColor(Color.WHITE);
        dialogLabel.setWrap(true); // Dialog boleh wrap (turun baris kalau panjang)

        // 4. Masukkan ke Tabel dengan layout yang benar
        // Baris 1: Nama
        dialogBox.add(nameLabel).left().padBottom(10).row();

        // Baris 2: Dialog (Expand X agar memenuhi lebar)
        dialogBox.add(dialogLabel).left().width(w - 80).expandX();

        // Listener klik layar
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                advanceStory();
            }
        });

        stage.addActor(dialogBox);
    }

    private void advanceStory() {
        scriptIndex++;
        if (scriptIndex >= script.length) {
            // CERITA HABIS -> PINDAH KE BATTLE
            game.setScreen(new BattleScreen("", EnemyType.SKELETON));
            // Note: BattleScreen tidak butuh 'game' di constructor sebelumnya,
            // tapi kalau mau balik ke menu nanti, BattleScreen perlu refactor dikit.
        } else {
            updateText();
        }
    }

    private void updateText() {
        String line = script[scriptIndex];
        // Format script simpel: "Nama: 'Isi teks'"
        // Kita pecah string-nya
        String[] parts = line.split(": ", 2); // Pisah di tanda ": " pertama

        if (parts.length == 2) {
            nameLabel.setText(parts[0]);
            dialogLabel.setText(parts[1]);
        } else {
            nameLabel.setText("???");
            dialogLabel.setText(line);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Background Hitam (atau kasih gambar hutan nanti)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Gambar Karakter (Layer tengah, di atas background)
        if (script[scriptIndex].startsWith("Artoria")) {
            stage.getBatch().draw(characterImg, Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight()/2 - 100, 200, 200);
        }
        stage.getBatch().end();

        // Update dan Gambar UI (Kotak Dialog) PALING TERAKHIR (Layer teratas)
        stage.act(delta);
        stage.draw();
    }

    // Boilerplate standard
    @Override public void show() {
        if (bgm != null) bgm.play();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        if (bgm != null) bgm.stop();
    }
    @Override public void dispose() { stage.dispose(); }
}
