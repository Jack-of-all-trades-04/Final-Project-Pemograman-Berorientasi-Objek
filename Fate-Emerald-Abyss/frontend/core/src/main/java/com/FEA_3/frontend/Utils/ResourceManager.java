package com.FEA_3.frontend.Utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourceManager {
    public static final ResourceManager instance = new ResourceManager();
    private AssetManager assetManager;

    private ResourceManager() {
        assetManager = new AssetManager();
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    public void loadAssets() {
        // Load Karakter
        assetManager.load("Entity/Player/Temp.png", Texture.class);
        assetManager.load("Entity/Enemy/Idle1.png", Texture.class);

        // Load UI Skin (PENTING untuk tombol)
        assetManager.load("Utility/uiskin.json", Skin.class);

        assetManager.load("Background/Temps.png", Texture.class);

        assetManager.finishLoading(); // Tunggu sampai semua selesai dimuat
    }

    public Texture getTexture(String name) {
        return assetManager.get(name, Texture.class);
    }

    public Skin getSkin() {
        return assetManager.get("Utility/uiskin.json", Skin.class);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
