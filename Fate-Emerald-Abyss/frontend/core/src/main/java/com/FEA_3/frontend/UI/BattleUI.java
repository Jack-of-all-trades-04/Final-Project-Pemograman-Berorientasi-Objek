package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Core.BattleScreen;
import com.FEA_3.frontend.Entity.*;
import com.FEA_3.frontend.UI.EffectWidget;
import com.FEA_3.frontend.UI.StatusWidget;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.List;

public class BattleUI {
    private Stage stage;
    private BattleScreen screen; // Reference ke Logic Utama
    private Skin skin;

    // Widgets
    private StatusWidget playerHpWidget, playerMpWidget;
    private StatusWidget enemyHpWidget, enemyMpWidget;
    private Table commandTable;

    private EffectWidget playerEffects;
    private EffectWidget enemyEffects;

    public BattleUI(Stage stage, BattleScreen screen, GameUnit hero, GameUnit enemy) {
        this.stage = stage;
        this.screen = screen;
        this.skin = ResourceManager.getInstance().getSkin();
        setupLayout(hero, enemy);
    }

    private void setupLayout(GameUnit hero, GameUnit enemy) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float uiPanelHeight = h * 0.3f;

        // 1. Widgets Init
        playerHpWidget = new StatusWidget(skin, Color.GREEN, false);
        playerMpWidget = new StatusWidget(skin, Color.BLUE, true);
        enemyHpWidget = new StatusWidget(skin, Color.RED, false);
        enemyMpWidget = new StatusWidget(skin, Color.MAGENTA, true);
        playerEffects = new EffectWidget(skin);
        enemyEffects = new EffectWidget(skin);

        // 2. Left Panel (Player)
        Table leftPanel = new Table();
        leftPanel.top().left().pad(15);
        leftPanel.add(new Label(hero.getName(), skin)).left().padBottom(5).row();
        leftPanel.add(playerHpWidget).left().padBottom(2).row();
        leftPanel.add(playerMpWidget).left().padBottom(5).row();
        leftPanel.add(playerEffects).left().height(30);

        // 3. Right Panel (Enemy)
        Table rightPanel = new Table();
        rightPanel.top().right().pad(15);
        rightPanel.add(new Label(enemy.getName(), skin)).right().padBottom(5).row();
        rightPanel.add(enemyHpWidget).right().padBottom(2).row();
        rightPanel.add(enemyMpWidget).right().padBottom(5).row();
        rightPanel.add(enemyEffects).right().height(30);

        // 4. Center Panel (Commands)
        createCommandTable();

        // 5. Main Layout (Bottom Blue Panel)
        Table bottomPanel = new Table();
        Pixmap bgMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgMap.setColor(0, 0, 0.5f, 0.85f);
        bgMap.fill();
        bottomPanel.setBackground(new TextureRegionDrawable(new Texture(bgMap)));

        bottomPanel.add(leftPanel).width(w * 0.3f).height(uiPanelHeight).top().left();
        bottomPanel.add(commandTable).width(w * 0.4f).height(uiPanelHeight).center();
        bottomPanel.add(rightPanel).width(w * 0.3f).height(uiPanelHeight).top().right();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom();
        rootTable.add(bottomPanel).width(w).height(uiPanelHeight);

        stage.addActor(rootTable);
    }

    private void createCommandTable() {
        commandTable = new Table();
        TextButton btnAttack = createBtn("ATTACK", () -> screen.onAttackClicked());
        TextButton btnDefend = createBtn("DEFEND", () -> screen.onDefendClicked());
        TextButton btnSkill = createBtn("SKILL", () -> showSkillDialog());
        TextButton btnItem = createBtn("ITEM", () -> showItemDialog());
        TextButton btnEscape = createBtn("ESCAPE", () -> screen.onEscapeClicked());

        float btnW = 140, btnH = 40;
        commandTable.add(btnAttack).width(btnW).height(btnH).pad(5);
        commandTable.add(btnDefend).width(btnW).height(btnH).pad(5).row();
        commandTable.add(btnSkill).width(btnW).height(btnH).pad(5);
        commandTable.add(btnEscape).width(btnW).height(btnH).pad(5).row();
        commandTable.add(btnItem).colspan(2).width(btnW).height(btnH).pad(5);
    }

    // --- Public Methods untuk Update UI ---

    public void setButtonsVisible(boolean visible) {
        commandTable.setVisible(visible);
    }

    public void refreshStats(GameUnit hero, GameUnit enemy) {
        UnitStats hs = hero.getStats();
        UnitStats es = enemy.getStats();
        playerHpWidget.onHealthChanged(hs.getCurrentHp(), hs.getMaxHp());
        playerMpWidget.updateMp(hs.getCurrentMp(), hs.getMaxMp());
        enemyHpWidget.onHealthChanged(es.getCurrentHp(), es.getMaxHp());
        enemyMpWidget.updateMp(es.getCurrentMp(), es.getMaxMp());
        playerEffects.updateFromUnit(hero);
        enemyEffects.updateFromUnit(enemy);
    }

    // --- Dialogs ---

    private void showSkillDialog() {
        if (!screen.isPlayerTurn()) return;
        Dialog d = new Dialog("Select Skill", skin);
        Table c = d.getContentTable(); c.pad(10);

        List<Skill> skills = screen.getPlayerSkills();
        boolean hasActive = false;

        for (Skill s : skills) {
            if (s.getType() == Skill.SkillType.PASSIVE) continue;
            hasActive = true;
            boolean enoughMp = screen.getPlayerMp() >= s.getManaCost();

            TextButton btn = new TextButton(s.getName() + " (" + s.getManaCost() + " MP)", skin);
            if (!enoughMp) { btn.setDisabled(true); btn.setColor(Color.GRAY); }

            btn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (enoughMp) { screen.onSkillSelected(s); d.hide(); }
                }
            });
            c.add(btn).width(250).height(40).pad(5).row();
        }

        if (!hasActive) c.add(new Label("No Active Skills", skin)).pad(20);

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ d.hide(); }});
        d.button(cancel);
        d.show(stage);
    }

    private void showItemDialog() {
        if (!screen.isPlayerTurn()) return;
        Dialog d = new Dialog("Select Item", skin);
        Table c = d.getContentTable(); c.pad(10);

        List<Consumable> inventory = screen.getPlayerInventory();
        boolean hasItem = false;

        if (inventory != null) {
            for (Consumable item : inventory) {
                if (item.getQuantity() > 0) {
                    hasItem = true;
                    TextButton btn = new TextButton(item.getName() + " (x" + item.getQuantity() + ")", skin);
                    btn.addListener(new ClickListener() {
                        @Override public void clicked(InputEvent event, float x, float y) {
                            screen.onItemSelected(item); d.hide();
                        }
                    });
                    c.add(btn).width(250).height(40).pad(5).row();
                }
            }
        }

        if (!hasItem) c.add(new Label("Inventory Empty", skin)).pad(20);

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ d.hide(); }});
        d.button(cancel);
        d.show(stage);
    }

    // --- Helper ---
    private TextButton createBtn(String txt, Runnable action) {
        TextButton btn = new TextButton(txt, skin);
        btn.addListener(new SoundListener());
        btn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { action.run(); }
        });
        return btn;
    }
}
