package com.FEA_3.frontend.Patterns.Factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UIFactory {

    // Membuat Background Transparan untuk Kotak Dialog
    public static TextureRegionDrawable createBackground(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    // Membuat Kotak Dialog Standar VN (Di bawah layar)
    public static Table createDialogBox(Skin skin) {
        Table table = new Table();
        // Background hitam transparan (Alpha 0.8)
        table.setBackground(createBackground(1, 1, new Color(0, 0, 0, 0.8f)));

        // Ukuran Dialog Box (Full width, tinggi 30% layar)
        table.setSize(800, 200); // Nanti di-resize di Screen
        return table;
    }

    // Membuat Label Teks Standar
    public static Label createLabel(String text, Skin skin) {
        Label label = new Label(text, skin);
        label.setWrap(true); // Agar teks panjang otomatis turun ke bawah
        return label;
    }
}
