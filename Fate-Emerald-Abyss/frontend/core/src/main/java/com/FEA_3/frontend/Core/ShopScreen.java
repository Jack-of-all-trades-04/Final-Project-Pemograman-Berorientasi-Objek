package com.FEA_3.frontend.Core;

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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ShopScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private Label crystalLabel;

    // Data Player saat ini (Untuk memanipulasi uang secara lokal sebelum save)
    private UnitStats currentStats;
    private String userId = "User1";

    // Class Sederhana untuk Data Barang Toko
    private static class ShopItem {
        String name;
        int price;
        String description;
        String iconPath; // Path texture icon

        public ShopItem(String name, int price, String desc, String iconPath) {
            this.name = name;
            this.price = price;
            this.description = desc;
            this.iconPath = iconPath;
        }
    }

    private ShopItem[] shopItems = {
        new ShopItem("Health Potion", 50, "Pulihkan 50 HP", "Consumable/HP Potion.png"),
        new ShopItem("Mana Potion", 80, "Pulihkan 30 MP", "Consumable/MP Potion.png"),
        new ShopItem("Iron Elixir", 65, "3-Turn 30% DEF", "Consumable/Iron Elixir.png")
    };

    public ShopScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(1280, 720));
        this.skin = ResourceManager.getInstance().getSkin();

        Gdx.input.setInputProcessor(stage);

        setupUI();
        loadPlayerData();
    }

    private void loadPlayerData() {
        crystalLabel.setText("Syncing...");

        NetworkManager.getInstance().loadPlayer(userId, new NetworkManager.LoadCallback() {
            @Override
            public void onSuccess(UnitStats stats) {
                currentStats = stats;
                updateCrystalLabel();
            }

            @Override
            public void onFail(String msg) {
                crystalLabel.setText("Offline");
                System.err.println(msg);
            }
        });
    }

    private void updateCrystalLabel() {
        if (currentStats != null) {
            crystalLabel.setText("Mana Crystals: " + currentStats.getManaCrystals());
        }
    }

    private void setupUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);

        // --- 1. HEADER
        Label titleLabel = new Label("Shopping Menu", skin);
        titleLabel.setColor(Color.GREEN);
        titleLabel.setFontScale(1.5f);

        crystalLabel = new Label("Loading...", skin);
        crystalLabel.setColor(Color.CYAN);

        Table headerTable = new Table();
        headerTable.add(titleLabel).expandX().center();
        headerTable.add(crystalLabel).right().padRight(20);

        root.add(headerTable).growX().padBottom(30).row();

        // --- 2. LIST ITEMS ---
        Table listTable = new Table();
        listTable.top();

        for (ShopItem item : shopItems) {
            addItemRow(listTable, item);
        }

        ScrollPane scrollPane = new ScrollPane(listTable, skin);
        scrollPane.setFadeScrollBars(false);
        // Style background gelap transparan seperti di gambar
        // scrollPane.setBackground(...);

        root.add(scrollPane).width(1000).height(500).row();

        // --- 3. TOMBOL KELUAR ---
        TextButton backBtn = new TextButton("Back to Map", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WorldMapScreen(game));
                System.out.println("Kembali ke Map");
            }
        });
        backBtn.addListener(new SoundListener());
        root.add(backBtn).padTop(20);

        stage.addActor(root);
    }

    private void addItemRow(Table container, ShopItem item) {
        // Container baris (bisa dikasih background abu-abu tipis)
        Table row = new Table();
        row.pad(10);

        // 1. Icon (Placeholder jika gambar tidak ada)
        Image icon;
        try {
            icon = new Image(ResourceManager.getInstance().getTexture(item.iconPath));
        } catch (Exception e) {
            icon = new Image(ResourceManager.getInstance().getTexture("Utility/noimage.png"));
        }

        // 2. Deskripsi
        Table descTable = new Table();
        Label nameLbl = new Label(item.name, skin);
        Label descLbl = new Label(item.description, skin);
        descLbl.setColor(Color.GRAY);
        descLbl.setFontScale(0.8f);
        Label priceLbl = new Label(item.price + " MC", skin);
        priceLbl.setColor(Color.CYAN);

        descTable.add(nameLbl).left().row();
        descTable.add(descLbl).left().row();
        descTable.add(priceLbl).left().padTop(5);

        // 3. Kontrol Kuantitas & Tombol Beli
        Table actionTable = new Table();
        final int[] quantity = {1}; // Array wrapper biar bisa diakses di inner class

        Label qtyLabel = new Label("1", skin);
        qtyLabel.setAlignment(Align.center);

        TextButton minusBtn = new TextButton("-", skin);
        TextButton plusBtn = new TextButton("+", skin);
        TextButton buyBtn = new TextButton("BELI (Total: " + item.price + ")", skin);

        // Logic UI Minus
        minusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    qtyLabel.setText(String.valueOf(quantity[0]));
                    buyBtn.setText("BELI (Total: " + (item.price * quantity[0]) + ")");
                }
            }
        });
        minusBtn.addListener(new SoundListener());

        // Logic UI Plus
        plusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (quantity[0] < 99) {
                    quantity[0]++;
                    qtyLabel.setText(String.valueOf(quantity[0]));
                    buyBtn.setText("BELI (Total: " + (item.price * quantity[0]) + ")");
                }
            }
        });
        plusBtn.addListener(new SoundListener());

        // --- LOGIC BELI MENGGUNAKAN NETWORK MANAGER YANG DILAMPIRKAN ---
        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentStats == null) return;

                int totalCost = item.price * quantity[0];

                // 1. Cek Uang Lokal
                if (currentStats.getManaCrystals() >= totalCost) {

                    // 2. Kurangi Uang
                    int newBalance = currentStats.getManaCrystals() - totalCost;
                    currentStats.setManaCrystals(newBalance);
                    currentStats.addItem(item.name, quantity[0]);

                    // 3. Update UI
                    updateCrystalLabel();
                    buyBtn.setText("PROCESSING...");
                    buyBtn.setDisabled(true);

                    // 4. SIMPAN KE SERVER (Penting!)
                    NetworkManager.getInstance().savePlayer(userId, currentStats);

                    // Feedback Visual Sukses
                    buyBtn.setColor(Color.GREEN);
                    buyBtn.setText("SUCCESS");

                    // Reset tombol setelah 1 detik
                    buyBtn.addAction(Actions.sequence(
                        Actions.delay(1f),
                        Actions.run(() -> {
                            buyBtn.setDisabled(false);
                            buyBtn.setColor(Color.WHITE);
                            buyBtn.setText("BELI (Total: " + (item.price * quantity[0]) + ")");
                        })
                    ));

                } else {
                    // Uang Kurang
                    buyBtn.setColor(Color.RED);
                    buyBtn.setText("NO FUNDS");
                    buyBtn.addAction(Actions.sequence(Actions.delay(1f), Actions.run(() -> {
                        buyBtn.setColor(Color.WHITE);
                        buyBtn.setText("BELI (Total: " + totalCost + ")");
                    })));
                }
            }
        });
        buyBtn.addListener(new SoundListener());

        actionTable.add(minusBtn).width(30);
        actionTable.add(qtyLabel).width(40);
        actionTable.add(plusBtn).width(30);
        actionTable.add(buyBtn).padLeft(10).width(150);

        // Gabungkan ke Row
        row.add(icon).size(64).padRight(20);
        row.add(descTable).expandX().left();
        row.add(actionTable).right();

        container.add(row).growX().padBottom(10).row();
        // Separator line (opsional)
        // container.add(new Image(skin.newDrawable("white", Color.DARK_GRAY))).height(1).growX().row();
    }

    @Override
    public void render(float delta) {
        // Background Gelap Sesuai Gambar
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); }
}
