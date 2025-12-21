package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.*;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Patterns.Command.*;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.FEA_3.frontend.UI.BattleUI;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;

public class BattleScreen implements Screen {
    private Main game;
    private Stage stage;
    private Music bgm;

    // Core Objects
    private GameUnit hero;
    private GameUnit enemy;
    private BattleRenderer renderer;
    private BattleUI ui;

    private enum BattleState { PLAYER_TURN, ENEMY_TURN, VICTORY, DEFEAT }
    private BattleState currentState;
    private Runnable onVictoryAction;

    public BattleScreen(Main game, String bgPath, EnemyType enemyType, Runnable onVictory) {
        this.game = game;
        this.onVictoryAction = onVictory;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        ResourceManager.getInstance().loadAssets();

        // 1. Setup Units
        hero = new GameUnit(game.playerStats);
        UnitFactory.loadSkillsForPlayer(hero);
        enemy = UnitFactory.createEnemy(enemyType, game.playerStats);

        // 2. Setup Sub-Systems (Renderer & UI)
        renderer = new BattleRenderer(stage, bgPath, enemyType, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui = new BattleUI(stage, this, hero, enemy);

        // 3. Setup Observers (Damage Logic -> Renderer)
        setupObservers();

        // 4. Start Music
        bgm = ResourceManager.getInstance().getMusic("Soundtrack/BattleScreen.mp3");
        bgm.setLooping(true);
        bgm.play();

        currentState = BattleState.PLAYER_TURN;
        startPlayerTurn();
    }

    private void setupObservers() {
        // Hero Observer
        hero.addObserver(new UnitObserver() {
            @Override public void onHealthChanged(int c, int m) { ui.refreshStats(hero, enemy); }
            @Override public void onDamageTaken(int amt, boolean crit) {
                renderer.spawnDamageNumber(amt, crit, true); // true = target player
            }
        });

        // Enemy Observer
        enemy.addObserver(new UnitObserver() {
            @Override public void onHealthChanged(int c, int m) { ui.refreshStats(hero, enemy); }
            @Override public void onDamageTaken(int amt, boolean crit) {
                renderer.spawnDamageNumber(amt, crit, false); // false = target enemy
            }
        });
    }

    // --- GAME LOOP ---
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (hero != null) hero.update(delta);
        if (enemy != null) enemy.update(delta);

        // Renderer menggambar Background & Unit
        renderer.render(stage.getBatch(), hero, enemy);

        // Stage menggambar UI & Efek
        stage.act(delta);
        stage.draw();
    }

    // --- INPUT HANDLERS (Dipanggil oleh BattleUI) ---

    public void onAttackClicked() {
        if(isPlayerTurn()) performPlayerAction(new AttackCommand(hero, enemy));
    }

    public void onDefendClicked() {
        if(isPlayerTurn()) performPlayerAction(new DefendCommand(hero));
    }

    public void onEscapeClicked() {
        if(isPlayerTurn()) game.setScreen(new WorldMapScreen(game));
    }

    public void onSkillSelected(Skill s) {
        if(isPlayerTurn()) {
            // Kita bungkus logic skill player ke dalam Anonymous Class Command
            // agar bisa punya getDescription()
            performPlayerAction(new Command() {
                @Override
                public void execute() {
                    hero.getStats().consumeMana(s.getManaCost());
                    s.use(hero, enemy);
                    renderer.playSkillEffect(s.getName(), false);
                }

                @Override
                public String getDescription() {
                    return "Saber uses " + s.getName() + "!";
                }
            });
        }
    }

    public void onItemSelected(Consumable item) {
        if(isPlayerTurn()) {
            performPlayerAction(new Command() {
                @Override
                public void execute() {
                    item.use(hero);
                    renderer.playSkillEffect("Divine Light", true);
                }

                @Override
                public String getDescription() {
                    return "Saber uses " + item.getName() + "!";
                }
            });
        }
    }

    // Helper sederhana untuk mengecek tipe target berdasarkan nama
    private boolean isSelfTargetSkill(String skillName) {
        return skillName.equals("Divine Light") ||
            skillName.equals("Buckle Up") ||
            skillName.equals("Magic Res") ||
            skillName.equals("Endure") ||
            skillName.equals("Gulp"); // Skill Slime
    }

    // --- BATTLE LOGIC ---

    private void startPlayerTurn() {
        currentState = BattleState.PLAYER_TURN;
        ui.setButtonsVisible(true);

        hero.applyTurnEffects();
        ui.refreshStats(hero, enemy);

        if (hero.isDead()) { handleGameOver(); return; }
        if (hero.isStunned()) {
            System.out.println("Player Stunned!");
            hero.setStunned(false);
            Timer.schedule(new Timer.Task(){ @Override public void run(){ performEnemyTurn(); }}, 1f);
        }
    }

    private void performPlayerAction(Command action) {
        // 1. Tampilkan Notifikasi Player
        ui.showNotification(action.getDescription()); // <--- INI DIA

        // 2. Eksekusi
        action.execute();
        ui.refreshStats(hero, enemy);

        if (hero.isDead()) { handleGameOver(); return; } // Cek suicide skill
        if (enemy.isDead()) { handleVictory(); return; }

        currentState = BattleState.ENEMY_TURN;
        ui.setButtonsVisible(false);
        Timer.schedule(new Timer.Task(){ @Override public void run(){ performEnemyTurn(); }}, 1f);
    }

    private void performEnemyTurn() {
        enemy.applyTurnEffects();
        String actionText = enemy.act(hero);

        // 2. Tampilkan Notifikasi Musuh
        if (!actionText.isEmpty()) {
            ui.showNotification(actionText);
        }
        if (enemy.isDead()) { handleVictory(); return; }

        if (enemy.isStunned()) {
            System.out.println("Enemy Stunned!");
            enemy.setStunned(false);
            startPlayerTurn();
            return;
        }

        enemy.act(hero); // AI Action

        if (hero.isDead()) { handleGameOver(); return; }
        startPlayerTurn();
    }

    private void handleVictory() {
        currentState = BattleState.VICTORY;
        // Logic Reward & Save
        hero.getStats().addExp(enemy.getStats().getExpReward());
        hero.getStats().addManaCrystals(enemy.getStats().getCrystalReward());
        com.FEA_3.frontend.Utils.NetworkManager.getInstance()
            .savePlayer("User1", hero.getStats(), null);

        Dialog win = new Dialog("VICTORY!", ResourceManager.getInstance().getSkin()) {
            @Override protected void result(Object o) { if(onVictoryAction != null) onVictoryAction.run(); }
        };
        win.text("Enemy Defeated!\n+"+enemy.getStats().getCrystalReward()+" Mana Crystal\n+"+enemy.getStats().getExpReward()+" Exp\nSaved.");
        win.button("Continue", true);
        win.show(stage);
    }

    private void handleGameOver() {
        currentState = BattleState.DEFEAT;
        ui.setButtonsVisible(false);
        Timer.schedule(new Timer.Task(){ @Override public void run(){ game.setScreen(new GameOverScreen(game)); }}, 1.5f);
    }

    // --- GETTERS (Untuk UI) ---
    public boolean isPlayerTurn() { return currentState == BattleState.PLAYER_TURN; }
    public List<Skill> getPlayerSkills() { return hero.getUnlockedSkills(); }
    public int getPlayerMp() { return hero.getStats().getCurrentMp(); }
    public List<Consumable> getPlayerInventory() { return hero.getStats().getInventory(); }

    @Override public void dispose() { stage.dispose(); }
    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { bgm.stop(); }
}
