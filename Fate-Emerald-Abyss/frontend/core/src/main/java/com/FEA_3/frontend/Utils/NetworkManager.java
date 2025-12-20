package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.UnitStats;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

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

        // 1. Buat JSON String secara manual atau pakai LibGDX Json
        // Kita pakai cara manual yang aman agar key-nya persis dengan Backend
        String jsonContent = "{" +
            "\"id\": \"" + userId + "\"," +
            "\"characterName\": \"" + stats.getName() + "\"," +
            "\"level\": " + stats.getLevel() + "," +
            "\"currentExp\": " + stats.getCurrentExp() + "," +
            "\"maxExp\": " + stats.getMaxExp() + "," +
            "\"manaCrystals\": " + stats.getManaCrystals() + "," +
            "\"maxHp\": " + stats.getMaxHp() + "," +
            "\"currentHp\": " + stats.getCurrentHp() + "," +
            "\"maxMp\": " + stats.getMaxMp() + "," +
            "\"currentMp\": " + stats.getCurrentMp() + "," +
            "\"attackPower\": " + stats.getAttackPower() + "," +
            "\"defense\": " + stats.getDefense() + "," +
            "\"speed\": " + stats.getSpeed() +
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
