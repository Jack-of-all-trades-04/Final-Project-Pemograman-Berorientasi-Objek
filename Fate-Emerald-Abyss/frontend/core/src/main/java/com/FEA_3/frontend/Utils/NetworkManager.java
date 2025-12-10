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
                    // JSON format: {"characterName":"Artoria", "maxHp":1000 ...}
                    // Kita parsing manual atau mapping ke UnitStats
                    JsonValue root = new JsonReader().parse(result);

                    String name = root.getString("characterName");
                    int hp = root.getInt("maxHp");
                    int atk = root.getInt("attackPower");

                    // Buat UnitStats dari data server
                    UnitStats stats = new UnitStats(name, hp, atk);

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
