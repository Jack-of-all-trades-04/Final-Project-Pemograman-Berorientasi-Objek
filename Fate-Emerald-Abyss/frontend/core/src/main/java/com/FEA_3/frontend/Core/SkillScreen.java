package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SkillScreen implements Screen {
    private Main game;
    private Stage stage;
    private UnitStats stats;
    private GameUnit referenceUnit;

    public SkillScreen(Main game) {
        this.game = game;
        this.stats = game.playerStats;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Setup Reference Unit untuk baca skill
        referenceUnit = new GameUnit(stats);
        com.FEA_3.frontend.Patterns.Factory.UnitFactory.loadSkillsForPlayer(referenceUnit);

        setupUI();
    }

    private void setupUI() {
        Skin skin = ResourceManager.getInstance().getSkin();

        // 1. Root Table
        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);

        Label title = new Label("SKILL MASTERY", skin);
        title.setFontScale(1.5f);
        title.setColor(Color.GOLD);
        root.add(title).padBottom(20).row();

        // 2. List Container
        Table listTable = new Table();
        listTable.top();

        // Loop SEMUA skill (bukan cuma activeSkills)
        for (Skill s : referenceUnit.getAllSkills()) {
            boolean isLocked = stats.getLevel() < s.getUnlockLevel();
            addSkillRow(listTable, s, isLocked, skin);
        }

        ScrollPane scrollPane = new ScrollPane(listTable, skin);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).width(700).height(450).row();

        // 3. Tombol Back
        TextButton backBtn = new TextButton("BACK TO MAP", skin);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WorldMapScreen(game));
            }
        });
        backBtn.addListener(new SoundListener());
        root.add(backBtn).width(200).height(50).padTop(20);

        stage.addActor(root);
    }

    private void addSkillRow(Table table, Skill skill, boolean isLocked, Skin skin) {
        // Container Baris (Button agar bisa diklik)
        Button rowBtn = new Button(skin);
        // Style rowBtn bisa diatur agar transparan atau kotak biasa

        Table rowContent = new Table();
        rowContent.pad(10);

        // A. ICON SKILL
        Image icon;
        try {
            Texture tex = ResourceManager.getInstance().getTexture(skill.getIconPath());
            icon = new Image(tex);
        } catch (Exception e) {
            // Fallback icon jika gambar tidak ketemu
            icon = new Image(ResourceManager.getInstance().getTexture("Entity/Player/MC.png"));
        }

        // LOGIKA VISUAL LOCKED
        if (isLocked) {
            icon.setColor(Color.DARK_GRAY); // Gelapkan Icon
        }

        // B. NAMA & LEVEL INFO
        Table textTable = new Table();
        Label nameLbl = new Label(skill.getName(), skin);
        Label statusLbl;

        if (isLocked) {
            nameLbl.setColor(Color.GRAY);
            statusLbl = new Label("Unlocks at Lv." + skill.getUnlockLevel(), skin);
            statusLbl.setColor(Color.RED);
        } else {
            nameLbl.setColor(Color.CYAN);
            statusLbl = new Label(skill.getType() == Skill.SkillType.PASSIVE ? "Passive" : "MP: " + skill.getManaCost(), skin);
            statusLbl.setColor(Color.LIGHT_GRAY);
        }

        textTable.add(nameLbl).left().row();
        textTable.add(statusLbl).left();

        // C. SUSUN KE ROW
        rowContent.add(icon).size(50).padRight(20);
        rowContent.add(textTable).expandX().left();

        // Tambahkan content ke Button
        rowBtn.add(rowContent).expand().fill();

        // D. LISTENER KLIK (Munculkan Detail)
        rowBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSkillDetailDialog(skill, isLocked);
            }
        });
        rowBtn.addListener(new SoundListener());

        table.add(rowBtn).width(650).height(80).padBottom(5);
        table.row();
    }

    // --- POPUP DETAIL SKILL ---
    private void showSkillDetailDialog(Skill skill, boolean isLocked) {
        Skin skin = ResourceManager.getInstance().getSkin();

        Dialog detailDialog = new Dialog("", skin);
        detailDialog.setBackground(skin.getDrawable("default-window")); // Pastikan ada drawable background window/rect

        // Judul
        Label title = new Label(skill.getName(), skin);
        title.setFontScale(1.2f);
        title.setColor(Color.GOLD);

        // Icon Besar
        Image icon = new Image(ResourceManager.getInstance().getTexture(skill.getIconPath())); // Gunakan try-catch idealnya
        if(isLocked) icon.setColor(Color.DARK_GRAY);

        // Deskripsi
        Label desc = new Label(skill.getDescription(), skin);
        desc.setWrap(true);
        desc.setAlignment(Align.center);

        // Info Unlock
        Label unlockInfo = new Label("", skin);
        if (isLocked) {
            unlockInfo.setText("LOCKED - Requires Level " + skill.getUnlockLevel());
            unlockInfo.setColor(Color.RED);
        } else {
            unlockInfo.setText("Status: UNLOCKED");
            unlockInfo.setColor(Color.GREEN);
        }

        // Susun Layout Dialog
        detailDialog.getContentTable().pad(20);
        detailDialog.getContentTable().add(title).row();
        detailDialog.getContentTable().add(icon).size(80).pad(15).row();
        detailDialog.getContentTable().add(desc).width(400).padBottom(15).row();
        detailDialog.getContentTable().add(unlockInfo).row();

        // Tombol Close
        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                detailDialog.hide();
            }
        });
        closeBtn.addListener(new SoundListener());
        detailDialog.getButtonTable().add(closeBtn).width(100).pad(10);

        detailDialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
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
