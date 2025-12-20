package com.FEA_3.frontend.UI;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EffectWidget extends Table {
    private Skin skin;
    private float iconSize = 24f;

    public EffectWidget(Skin skin) {
        this.skin = skin;
        this.left().top();
    }

    public void addEffect(String iconPath, String name) {
        Image icon;
        try {
            Texture tex = ResourceManager.getInstance().getTexture(iconPath);
            icon = new Image(tex);
        } catch (Exception e) {
            icon = createPlaceholderIcon();
        }
        // Tambahkan Tooltip nanti jika perlu
        this.add(icon).size(iconSize).padRight(5);
    }

    public void clearEffects() {
        this.clearChildren();
    }

    private Image createPlaceholderIcon() {
        Pixmap p = new Pixmap((int)iconSize, (int)iconSize, Pixmap.Format.RGBA8888);
        p.setColor(Color.ORANGE); p.fill();
        Texture t = new Texture(p); p.dispose();
        return new Image(t);
    }

    // --- METHOD BARU: SYNC DENGAN GAME UNIT ---
    public void updateFromUnit(GameUnit unit) {
        clearEffects(); // Hapus icon lama dulu

        // Cek satu per satu status di Unit
        if (unit.getBurnTurns() > 0) addEffect("Icons/Fireball.png", "Burn");
        if (unit.getBleedTurns() > 0) addEffect("Icons/Bleed.png", "Bleed"); // Atau icon tetesan darah
        if (unit.isStunned()) addEffect("Icons/Stun.png", "Stun");

        if (unit.getDefenseBuffTurns() > 0) addEffect("Icons/ShieldBuff.png", "Def Up");
        if (unit.getSpeedBuffTurns() > 0) addEffect("Icons/Dodge.png", "Spd Up");

        if (unit.getAccuracyDebuffTurns() > 0) addEffect("Icons/Miss.png", "Acc Down");
        if (unit.getSlowDebuffTurns() > 0) addEffect("Icons/Slow.png", "Slow"); // Icon Es

        if (unit.isHasEndure()) addEffect("Icons/Endure.png", "Endure");
        if (unit.isNextHitGuaranteed()) addEffect("Icons/Eye.png", "Sure Hit");
    }
}
