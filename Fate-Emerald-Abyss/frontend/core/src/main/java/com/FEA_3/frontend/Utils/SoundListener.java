package com.FEA_3.frontend.Utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

// Listener custom yang otomatis bunyi saat Hover/Click
public class SoundListener extends ClickListener {

    private Sound clickSound;
    private Sound hoverSound;

    public SoundListener() {
        clickSound = ResourceManager.getInstance().getSound("Audio/Sound_Effect/ui_click1.wav");
        hoverSound = ResourceManager.getInstance().getSound("Audio/Sound_Effect/ui_hover1.wav");
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (clickSound != null) clickSound.play(1.0f); // Play volume 100%
        super.clicked(event, x, y);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        // Cek pointer == -1 agar sound hanya main saat mouse masuk, bukan saat drag
        if (pointer == -1 && hoverSound != null) {
            hoverSound.play(0.4f);
        }
        super.enter(event, x, y, pointer, fromActor);
    }
}
