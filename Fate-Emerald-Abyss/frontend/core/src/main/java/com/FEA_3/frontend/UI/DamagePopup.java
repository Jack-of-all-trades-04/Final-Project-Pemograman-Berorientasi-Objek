package com.FEA_3.frontend.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DamagePopup extends Label {

    public DamagePopup(String text, Skin skin, boolean isCrit, boolean isHeal) {
        super(text, skin);

        // 1. Tentukan Warna & Ukuran
        if (isHeal) {
            setColor(Color.GREEN);
            setFontScale(2.5f); // Naikkan dari 1.2 ke 2.5
        } else if (isCrit) {
            setColor(Color.GOLD);
            setFontScale(3.5f); // Naikkan dari 1.5 ke 3.5 (Biar puas!)
            setText(text + "!");
        } else {
            setColor(Color.WHITE);
            setFontScale(2.5f); // Naikkan dari 1.0 ke 2.5
        }

        // 2. Buat Animasi (Sequence: Gerak ke atas + Fade Out + Hapus diri sendiri)
        this.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, 50, 1.5f), // Naik 50 pixel dalam 1 detik
                Actions.fadeOut(1.0f)        // Menghilang perlahan
            ),
            Actions.removeActor() // Hapus dari memori setelah selesai
        ));
    }
}
