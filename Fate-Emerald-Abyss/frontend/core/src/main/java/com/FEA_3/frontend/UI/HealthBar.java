package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HealthBar extends Actor implements UnitObserver {
    private Texture background;
    private Texture foreground;
    private float currentHealth;
    private float maxHealth;

    public HealthBar(float width, float height) {
        setSize(width, height);

        // Membuat texture bar secara coding (Merah & Hijau)
        // Background (Merah/Kosong)
        Pixmap bgMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgMap.setColor(Color.RED); bgMap.fill();
        background = new Texture(bgMap);

        // Foreground (Hijau/Isi)
        Pixmap fgMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        fgMap.setColor(Color.GREEN); fgMap.fill();
        foreground = new Texture(fgMap);

        // Default awal
        this.maxHealth = 100;
        this.currentHealth = 100;
    }

    // --- IMPLEMENTASI OBSERVER BARU ---
    @Override
    public void onHealthChanged(int currentHp, int maxHp) {
        this.currentHealth = currentHp;
        this.maxHealth = maxHp;
    }

    @Override
    public void onDamageTaken(int amount, boolean isCrit) {

    }

    // Method Helper untuk update manual saat inisialisasi
    public void init(int currentHp, int maxHp) {
        this.currentHealth = currentHp;
        this.maxHealth = maxHp;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Gambar Background (Merah Full)
        batch.draw(background, getX(), getY(), getWidth(), getHeight());

        // Gambar Foreground (Hijau sesuai persentase)
        float progress = currentHealth / maxHealth;
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        batch.draw(foreground, getX(), getY(), getWidth() * progress, getHeight());
    }
}
