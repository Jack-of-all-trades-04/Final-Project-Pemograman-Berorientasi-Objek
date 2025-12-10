package com.FEA_3.frontend.Core;

// Import Backend & Entity
import com.FEA_3.frontend.Utils.NetworkManager;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Entity.GameUnit;

import com.FEA_3.frontend.Patterns.Command.AttackCommand;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.UI.HealthBar;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private TextButton attackBtn;

    // --- TAMBAHAN UNTUK LOADING STATE ---
    private boolean isLoading = true; // Status: Apakah sedang ambil data?
    private Label loadingLabel;       // Tulisan "Connecting..."

    public BattleScreen() {
        batch = new SpriteBatch();
        ResourceManager.getInstance().loadAssets();

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
        Gdx.input.setInputProcessor(stage); // Pastikan input processor di-set ulang

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        heroX = w * 0.1f; heroY = h * 0.4f;
        enemyX = w * 0.6f; enemyY = h * 0.4f;

        // --- HP BAR ---
        HealthBar heroBar = new HealthBar(150, 20);
        heroBar.setPosition(heroX, heroY + 160);
        hero.addObserver(heroBar); // Aman dipanggil karena hero sudah tidak null
        stage.addActor(heroBar);

        HealthBar enemyBar = new HealthBar(150, 20);
        enemyBar.setPosition(enemyX, enemyY + 160);
        enemy.addObserver(enemyBar);
        stage.addActor(enemyBar);

        // --- TOMBOL ---
        Skin skin = ResourceManager.getInstance().getSkin();
        attackBtn = new TextButton("ATTACK TURN", skin);
        attackBtn.setPosition(100, 100);
        attackBtn.setSize(200, 50);

        attackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState == BattleState.PLAYER_TURN) {
                    performPlayerTurn();
                }
            }
        });

        stage.addActor(attackBtn);
    }

    // ... (performPlayerTurn & performEnemyTurn SAMA PERSIS dengan kode lama Anda) ...
    private void performPlayerTurn() {
        Command attack = new AttackCommand(hero, enemy);
        attack.execute();

        if (enemy.isDead()) {
            System.out.println("VICTORY!");
            currentState = BattleState.VICTORY;
            return;
        }

        currentState = BattleState.ENEMY_TURN;
        attackBtn.setDisabled(true);
        attackBtn.setText("ENEMY TURN...");

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
        attackBtn.setDisabled(false);
        attackBtn.setText("ATTACK TURN");
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
        drawUnit(hero, heroImg, heroX, heroY);
        drawUnit(enemy, enemyImg, enemyX, enemyY);
        batch.end();

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
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
