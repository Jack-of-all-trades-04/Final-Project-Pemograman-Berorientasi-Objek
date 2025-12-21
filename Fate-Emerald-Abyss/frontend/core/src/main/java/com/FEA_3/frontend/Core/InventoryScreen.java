package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.Consumable;
import com.FEA_3.frontend.Entity.Consumable.ItemType;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Main;
import com.FEA_3.frontend.Utils.NetworkManager;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SoundListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class InventoryScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;

    public InventoryScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = ResourceManager.getInstance().getSkin();
        setupUI();
    }

    private void setupUI() {
        // Root Table
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Judul
        Label title = new Label("INVENTORY", skin);
        title.setFontScale(1.5f);
        root.add(title).padBottom(20).row();

        // Container untuk list item (Scrollable)
        Table itemTable = new Table();
        ScrollPane scrollPane = new ScrollPane(itemTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Hanya scroll vertikal

        // --- POPULATE ITEMS ---
        List<Consumable> inventory = game.playerStats.getInventory();

        if (inventory == null || inventory.isEmpty()) {
            itemTable.add(new Label("Inventory is Empty", skin)).pad(20);
        } else {
            for (final Consumable item : inventory) {
                // Background per baris item
                Table rowTable = new Table();

                // Nama Item & Jumlah
                Label itemLabel = new Label(item.getDisplayName(), skin);
                itemLabel.setAlignment(Align.left);

                // Tombol USE
                TextButton useBtn = new TextButton("USE", skin);

                // LOGIKA TOMBOL:
                // Buff (ATK/DEF/dll) tidak bisa dipakai di Map karena butuh sistem Turn Battle.
                // Hanya POTION_HP dan POTION_MP yang bisa dipakai di sini.
                boolean isUsableOnMap = (item.getType() == ItemType.POTION_HP || item.getType() == ItemType.POTION_MP);

                if (isUsableOnMap) {
                    useBtn.addListener(new SoundListener());
                    useBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            useItemLogic(item);
                        }
                    });
                } else {
                    // Jika Buff, matikan tombol dan ubah teks
                    useBtn.setText("BATTLE ONLY");
                    useBtn.setColor(Color.GRAY);
                    useBtn.setDisabled(true);
                }

                // Layout Baris
                rowTable.add(itemLabel).width(350).padLeft(15).left();
                rowTable.add(useBtn).width(120).padRight(10).right();

                // Masukkan baris ke tabel utama
                itemTable.add(rowTable).padBottom(10).width(500).height(60).row();
            }
        }

        root.add(scrollPane).width(550).height(400).row();

        // --- INFO STATS (Opsional, biar kelihatan HP nambah) ---
        Label statsInfo = new Label(
            "HP: " + game.playerStats.getCurrentHp() + "/" + game.playerStats.getMaxHp() +
                "  |  MP: " + game.playerStats.getCurrentMp() + "/" + game.playerStats.getMaxMp(),
            skin
        );
        root.add(statsInfo).padTop(10).row();

        // --- BACK BUTTON ---
        TextButton backBtn = new TextButton("BACK TO MAP", skin);
        backBtn.setColor(Color.SALMON);
        backBtn.addListener(new SoundListener());
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WorldMapScreen(game));
            }
        });
        root.add(backBtn).padTop(20).width(200).height(50);

        stage.addActor(root);
    }

    private void useItemLogic(Consumable item) {
        UnitStats stats = game.playerStats;
        boolean itemUsed = false;

        switch (item.getType()) {
            case POTION_HP:
                if (stats.getCurrentHp() >= stats.getMaxHp()) {
                    showDialog("Full Health", "Your HP is already full!");
                    return;
                }
                int newHp = stats.getCurrentHp() + item.getValue();
                if (newHp > stats.getMaxHp()) newHp = stats.getMaxHp();
                stats.setCurrentHp(newHp);
                showDialog("Used Potion", "Restored " + item.getValue() + " HP!");
                itemUsed = true;
                break;

            case POTION_MP:
                if (stats.getCurrentMp() >= stats.getMaxMp()) {
                    showDialog("Full Mana", "Your MP is already full!");
                    return;
                }
                int newMp = stats.getCurrentMp() + item.getValue();
                if (newMp > stats.getMaxMp()) newMp = stats.getMaxMp();
                stats.setCurrentMp(newMp);
                showDialog("Used Potion", "Restored " + item.getValue() + " MP!");
                itemUsed = true;
                break;

            default:
                showDialog("Info", "This item can only be used during battle.");
                return;
        }

        if (itemUsed) {

            item.addQuantity(-1); // Mengurangi 1

            // Hapus dari list jika habis
            if (item.getQuantity() <= 0) {
                game.playerStats.getInventory().remove(item);
            }

            // Simpan perubahan ke server
            NetworkManager.getInstance().savePlayer("User1", game.playerStats, new NetworkManager.SaveCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("Save Berhasil!");
                }

                @Override
                public void onFail(String msg) {
                    System.err.println("Save Gagal: " + msg);
                }
            });

            // Reload layar untuk update UI (jumlah berkurang / item hilang)
            game.setScreen(new InventoryScreen(game));
        }
    }

    private void showDialog(String title, String msg) {
        Dialog d = new Dialog(title, skin);
        d.text(msg);
        d.button("OK");
        d.show(stage);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override
    public void render(float delta) {
        // Background Gelap agar terlihat seperti overlay
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
