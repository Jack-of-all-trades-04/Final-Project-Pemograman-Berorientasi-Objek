package com.FEA_3.frontend.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.FEA_3.frontend.Entity.UnitStats;

public class NetworkManager {
    private static final NetworkManager instance = new NetworkManager();
    private static final String BASE_URL = "http://localhost:8080/api"; // URL Springboot Anda
    private Json json;

    private NetworkManager() {
        json = new Json();
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    // Interface Callback agar BattleScreen tahu kapan data selesai di-load
    public interface DataCallback {
        void onSuccess(UnitStats stats);
        void onFail(Throwable t);
    }

    public void loadPlayerData(String userId, final DataCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/load/" + userId)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();

                // Parsing JSON dari Springboot ke Object Java
                try {
                    // ... try block ...
                    JsonValue root = new JsonReader().parse(result);

                    String name = root.getString("characterName");

                    // Ambil data lengkap
                    int hp = root.getInt("maxHp");
                    int mp = root.getInt("maxMp", 200); // Default 200 kalau null
                    int atk = root.getInt("attackPower");
                    int def = root.getInt("defense", 20);
                    int spd = root.getInt("speed", 10);

                    // Buat UnitStats dengan Constructor BARU yang lengkap
                    UnitStats stats = new UnitStats(name, hp, mp, atk, def, spd);

                    // Load progress tambahan (Level, Exp, dll)
                    // Kita perlu buat method setter khusus di UnitStats untuk load ini
                    stats.setLevel(root.getInt("level", 1)); // Anda perlu buat setter ini di UnitStats
                    stats.setManaCrystals(root.getInt("manaCrystals", 0)); // Perlu setter ini juga

                    // Panggil callback ke Main Thread (PENTING: LibGDX UI harus di main thread)
                    Gdx.app.postRunnable(() -> callback.onSuccess(stats));

                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onFail(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFail(t));
            }

            @Override
            public void cancelled() {
                // Handle cancelled
            }
        });
    }
}
