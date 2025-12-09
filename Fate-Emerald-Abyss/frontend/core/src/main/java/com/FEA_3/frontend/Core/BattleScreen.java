package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Patterns.Command.AttackCommand;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.Patterns.Strategy.AggressiveStrategy;
import com.FEA_3.frontend.UI.HealthBar;
import com.FEA_3.frontend.Utils.GameUnit;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BattleScreen implements Screen {
    private SpriteBatch batch;
    private Texture heroImg, enemyImg;
    private Stage stage; // Wadah untuk UI
    private float heroX, heroY;
    private float enemyX, enemyY;
    private GameUnit hero, enemy;
    private BattleState currentState; // STATE PATTERN
    private TextButton attackBtn;     // Kita butuh akses ke tombol ini global di class

    public BattleScreen() {
        batch = new SpriteBatch();
        ResourceManager.getInstance().loadAssets();

        // LOAD GAMBAR
        heroImg = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png");

        // --- PERUBAHAN DISINI (FACTORY METHOD) ---
        // Kita tentukan mau lawan apa. Misal: SKELETON
        EnemyType currentEnemyType = EnemyType.SKELETON;

        // 1. Minta Pabrik buatkan Musuh
        enemy = UnitFactory.createEnemy(currentEnemyType);

        // 2. Minta Pabrik ambilkan Gambar yang cocok
        enemyImg = UnitFactory.getEnemyTexture(currentEnemyType);

        // Setup Hero tetap sama (atau bisa dibuatkan HeroFactory juga nanti)
        hero = new GameUnit("Artoria", 1000, 50);

        currentState = BattleState.PLAYER_TURN;
        setupUI();
    }

    private void setupUI() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Hitung posisi responsif (supaya bar pas di atas kepala)
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        heroX = w * 0.1f;
        heroY = h * 0.4f;

        enemyX = w * 0.6f;
        enemyY = h * 0.4f;

        // --- SETUP HP BAR HERO ---
        HealthBar heroBar = new HealthBar(150, 20); // Lebar 150, Tinggi 20
        heroBar.setPosition(heroX, heroY + 160);    // Taruh sedikit di atas gambar karakter
        hero.addObserver(heroBar);                  // PENTING: Sambungkan Observer
        stage.addActor(heroBar);

        // --- SETUP HP BAR ENEMY ---
        HealthBar enemyBar = new HealthBar(150, 20);
        enemyBar.setPosition(enemyX, enemyY + 160);
        enemy.addObserver(enemyBar);                // PENTING: Sambungkan Observer
        stage.addActor(enemyBar);

        // --- TOMBOL ATTACK ---
        Skin skin = ResourceManager.getInstance().getSkin();
        attackBtn = new TextButton("ATTACK TURN", skin); // Hapus 'TextButton' di depan agar pakai variabel global
        attackBtn.setPosition(100, 100);
        attackBtn.setSize(200, 50);

        attackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cek State: Hanya boleh klik kalau giliran Player
                if (currentState == BattleState.PLAYER_TURN) {
                    performPlayerTurn();
                }
            }
        });

        stage.addActor(attackBtn);
    }

    private void performPlayerTurn() {
        // 1. Player Nyerang
        Command attack = new AttackCommand(hero, enemy);
        attack.execute();

        // 2. Cek apakah musuh mati?
        if (enemy.isDead()) {
            System.out.println("VICTORY!");
            currentState = BattleState.VICTORY;
            return;
        }

        // 3. Ganti State ke Musuh & Matikan Tombol
        currentState = BattleState.ENEMY_TURN;
        attackBtn.setDisabled(true);
        attackBtn.setText("ENEMY TURN..."); // Ubah teks biar jelas

        // 4. Beri jeda 1 detik sebelum musuh nyerang (Biar gak instan)
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                performEnemyTurn();
            }
        }, 1.0f); // Delay 1 detik
    }

    private void performEnemyTurn() {
        // 1. Musuh Beraksi pakai Strategy
        enemy.act(hero);

        // 2. Cek apakah Hero mati?
        if (hero.isDead()) {
            System.out.println("GAME OVER");
            currentState = BattleState.DEFEAT;
            return;
        }

        // 3. Balikkan ke Player
        currentState = BattleState.PLAYER_TURN;
        attackBtn.setDisabled(false);
        attackBtn.setText("ATTACK TURN");
    }

    @Override
    public void render(float delta) {
        // 1. Update Timer State setiap frame
        hero.update(delta);
        enemy.update(delta);

        // Bersihkan layar
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update UI
        stage.act(delta);

        batch.begin();

        // --- GAMBAR HERO ---
        drawUnit(hero, heroImg, heroX, heroY);

        // --- GAMBAR ENEMY ---
        drawUnit(enemy, enemyImg, enemyX, enemyY);

        batch.end();

        stage.draw();
    }

    // Method Helper biar rapi
    private void drawUnit(GameUnit unit, Texture tex, float originalX, float originalY) {
        float drawX = originalX;
        float drawY = originalY;

        // LOGIKA STATE VISUAL
        if (unit.getState() == UnitState.ATTACK) {
            // Maju ke depan (Arah tergantung siapa).
            // Kalau Hero (kiri) maju ke kanan (+), Enemy (kanan) maju ke kiri (-)
            // Cara gampang deteksi: cek nama atau posisi X
            if (originalX < Gdx.graphics.getWidth() / 2) {
                drawX += 50; // Hero maju ke kanan
            } else {
                drawX -= 50; // Enemy maju ke kiri
            }
        }
        else if (unit.getState() == UnitState.HURT) {
            // Ubah warna jadi Merah
            batch.setColor(1, 0, 0, 1);
            // Efek getar (Shake) acak dikit
            drawX += (Math.random() * 10) - 5;
            drawY += (Math.random() * 10) - 5;
        }

        // Gambar
        batch.draw(tex, drawX, drawY, 150, 150);

        // PENTING: Reset warna batch ke putih (normal) setelah gambar
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        ResourceManager.getInstance().dispose();
    }

    // Method boilerplate lain biarkan kosong dulu
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
