package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.Consumable;
import com.FEA_3.frontend.Entity.UnitStats;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static NetworkManager instance;

    // --- KONFIGURASI PENTING ---
    // Ubah ke TRUE saat mau build HTML/EXE untuk Itch.io
    // Ubah ke FALSE saat development pakai Spring Boot
    private final boolean USE_LOCAL_STORAGE = true;

    private final String BASE_URL = "http://localhost:9090/api";

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instance == null) instance = new NetworkManager();
        return instance;
    }

    public interface ResetCallback {
        void onSuccess();
        void onFail(String msg);
    }

    public void resetPlayer(String userId, final ResetCallback callback) {

        if (USE_LOCAL_STORAGE) {
            // MODE OFFLINE: Hapus data dari Preferences
            try {
                Preferences prefs = Gdx.app.getPreferences("FateEmeraldAbyssSave");

                // Hapus key spesifik user
                prefs.remove("save_data_" + userId);
                prefs.flush(); // Paksa simpan perubahan

                System.out.println("LOCAL DATA RESET SUCCESS");
                if (callback != null) {
                    Gdx.app.postRunnable(callback::onSuccess);
                }
            } catch (Exception e) {
                if (callback != null) callback.onFail(e.getMessage());
            }
            return; // Stop, jangan panggil server
        }

        // MODE ONLINE: Panggil Server DELETE (Logika Lama)
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.DELETE)
            .url(BASE_URL + "/delete/" + userId)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println("SERVER RESET SUCCESS: " + httpResponse.getResultAsString());
                if (callback != null) Gdx.app.postRunnable(callback::onSuccess);
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("Reset Failed: " + t.getMessage());
                if (callback != null) Gdx.app.postRunnable(() -> callback.onFail(t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    public interface SaveCallback {
        void onSuccess();
        void onFail(String msg);
    }

    public interface LoadCallback {
        void onSuccess(UnitStats stats);
        void onFail(String msg);
    }

    // --- FITUR SAVE ---
    public void savePlayer(String userId, UnitStats stats, final SaveCallback callback) {
        // 1. Generate JSON String (Logika ini SAMA untuk Local maupun Online)
        StringBuilder inventoryJson = new StringBuilder("[");
        List<Consumable> items = stats.getInventory();

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Consumable item = items.get(i);
                inventoryJson.append("{")
                    .append("\"name\":\"").append(item.getName()).append("\",")
                    .append("\"quantity\":").append(item.getQuantity()).append(",")
                    .append("\"type\":\"").append(item.getType().toString()).append("\",")
                    .append("\"value\":").append(item.getValue())
                    .append("}");
                if (i < items.size() - 1) inventoryJson.append(",");
            }
        }
        inventoryJson.append("]");

        String jsonContent = "{" +
            "\"id\": \"" + userId + "\"," +
            "\"characterName\": \"" + stats.getName() + "\"," +
            "\"level\": " + stats.getLevel() + "," +
            "\"currentExp\": " + stats.getCurrentExp() + "," +
            "\"maxExp\": " + stats.getMaxExp() + "," +
            "\"manaCrystals\": " + stats.getManaCrystals() + "," +
            "\"unlockedChapter\": " + stats.getUnlockedChapter() + "," +
            "\"maxHp\": " + stats.getMaxHp() + "," +
            "\"currentHp\": " + stats.getCurrentHp() + "," +
            "\"maxMp\": " + stats.getMaxMp() + "," +
            "\"currentMp\": " + stats.getCurrentMp() + "," +
            "\"attackPower\": " + stats.getAttackPower() + "," +
            "\"defense\": " + stats.getDefense() + "," +
            "\"speed\": " + stats.getSpeed() + "," +
            "\"inventoryItems\": " + inventoryJson.toString() +
            "}";

        // --- CABANG LOGIKA: OFFLINE VS ONLINE ---

        if (USE_LOCAL_STORAGE) {
            // MODE OFFLINE (HTML / EXE)
            try {
                // Simpan ke Preferences (LocalStorage di Web / File di Desktop)
                Preferences prefs = Gdx.app.getPreferences("FateEmeraldAbyssSave");
                prefs.putString("save_data_" + userId, jsonContent);
                prefs.flush(); // PENTING: Paksa tulis ke disk

                System.out.println("LOCAL SAVE SUCCESS");
                if (callback != null) {
                    // Beri jeda sedikit simulasi async (opsional)
                    Gdx.app.postRunnable(callback::onSuccess);
                }
            } catch (Exception e) {
                if (callback != null) callback.onFail(e.getMessage());
            }
            return; // STOP, jangan lanjut ke HTTP
        }

        // MODE ONLINE (Spring Boot)
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/save")
            .header("Content-Type", "application/json")
            .content(jsonContent)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println("SERVER SAVE SUCCESS");
                if (callback != null) Gdx.app.postRunnable(callback::onSuccess);
            }
            @Override
            public void failed(Throwable t) {
                if (callback != null) Gdx.app.postRunnable(() -> callback.onFail(t.getMessage()));
            }
            @Override
            public void cancelled() { }
        });
    }

    // --- FITUR LOAD ---
    public void loadPlayer(String userId, final LoadCallback callback) {

        if (USE_LOCAL_STORAGE) {
            // MODE OFFLINE
            Preferences prefs = Gdx.app.getPreferences("FateEmeraldAbyssSave");
            String jsonString = prefs.getString("save_data_" + userId, "");

            if (jsonString.isEmpty()) {
                // Belum ada save data -> Buat Data Baru (New Game)
                // Kita buat stats level 1 manual (mirip logika di Spring Boot dulu)
                UnitStats newStats = com.FEA_3.frontend.Utils.UnitDatabase.createPlayerStats(1);
                // Reset inventory atau parameter lain jika perlu
                newStats.setUnlockedChapter(1);

                System.out.println("NO SAVE FOUND. CREATING NEW DATA.");
                Gdx.app.postRunnable(() -> callback.onSuccess(newStats));
            } else {
                // Ada Save Data -> Parse JSON-nya
                parseAndReturnStats(jsonString, callback);
            }
            return; // STOP
        }

        // MODE ONLINE
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/load/" + userId)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                parseAndReturnStats(result, callback);
            }
            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFail("Connection Failed"));
            }
            @Override
            public void cancelled() {}
        });
    }

    // --- HELPER: PARSING JSON (Dipakai Offline & Online) ---
    private void parseAndReturnStats(String jsonString, LoadCallback callback) {
        try {
            com.badlogic.gdx.utils.JsonReader reader = new com.badlogic.gdx.utils.JsonReader();
            com.badlogic.gdx.utils.JsonValue root = reader.parse(jsonString);

            // Ambil data
            String name = root.getString("characterName");
            int hp = root.getInt("maxHp");
            int mp = root.getInt("maxMp");
            int atk = root.getInt("attackPower");
            int def = root.getInt("defense");
            int spd = root.getInt("speed");

            UnitStats stats = new UnitStats(name, hp, mp, atk, def, spd);

            stats.setLevel(root.getInt("level"));
            stats.setCurrentHp(root.getInt("currentHp"));
            stats.setCurrentMp(root.getInt("currentMp"));
            stats.setCurrentExp(root.getInt("currentExp"));
            stats.setMaxExp(root.getInt("maxExp"));
            stats.setManaCrystals(root.getInt("manaCrystals"));
            stats.setUnlockedChapter(root.getInt("unlockedChapter", 1));

            // Load Inventory
            List<Consumable> inventory = new ArrayList<>();
            JsonValue invArray = root.get("inventoryItems");
            if (invArray != null) {
                for (JsonValue itemJson : invArray) {
                    String iName = itemJson.getString("name", "Item");
                    String iTypeStr = itemJson.getString("type", "POTION_HP");
                    int iVal = itemJson.getInt("value", 0);
                    int iQty = itemJson.getInt("quantity", 1);
                    try {
                        Consumable.ItemType type = Consumable.ItemType.valueOf(iTypeStr);
                        stats.addItem(iName, type, iVal, iQty);
                    } catch (Exception e) {}
                }
            }

            Gdx.app.postRunnable(() -> callback.onSuccess(stats));

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.postRunnable(() -> callback.onFail("Error Parsing Data"));
        }
    }
}
