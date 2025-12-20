package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class EffectWidget extends Table {
    private Skin skin;
    private float iconSize = 24f; // Ukuran icon kecil (24x24 px)

    public EffectWidget(Skin skin) {
        this.skin = skin;
        this.left().top(); // Icon mulai dari kiri
    }

    /**
     * Menambahkan Icon Buff/Debuff ke panel.
     * @param iconPath Path gambar icon (misal: "Icons/Shield.png")
     * @param name Nama efek untuk tooltip (opsional, saat ini belum dipakai)
     */
    public void addEffect(String iconPath, String name) {
        Image icon;

        try {
            // Coba load texture icon
            Texture tex = ResourceManager.getInstance().getTexture(iconPath);
            icon = new Image(tex);
        } catch (Exception e) {
            // Fallback: Jika gambar tidak ada, buat kotak warna kuning (Placeholder)
            icon = createPlaceholderIcon(name);
        }

        // Tambahkan ke table
        this.add(icon).size(iconSize).padRight(5);
    }

    // Method untuk menghapus semua icon (misal saat reset turn)
    public void clearEffects() {
        this.clearChildren(); // Hapus semua actor di dalam table ini
    }

    // Helper bikin kotak warna jika icon belum siap
    private Image createPlaceholderIcon(String name) {
        Pixmap p = new Pixmap((int)iconSize, (int)iconSize, Pixmap.Format.RGBA8888);
        p.setColor(Color.ORANGE);
        p.fill();
        // Gambar huruf pertama buff di tengah (Manual pixel art dikit :D) - Skip aja biar simple
        Texture t = new Texture(p);
        p.dispose();

        Image img = new Image(t);
        // Bisa tambah tooltip listener disini nanti
        return img;
    }
}
