package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.Consumable;
import com.FEA_3.frontend.Entity.UnitStats;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static NetworkManager instance;
    private final String BASE_URL = "http://localhost:8080/api"; // Sesuaikan port

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        if (instance == null) instance = new NetworkManager();
        return instance;
    }

    // --- FITUR SAVE ---
    public void savePlayer(String userId, UnitStats stats) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/save")
            .header("Content-Type", "application/json")
            .build();

        StringBuilder inventoryJson = new StringBuilder("[");
        List<Consumable> items = stats.getInventory();

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Consumable item = items.get(i);
                inventoryJson.append("{")
                    .append("\"name\":\"").append(item.getName()).append("\",")
                    .append("\"quantity\":").append(item.getQuantity()).append(",")
                    // Enum dikirim sebagai String agar Backend bisa membacanya
                    .append("\"type\":\"").append(item.getType().toString()).append("\",")
                    .append("\"value\":").append(item.getValue())
                    .append("}");

                // Tambah koma jika bukan item terakhir
                if (i < items.size() - 1) inventoryJson.append(",");
            }
        }
        inventoryJson.append("]");

        // 1. Buat JSON String secara manual atau pakai LibGDX Json
        // Kita pakai cara manual yang aman agar key-nya persis dengan Backend
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

        httpRequest.setContent(jsonContent);

        // 2. Kirim Request
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println("SAVE SUCCESS: " + httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("SAVE FAILED: " + t.getMessage());
            }

            @Override
            public void cancelled() { }
        });
    }

    // --- FITUR LOAD (Update yang lama agar support stats baru) ---
    public interface LoadCallback {
        void onSuccess(UnitStats stats);
        void onFail(String msg);
    }

    // --- FITUR RESET / DELETE DATA ---
    public interface ResetCallback {
        void onSuccess();
        void onFail(String msg);
    }

    public void resetPlayer(String userId, final ResetCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.DELETE) // Method DELETE
            .url(BASE_URL + "/delete/" + userId)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                // Sukses
                System.out.println("Reset Success: " + httpResponse.getResultAsString());
                Gdx.app.postRunnable(callback::onSuccess);
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("Reset Failed: " + t.getMessage());
                Gdx.app.postRunnable(() -> callback.onFail(t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    public void loadPlayer(String userId, final LoadCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/load/" + userId)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();

                try {
                    // Parse JSON response
                    com.badlogic.gdx.utils.JsonReader reader = new com.badlogic.gdx.utils.JsonReader();
                    com.badlogic.gdx.utils.JsonValue root = reader.parse(result);

                    // Ambil data
                    String name = root.getString("characterName");
                    int hp = root.getInt("maxHp");
                    int mp = root.getInt("maxMp");
                    int atk = root.getInt("attackPower");
                    int def = root.getInt("defense");
                    int spd = root.getInt("speed");

                    // Buat Objek Stats
                    UnitStats stats = new UnitStats(name, hp, mp, atk, def, spd);

                    // Isi data progression
                    stats.setLevel(root.getInt("level"));
                    stats.setCurrentHp(root.getInt("currentHp"));
                    stats.setCurrentMp(root.getInt("currentMp"));
                    stats.setCurrentExp(root.getInt("currentExp"));
                    stats.setMaxExp(root.getInt("maxExp"));
                    stats.setManaCrystals(root.getInt("manaCrystals"));
                    stats.setUnlockedChapter(root.getInt("unlockedChapter", 1));

                    // --- LOAD INVENTORY (PARSING ARRAY) ---
                    List<Consumable> inventory = new ArrayList<>();
                    JsonValue invArray = root.get("inventoryItems"); // Sesuai nama field di Backend

                    if (invArray != null) {
                        for (JsonValue itemJson : invArray) {
                            String iName = itemJson.getString("name", "Item");
                            String iTypeStr = itemJson.getString("type", "POTION_HP");
                            int iVal = itemJson.getInt("value", 0);
                            int iQty = itemJson.getInt("quantity", 1);

                            try {
                                // Convert String JSON balik ke Enum Java
                                Consumable.ItemType type = Consumable.ItemType.valueOf(iTypeStr);
                                // Gunakan method addItem (Overload 4 param) untuk memasukkan data
                                stats.addItem(iName, type, iVal, iQty);
                            } catch (Exception e) {
                                System.err.println("Unknown item type skip: " + iTypeStr);
                            }
                        }
                    }

                    // Kirim ke Main thread
                    Gdx.app.postRunnable(() -> callback.onSuccess(stats));

                } catch (Exception e) {
                    e.printStackTrace();
                    Gdx.app.postRunnable(() -> callback.onFail("Error Parsing Data"));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFail("Connection Failed"));
            }

            @Override
            public void cancelled() {}
        });
    }
}
