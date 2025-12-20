package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Command.*;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.UI.EffectWidget; // Import Widget Baru
import com.FEA_3.frontend.UI.StatusWidget; // Import Widget Baru
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class BattleScreen implements Screen {
    private Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Music bgm;
    // Units
    private GameUnit hero;
    private GameUnit enemy;
    private Texture heroImg, enemyImg;

    // --- UI COMPONENTS BARU ---
    private StatusWidget playerHpWidget, playerMpWidget;
    private StatusWidget enemyHpWidget, enemyMpWidget;
    private EffectWidget playerEffects, enemyEffects;

    // UI Tombol (Agar bisa di-disable saat giliran musuh)
    private Table commandTable;

    // Logic Variables
    private enum BattleState { PLAYER_TURN, ENEMY_TURN, VICTORY, DEFEAT }
    private BattleState currentState;
    private Runnable onVictoryAction;

    // Layout
    private float uiPanelHeight;
    private float heroX, heroY, enemyX, enemyY;

    public BattleScreen(Main game, String bgPath, EnemyType enemyType, Runnable onVictory) {
        this.game = game;
        this.onVictoryAction = onVictory;

        stage = new Stage(new ScreenViewport());
        ResourceManager.getInstance().loadAssets();

        // 1. Load Background & Asset
        try {
            backgroundTexture = new Texture(Gdx.files.internal(bgPath));
        } catch (Exception e) {
            backgroundTexture = ResourceManager.getInstance().getTexture("Background/bg_forest.png"); // Fallback
        }
        bgm = ResourceManager.getInstance().getMusic("Soundtrack/BattleScreen.mp3");
        bgm.setLooping(true);
        bgm.play();

        // 2. Setup Units
        // Player ambil dari Global Main Data
        hero = new GameUnit(game.playerStats);
        heroImg = ResourceManager.getInstance().getTexture("Entity/Player/Temp.png"); // Ganti path gambar MC Anda
        UnitFactory.loadSkillsForPlayer(hero);

        // Enemy baru tiap battle
        enemy = UnitFactory.createEnemy(enemyType);
        enemyImg = UnitFactory.getEnemyTexture(enemyType);

        currentState = BattleState.PLAYER_TURN;

        setupUI();
    }

    private void setupUI() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = ResourceManager.getInstance().getSkin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // --- 1. SETTING POSISI UNIT (Agar berdiri di atas panel biru) ---
        uiPanelHeight = h * 0.3f; // Tinggi panel UI 30% layar
        float groundLevel = uiPanelHeight + 30; // Sedikit margin di atas panel

        heroX = w * 0.15f;
        heroY = groundLevel;
        enemyX = w * 0.65f;
        enemyY = groundLevel;

        // --- 2. INISIALISASI WIDGET BARU ---

        // A. Widget Player
        playerHpWidget = new StatusWidget(skin, Color.GREEN, false);
        playerMpWidget = new StatusWidget(skin, Color.BLUE, true);
        playerEffects = new EffectWidget(skin);

        // Testing Icon Buff (Nanti dihapus kalau system buff sudah jalan)
        playerEffects.addEffect("Icons/Sword.png", "Attack Up");

        // B. Widget Enemy
        enemyHpWidget = new StatusWidget(skin, Color.RED, false);
        enemyMpWidget = new StatusWidget(skin, Color.MAGENTA, true);
        enemyEffects = new EffectWidget(skin);

        // C. Register Observer (Otomatis update HP saat kena damage)
        hero.addObserver(playerHpWidget);
        enemy.addObserver(enemyHpWidget);

        // D. Update Nilai Awal (HP & MP)
        refreshStatsUI();

        // --- 3. PANEL STATUS KIRI (PLAYER) ---
        Table leftPanel = new Table();
        leftPanel.top().left().pad(15);
        leftPanel.add(new Label(hero.getStats().getName(), skin)).left().padBottom(5).row();
        leftPanel.add(playerHpWidget).left().padBottom(2).row();
        leftPanel.add(playerMpWidget).left().padBottom(5).row();
        leftPanel.add(playerEffects).left().height(30); // Slot Buff

        // --- 4. PANEL STATUS KANAN (ENEMY) ---
        Table rightPanel = new Table();
        rightPanel.top().right().pad(15);
        rightPanel.add(new Label(enemy.getStats().getName(), skin)).right().padBottom(5).row();
        rightPanel.add(enemyHpWidget).right().padBottom(2).row();
        rightPanel.add(enemyMpWidget).right().padBottom(5).row();
        rightPanel.add(enemyEffects).right().height(30); // Slot Debuff

        // --- 5. PANEL TENGAH (TOMBOL COMMAND) ---
        commandTable = new Table();
        // commandTable.debug(); // Uncomment untuk lihat garis layout

        // Buat Tombol
        TextButton btnAttack = createCommandButton("ATTACK", skin);
        TextButton btnSkill = createCommandButton("SKILL", skin);
        TextButton btnDefend = createCommandButton("DEFEND", skin);
        TextButton btnItem = createCommandButton("ITEM", skin);
        TextButton btnEscape = createCommandButton("ESCAPE", skin);

        // Listener Tombol
        btnAttack.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if(currentState == BattleState.PLAYER_TURN) performPlayerTurn(new AttackCommand(hero, enemy));
            }
        });

        btnDefend.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if(currentState == BattleState.PLAYER_TURN) performPlayerTurn(new DefendCommand(hero));
            }
        });

        btnEscape.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if(currentState == BattleState.PLAYER_TURN) {
                    // Kabur balik ke map
                    game.setScreen(new com.FEA_3.frontend.Core.WorldMapScreen(game));
                }
            }
        });

        btnSkill.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                // Hanya bisa klik jika giliran player
                if(currentState == BattleState.PLAYER_TURN) {
                    showSkillSelectionDialog();
                }
            }
        });

        // Susun Layout Tombol (Grid 2 Kolom)
        float btnW = 140;
        float btnH = 40;
        commandTable.add(btnAttack).width(btnW).height(btnH).pad(5);
        commandTable.add(btnDefend).width(btnW).height(btnH).pad(5).row();
        commandTable.add(btnSkill).width(btnW).height(btnH).pad(5);
        commandTable.add(btnEscape).width(btnW).height(btnH).pad(5).row();
        commandTable.add(btnItem).colspan(2).width(btnW).height(btnH).pad(5);

        // --- 6. GABUNGKAN KE PANEL UTAMA (REVISI) ---

        // A. Table Root (Wadah utama fullscreen transparan)
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom(); // Isi tabel akan ditaruh di bawah

        // B. Panel Bawah (Wadah khusus UI Biru)
        Table bottomPanel = new Table();

        // Background Biru HANYA untuk bottomPanel ini
        Pixmap bgMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgMap.setColor(0, 0, 0.5f, 0.85f);
        bgMap.fill();
        bottomPanel.setBackground(new TextureRegionDrawable(new Texture(bgMap)));

        // Masukkan 3 bagian (Kiri, Tengah, Kanan) ke bottomPanel
        // Gunakan .expandX() agar panel mengisi lebar layar secara merata
        bottomPanel.add(leftPanel).width(w * 0.3f).height(uiPanelHeight).top().left();
        bottomPanel.add(commandTable).width(w * 0.4f).height(uiPanelHeight).center();
        bottomPanel.add(rightPanel).width(w * 0.3f).height(uiPanelHeight).top().right();

        // C. Masukkan bottomPanel ke Root
        rootTable.add(bottomPanel).width(w).height(uiPanelHeight);

        stage.addActor(rootTable);
    }


    private void showSkillSelectionDialog() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. Buat Dialog
        Dialog skillDialog = new Dialog("Select Skill", skin);

        // Tombol Close (X) kecil di pojok (Opsional) atau tombol Cancel di bawah

        Table content = skillDialog.getContentTable();
        content.pad(10);

        // 2. Ambil Skill Player (yang Aktif saja, Pasif tidak perlu ditampilkan di menu battle)
        // Kita butuh akses ke list skill hero.
        // Pastikan GameUnit punya method: public List<Skill> getUnlockedSkills()
        List<Skill> skills = hero.getUnlockedSkills();

        boolean hasActiveSkill = false;

        for (final com.FEA_3.frontend.Entity.Skill s : skills) {
            // Filter: Hanya tampilkan Tipe ACTIVE
            if (s.getType() == com.FEA_3.frontend.Entity.Skill.SkillType.PASSIVE) continue;
            hasActiveSkill = true;
            // Cek apakah MP cukup?
            boolean enoughMp = hero.getStats().getCurrentMp() >= s.getManaCost();

            // Buat Tombol untuk Skill ini
            String btnText = s.getName() + " (" + s.getManaCost() + " MP)";
            TextButton skillBtn = new TextButton(btnText, skin);

            if (!enoughMp) {
                skillBtn.setDisabled(true); // Disable visual
                skillBtn.setColor(Color.GRAY); // Gelapkan
            }

            // Listener saat Skill dipilih
            skillBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (enoughMp) {
                        usePlayerSkill(s); // Eksekusi Skill
                        skillDialog.hide(); // Tutup dialog
                    } else {
                        System.out.println("Not enough MP!");
                    }
                }
            });

            // Tambahkan ke layout dialog
            content.add(skillBtn).width(250).height(40).pad(5).row();
        }

        if (!hasActiveSkill) {
            content.add(new Label("No Active Skills learned yet.", skin)).pad(20);
        }

        // Tombol Cancel (Batal milih skill)
        TextButton cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                skillDialog.hide();
            }
        });
        skillDialog.button(cancelBtn);

        skillDialog.show(stage);
    }

    private void usePlayerSkill(com.FEA_3.frontend.Entity.Skill skill) {
        // 1. Kurangi MP
        int manaCost = skill.getManaCost();
        hero.getStats().consumeMana(manaCost);
        // Note: Pastikan UnitStats punya method consumeMana,
        // atau pakai logic: currentMp -= cost;

        // 2. Jalankan Efek Skill
        // Parameter: user = hero, target = enemy
        skill.use(hero, enemy);

        // 3. Feedback Visual (Console / Dialog Kecil)
        System.out.println("Player used " + skill.getName() + "!");

        // Jika skill ini Multi Slice (HP Cost), HP bar juga perlu update
        // Maka kita panggil refreshStatsUI() untuk update Bar HP & MP
        refreshStatsUI();

        // 4. Cek apakah musuh mati akibat skill?
        if (enemy.isDead()) {
            currentState = BattleState.VICTORY;
            showVictoryDialog();
            return;
        }

        // 5. Oper Giliran ke Musuh
        currentState = BattleState.ENEMY_TURN;
        commandTable.setVisible(false);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                performEnemyTurn();
            }
        }, 1.0f);
    }

    // Helper bikin tombol biar codingan rapi
    private TextButton createCommandButton(String text, Skin skin) {
        TextButton btn = new TextButton(text, skin);
        btn.addListener(new SoundListener()); // Bunyi klik
        return btn;
    }

    // Helper update UI manual (terutama MP yang belum otomatis)
    private void refreshStatsUI() {
        UnitStats hs = hero.getStats();
        UnitStats es = enemy.getStats();

        // HP update via Observer, tapi init awal perlu manual display
        playerHpWidget.onHealthChanged(hs.getCurrentHp(), hs.getMaxHp());
        playerMpWidget.updateMp(hs.getCurrentMp(), hs.getMaxMp());

        enemyHpWidget.onHealthChanged(es.getCurrentHp(), es.getMaxHp());
        enemyMpWidget.updateMp(es.getCurrentMp(), es.getMaxMp());
    }

    // Panggil ini di awal giliran Player
    private void startPlayerTurn() {
        currentState = BattleState.PLAYER_TURN;
        commandTable.setVisible(true);

        // 1. Apply Efek (Burn/Bleed damage kena disini)
        hero.applyTurnEffects();

        // 2. Cek Death akibat Burn/Bleed
        if (hero.isDead()) {
            // Game Over Logic
            return;
        }

        // 3. Cek STUN
        if (hero.isStunned()) {
            System.out.println("Player is STUNNED! Skipping turn...");
            hero.setStunned(false); // Reset stun
            // Skip langsung ke musuh
            Timer.schedule(new Timer.Task() {
                @Override public void run() { performEnemyTurn(); }
            }, 1.0f);
        }
    }

    private void performPlayerTurn(Command playerAction) {
        startPlayerTurn();
        playerAction.execute();

        // Update MP bar manual jika aksi menggunakan MP (nanti)
        refreshStatsUI();

        if (enemy.isDead()) {
            currentState = BattleState.VICTORY;
            showVictoryDialog();
            return;
        }

        currentState = BattleState.ENEMY_TURN;

        // Disable tombol saat giliran musuh
        commandTable.setVisible(false); // Atau setTouchable(Disabled)

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                performEnemyTurn();
            }
        }, 1.0f);
    }

    private void performEnemyTurn() {
        // 1. Apply Efek Musuh (Burn/Bleed)
        enemy.applyTurnEffects();

        if (enemy.isDead()) {
            currentState = BattleState.VICTORY;
            showVictoryDialog();
            return;
        }

        // 2. Cek STUN Musuh
        if (enemy.isStunned()) {
            System.out.println("Enemy is STUNNED! Skipping turn...");
            enemy.setStunned(false);
            currentState = BattleState.PLAYER_TURN;
            commandTable.setVisible(true); // Balik ke player
            return;
        }

        // 3. Musuh Menyerang (Jika tidak stun)
        enemy.act(hero);

        if (hero.isDead()) { /* Game Over */ return; }

        // Selesai giliran musuh, balik ke player
        startPlayerTurn();
    }

    // ... method showVictoryDialog sama seperti sebelumnya ...
    private void showVictoryDialog() {
        hero.getStats().addManaCrystals(enemy.getStats().getCrystalReward());
        hero.getStats().addExp(enemy.getStats().getExpReward());
        int expGained = enemy.getStats().getExpReward();
        int crystalGained = enemy.getStats().getCrystalReward();

        // Update Stats Player di Global State
        hero.getStats().addExp(expGained);
        hero.getStats().addManaCrystals(crystalGained);

        // --- TAMBAHAN BARU: AUTO SAVE ---
        System.out.println("Saving Game...");
        // Gunakan ID "User1" dulu untuk testing. Nanti bisa dinamis dari Login screen.
        com.FEA_3.frontend.Utils.NetworkManager.getInstance()
            .savePlayer("User1", hero.getStats());
        // -------------------------------

        Dialog winDialog = new Dialog("VICTORY!", ResourceManager.getInstance().getSkin()) {
            @Override
            protected void result(Object object) {
                if (onVictoryAction != null) onVictoryAction.run();
            }
        };
        // ... text dialog ...
        winDialog.text("You defeated " + enemy.getStats().getName() + "!\n" +
            "Gained " + expGained + " EXP\n" +
            "Found " + crystalGained + " Crystals\n\n" +
            "[Game Saved]"); // Beri tahu player game tersimpan

        winDialog.button("Continue", true);
        winDialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- TAMBAHAN PENTING: UPDATE LOGIC UNIT ---
        if (hero != null) hero.update(delta);
        if (enemy != null) enemy.update(delta);
        // -------------------------------------------

        stage.getBatch().begin();

        // 1. Draw Background
        if(backgroundTexture != null)
            stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // 2. Draw Units (Animasi akan jalan karena timer sudah di-update)
        drawUnit(stage.getBatch(), heroImg, heroX, heroY, hero);
        drawUnit(stage.getBatch(), enemyImg, enemyX, enemyY, enemy);

        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    // Helper draw unit dengan Animasi
    private void drawUnit(com.badlogic.gdx.graphics.g2d.Batch batch, Texture tex, float baseX, float baseY, GameUnit unit) {
        float drawX = baseX;
        float drawY = baseY;

        // Reset warna batch ke putih (normal) dulu
        batch.setColor(Color.WHITE);

        // --- LOGIC ANIMASI BERDASARKAN STATE ---
        // Asumsi: Anda punya method unit.getState() dan unit.getStateTimer() di GameUnit
        // Jika belum ada getter-nya, tambahkan: public UnitState getState() { return state; } di GameUnit

        switch (unit.getState()) {
            case ATTACK:
                // Gunakan Sine wave setengah putaran (0 sampai PI)
                // Timer 0.0s -> 0.5s akan dipetakan menjadi gerakan maju mundur
                float progress = Math.min(1.0f, unit.getStateTimer() / 0.5f); // 0 sampai 1

                // Rumus: Maju (0->50) lalu Mundur (50->0)
                float attackOffset = 100f * (float)Math.sin(progress * Math.PI);

                boolean isPlayer = (unit == hero);
                drawX += isPlayer ? attackOffset : -attackOffset;
                break;

            case HURT:
                // Efek Berkedip Merah
                // Jika waktu genap merah, ganjil putih (efek kedip cepat)
                if ((int)(unit.getStateTimer() * 20) % 2 == 0) {
                    batch.setColor(Color.RED);
                } else {
                    batch.setColor(Color.WHITE);
                }

                // Efek Getar (Shake)
                drawX += Math.random() * 10 - 5;
                break;

            case DEAD:
                batch.setColor(Color.GRAY); // Jadi abu-abu
                drawY -= 10; // Sedikit tenggelam/jatuh
                break;

            case DEFEND:
                batch.setColor(Color.CYAN); // Sedikit biru tanda bertahan
                break;
        }

        // Gambar texture
        // Pastikan arah hadap (Flip) benar. Player hadap kanan (default), Musuh hadap kiri.
        boolean flipX = (unit == enemy);

        batch.draw(tex,
            drawX, drawY,           // Posisi
            100, 100,               // Origin (Pusat putar, opsional)
            200, 200,               // Ukuran lebar x tinggi
            1, 1,                   // Scale
            0,                      // Rotasi
            0, 0,                   // SrcX, SrcY
            tex.getWidth(), tex.getHeight(), // SrcWidth, SrcHeight
            flipX, false            // FlipX (Musuh dibalik), FlipY
        );

        // Kembalikan warna batch ke normal agar UI tidak ikut merah
        batch.setColor(Color.WHITE);
    }

    @Override public void dispose() { stage.dispose(); }
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {bgm.stop();}
}
