package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Patterns.Command.DefendCommand;
import com.FEA_3.frontend.Utils.NetworkManager;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Patterns.Command.AttackCommand;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.UI.HealthBar;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BattleScreen implements Screen {
    private SpriteBatch batch;
    private Texture heroImg, enemyImg;
    private Stage stage;
    private float heroX, heroY;
    private float enemyX, enemyY;
    private GameUnit hero, enemy;
    private BattleState currentState;
    private TextButton btnAttack, btnSkill, btnItem, btnDefend, btnRun;
    private Texture backgroundTexture;
    private Music bgm;
    private float uiPanelHeight;

    // --- TAMBAHAN UNTUK LOADING STATE ---
    private boolean isLoading = true; // Status: Apakah sedang ambil data?
    private Label loadingLabel;       // Tulisan "Connecting..."

    public BattleScreen(String bgPath, EnemyType enemyType) {
        batch = new SpriteBatch();
        ResourceManager.getInstance().loadAssets();
        bgm = ResourceManager.getInstance().getMusic("Audio/Music/Battle_Music.wav");

        // Setting agar musik mengulang (Looping)
        bgm.setLooping(true);
        bgm.setVolume(0.5f); // Volume 50%

        // 1. Load Background Dinamis
        // Pastikan Anda sudah meload ini di ResourceManager atau load manual jika belum
        // Untuk aman, kita load manual via manager wrapper atau direct texture (untuk test)
        try {
            backgroundTexture = new Texture(Gdx.files.internal(bgPath));
        } catch (Exception e) {
            // Fallback jika gambar gak ada
            backgroundTexture = ResourceManager.getInstance().getTexture("Background/Temps.png");
        }

        // 2. Setup Musuh Dinamis
        enemy = UnitFactory.createEnemy(enemyType);
        enemyImg = UnitFactory.getEnemyTexture(enemyType);

        // 1. Load Gambar dulu (Aset lokal aman diload langsung)
        heroImg = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");

        // Setup Stage Sederhana untuk Loading Text
        stage = new Stage(new ScreenViewport());
        Skin skin = ResourceManager.getInstance().getSkin();
        loadingLabel = new Label("Connecting to Server...", skin);
        loadingLabel.setPosition(Gdx.graphics.getWidth()/2f - 50, Gdx.graphics.getHeight()/2f);
        stage.addActor(loadingLabel);

        // 2. PANGGIL BACKEND
        // Kita minta data user dengan ID "Commander" (bisa diganti nanti)
        NetworkManager.getInstance().loadPlayerData("Commander", new NetworkManager.DataCallback() {
            @Override
            public void onSuccess(UnitStats stats) {
                // DATA DATANG! Hore!
                System.out.println("Data loaded: " + stats.getName());

                // Inisialisasi Hero dengan data dari Server
                // ASUMSI: Anda sudah refactor GameUnit menerima UnitStats.
                // Jika belum, pakai: new GameUnit(stats.getName(), stats.getMaxHp(), stats.getAttackPower());
                hero = new GameUnit(stats);

                // Lanjut inisialisasi musuh & UI
                initGameLogic();

                // Matikan loading
                isLoading = false;
                loadingLabel.remove(); // Hapus tulisan loading
            }

            @Override
            public void onFail(Throwable t) {
                // INTERNET MATI / SERVER ERROR
                System.out.println("Gagal konek: " + t.getMessage());

                // Fallback: Pakai data offline biar game gak crash
                UnitStats offlineStats = new UnitStats("Offline Hero", 500, 10);
                hero = new GameUnit(offlineStats);

                initGameLogic();
                isLoading = false;
                loadingLabel.remove();
            }
        });
    }

    private void showVictoryDialog() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. Ambil Hadiah dari Stats Musuh
        // (Asumsi Anda sudah buat getter getStats() di GameUnit.java)
        // Jika belum, tambahkan public UnitStats getStats() { return stats; } di GameUnit
        int expGained = enemy.getStats().getExpReward();
        int crystalGained = enemy.getStats().getCrystalReward();

        // 2. Berikan ke Player
        hero.getStats().addExp(expGained);
        hero.getStats().addManaCrystals(crystalGained);

        // TODO: Panggil NetworkManager.savePlayer(...) disini nanti agar tersimpan ke Database

        // 3. Buat Jendela Dialog Pop-up
        Dialog winDialog = new Dialog("VICTORY!", skin) {
            @Override
            protected void result(Object object) {
                // Apa yang terjadi saat tombol diklik?
                // Kembali ke Main Menu (atau NarrativeScreen)
                // Karena kita belum punya 'Screen Manager' canggih, kita hardcode dulu:
                // Gdx.app.exit(); // Keluar game (Jangan pakai ini nanti)

                // Kembali ke Menu Utama
                // (Anda butuh referensi ke 'game' main class, atau lakukan transisi manual)
                // Contoh sementara: System.out.println("Kembali ke menu...");
            }
        };

        // Isi Teks Dialog
        winDialog.text("You defeated " + enemy.getStats().getName() + "!\n\n" +
            "Gained EXP: " + expGained + "\n" +
            "Mana Crystals: " + crystalGained);

        // Tombol OK
        winDialog.button("Continue", true);

        // Tampilkan di tengah layar
        winDialog.show(stage);
    }

    // Method baru: Dipanggil HANYA setelah data hero siap
    private void initGameLogic() {
        // --- SETUP MUSUH (FACTORY) ---
        EnemyType currentEnemyType = EnemyType.SKELETON;
        enemy = UnitFactory.createEnemy(currentEnemyType);
        enemyImg = UnitFactory.getEnemyTexture(currentEnemyType);

        currentState = BattleState.PLAYER_TURN;

        // Panggil setup UI yang asli
        setupUI();
    }

    private void setupUI() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = ResourceManager.getInstance().getSkin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // 1. Container Utama (Panel Bawah)
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom(); // Tempel di bawah layar
        // --- 1. TENTUKAN TINGGI PANEL UI (PENTING) ---
        // Misal kita ingin panel bawah tingginya 30% dari layar
        uiPanelHeight = h * 0.3f;

        // --- 2. SETUP POSISI UNIT (Agar di atas panel) ---
        // X tetap sama
        heroX = w * 0.1f;
        enemyX = w * 0.6f;

        // Y HARUS DI ATAS PANEL
        // Kita taruh di ketinggian panel + sedikit margin (misal 50 pixel)
        float unitBaseY = uiPanelHeight + 50;

        heroY = unitBaseY;
        enemyY = unitBaseY;

        if (hero != null) { // Cek null safety
            HealthBar heroBar = new HealthBar(150, 20);
            // Taruh 160 pixel di atas posisi kaki karakter
            heroBar.setPosition(heroX, heroY + 160);
            hero.addObserver(heroBar);
            stage.addActor(heroBar);
        }

        if (enemy != null) {
            HealthBar enemyBar = new HealthBar(150, 20);
            enemyBar.setPosition(enemyX, enemyY + 160);
            enemy.addObserver(enemyBar);
            stage.addActor(enemyBar);
        }

        // --- SETUP PANEL UI BAWAH ---
        mainTable.setFillParent(true);
        mainTable.bottom(); // Tempel di bawah

        // Background Biru Transparan
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0.5f, 0.8f);
        bgPixmap.fill();
        TextureRegionDrawable panelBg = new TextureRegionDrawable(new Texture(bgPixmap));

        Table commandTable = new Table();
        commandTable.setBackground(panelBg);

        // 2. Buat Background Panel (Warna Biru Transparan ala FF)
        // Kita pakai trik Pixmap agar tidak perlu aset gambar baru
        bgPixmap.setColor(0, 0, 0.5f, 0.8f); // Biru Gelap Transparan
        bgPixmap.fill();

        // Tabel untuk Menu Perintah
        commandTable.setBackground(panelBg); // Set background biru

        // 3. Buat Tombol-Tombol
        btnAttack = new TextButton("Attack", skin);
        btnSkill = new TextButton("Skill", skin);
        btnItem = new TextButton("Item", skin); // Consumable
        btnDefend = new TextButton("Defend", skin);
        btnRun = new TextButton("Escape", skin);
        btnAttack.addListener(new SoundListener());
        btnSkill.addListener(new SoundListener());
        btnItem.addListener(new SoundListener());
        btnDefend.addListener(new SoundListener());
        btnRun.addListener(new SoundListener());
        // Atur Listener Tombol
        btnAttack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState == BattleState.PLAYER_TURN) performPlayerTurn(new AttackCommand(hero, enemy));
            }
        });

        btnDefend.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState == BattleState.PLAYER_TURN) performPlayerTurn(new DefendCommand(hero));
            }
        });

        btnRun.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState == BattleState.PLAYER_TURN) {
                    System.out.println("Run Away!");
                    // Logic keluar screen: game.setScreen(new MainMenuScreen(game));
                }
            }
        });

        // 4. Susun Layout Grid (2 Kolom)
        // Kiri: Attack, Skill, Item. Kanan: Defend, Escape
        commandTable.add(btnAttack).width(150).pad(5);
        commandTable.add(btnDefend).width(150).pad(5).row();

        commandTable.add(btnSkill).width(150).pad(5);
        commandTable.add(btnRun).width(150).pad(5).row();

        commandTable.add(btnItem).width(150).pad(5).colspan(2).left(); // Item di bawah

        // Masukkan CommandTable ke MainTable (Tinggi 30% layar)
        mainTable.add(commandTable).width(w).height(uiPanelHeight);

        stage.addActor(mainTable);
    }

    // ... (performPlayerTurn & performEnemyTurn SAMA PERSIS dengan kode lama Anda) ...
    private void performPlayerTurn(Command playerAction) {
        // 1. Eksekusi aksi pilihan player (Attack/Defend/dll)
        playerAction.execute();

        // 2. Cek Win Condition (Jika aksi adalah attack)
        if (enemy.isDead()) {
            System.out.println("VICTORY!");
            currentState = BattleState.VICTORY;

            // PANGGIL DIALOG KEMENANGAN
            showVictoryDialog();

            return; // Stop code, jangan lanjut ke giliran musuh
        }

        // 3. Ganti Giliran
        currentState = BattleState.ENEMY_TURN;
        btnAttack.setDisabled(true);
        btnItem.setDisabled(true);
        btnDefend.setDisabled(true);
        btnRun.setDisabled(true);
        btnSkill.setDisabled(true);
        btnAttack.setText("ENEMY TURN...");

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                performEnemyTurn();
            }
        }, 1.0f);
    }

    private void performEnemyTurn() {
        enemy.act(hero);

        if (hero.isDead()) {
            System.out.println("GAME OVER");
            currentState = BattleState.DEFEAT;
            return;
        }

        currentState = BattleState.PLAYER_TURN;
        btnAttack.setDisabled(false);
        btnItem.setDisabled(false);
        btnDefend.setDisabled(false);
        btnRun.setDisabled(false);
        btnSkill.setDisabled(false);
        btnAttack.setText("ATTACK TURN");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // LOGIC KHUSUS SAAT LOADING
        if (isLoading) {
            stage.act(delta);
            stage.draw(); // Hanya gambar tulisan "Connecting..."
            return;       // STOP DISINI, jangan gambar unit dulu (karena 'hero' masih null)
        }

        // --- GAMEPLAY NORMAL ---
        hero.update(delta);
        enemy.update(delta);

        stage.act(delta);

        batch.begin();
        // Gambar Background Layar (Hutan/dll)
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // Gambar Unit di posisi yang sudah diupdate
        if (hero != null) drawUnit(hero, heroImg, heroX, heroY);
        if (enemy != null) drawUnit(enemy, enemyImg, enemyX, enemyY);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    // ... (drawUnit, dispose, dll SAMA PERSIS dengan kode lama Anda) ...
    private void drawUnit(GameUnit unit, Texture tex, float originalX, float originalY) {
        // Copy-paste method drawUnit Anda yang lama disini
        // ...
        float drawX = originalX;
        float drawY = originalY;

        if (unit.getState() == com.FEA_3.frontend.Core.UnitState.ATTACK) {
            if (originalX < Gdx.graphics.getWidth() / 2) drawX += 50;
            else drawX -= 50;
        } else if (unit.getState() == com.FEA_3.frontend.Core.UnitState.HURT) {
            batch.setColor(1, 0, 0, 1);
            drawX += (Math.random() * 10) - 5;
            drawY += (Math.random() * 10) - 5;
        }

        batch.draw(tex, drawX, drawY, 150, 150);
        batch.setColor(1, 1, 1, 1);
    }

    @Override public void dispose() {
        batch.dispose();
        stage.dispose();
        ResourceManager.getInstance().dispose();
    }
    @Override public void show() {
        if (bgm != null) bgm.play();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        if (bgm != null) bgm.stop();
    }
}
