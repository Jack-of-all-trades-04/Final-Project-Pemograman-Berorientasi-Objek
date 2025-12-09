package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HealthBar extends Actor implements UnitObserver {
    private Texture background; // Kotak Merah (Darah kosong)
    private Texture foreground; // Kotak Hijau (Darah sisa)
    private float currentPct = 1.0f; // Persentase HP (0.0 - 1.0)

    public HealthBar(float width, float height) {
        setSize(width, height);

        // Trik membuat Texture 1 pixel warna secara coding (tanpa butuh file aset png)
        background = createTexture(Color.RED);
        foreground = createTexture(Color.GREEN);
    }

    private Texture createTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    @Override
    public void onHealthChanged(int currentHp, int maxHp) {
        // Hitung persentase baru
        this.currentPct = (float) currentHp / maxHp;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Gambar Background (Merah Full)
        batch.draw(background, getX(), getY(), getWidth(), getHeight());

        // Gambar Foreground (Hijau sesuai persentase)
        // Lebarnya dikali currentPct
        if (currentPct > 0) {
            batch.draw(foreground, getX(), getY(), getWidth() * currentPct, getHeight());
        }
    }
}
