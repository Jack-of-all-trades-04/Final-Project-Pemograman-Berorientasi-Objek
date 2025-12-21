package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Patterns.Factory.UnitFactory;
import com.FEA_3.frontend.UI.DamagePopup;
import com.FEA_3.frontend.UI.VisualEffectActor;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BattleRenderer {
    private Stage stage;
    private Texture backgroundTexture;
    private Animation<TextureRegion> heroAnim, enemyAnim;

    // Posisi Unit
    private float heroX, heroY, enemyX, enemyY;

    public BattleRenderer(Stage stage, String bgPath, EnemyType enemyType, float w, float h) {
        this.stage = stage;

        // 1. Load Background
        try {
            backgroundTexture = new Texture(Gdx.files.internal(bgPath));
        } catch (Exception e) {
            backgroundTexture = ResourceManager.getInstance().getTexture("Background/Temps.png");
        }

        // 2. Load Animasi Unit
        heroAnim = createAnimation("Entity/Player/Saber-Battle.png", 4, 0.15f);
        Texture enemyTex = UnitFactory.getEnemyTexture(enemyType);

        // B. Ambil Jumlah Frame dari Factory (Pastikan method ini ada di UnitFactory)
        int enemyFrameCount = UnitFactory.getEnemyFrameCount(enemyType);

        // C. Buat Animasi
        if (enemyFrameCount > 1) {
            // Jika animasi (Assassin 6 frame, Rider 8 frame, dll)
            enemyAnim = createAnimationFromTexture(enemyTex, enemyFrameCount, 0.15f);
        } else {
            // Jika gambar diam (1 frame)
            TextureRegion[][] tmp = TextureRegion.split(enemyTex, enemyTex.getWidth(), enemyTex.getHeight());
            enemyAnim = new Animation<>(0.1f, tmp[0][0]);
        }

        // 3. Setup Posisi
        float uiPanelHeight = h * 0.3f;
        float groundLevel = uiPanelHeight + 30;
        heroX = w * 0.15f;
        heroY = groundLevel;
        enemyX = w * 0.65f;
        enemyY = groundLevel;
    }

    private Animation<TextureRegion> createAnimationFromTexture(Texture sheet, int cols, float frameDuration) {
        // Hitung lebar per frame: Lebar Total / Jumlah Kolom
        int tileWidth = sheet.getWidth() / cols;
        int tileHeight = sheet.getHeight();

        // Potong gambar
        TextureRegion[][] tmp = TextureRegion.split(sheet, tileWidth, tileHeight);

        // Masukkan ke Array 1 Dimensi
        TextureRegion[] frames = new TextureRegion[cols];
        for (int i = 0; i < cols; i++) {
            frames[i] = tmp[0][i];
        }

        return new Animation<>(frameDuration, frames);
    }

    public void render(Batch batch, GameUnit hero, GameUnit enemy) {
        // Draw Background
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // Draw Units
        drawUnit(batch, heroAnim, heroX, heroY, hero, true); // Hero = IsPlayer (True)
        drawUnit(batch, enemyAnim, enemyX, enemyY, enemy, false); // Enemy

        batch.end();
    }

    private void drawUnit(Batch batch, Animation<TextureRegion> anim, float baseX, float baseY, GameUnit unit, boolean isPlayer) {
        float drawX = baseX;
        float drawY = baseY;
        TextureRegion currentFrame = anim.getKeyFrame(unit.getStateTimer(), true);

        batch.setColor(Color.WHITE);

        // --- Logic Animasi (Attack/Hurt) ---
        switch (unit.getState()) {
            case ATTACK:
                float progress = Math.min(1.0f, unit.getStateTimer() / 0.5f);
                float attackOffset = 100f * (float)Math.sin(progress * Math.PI);
                drawX += isPlayer ? attackOffset : -attackOffset;
                break;
            case HURT:
                if ((int)(unit.getStateTimer() * 20) % 2 == 0) batch.setColor(Color.RED);
                else batch.setColor(Color.WHITE);
                drawX += Math.random() * 10 - 5;
                break;
            case DEAD:
                batch.setColor(Color.GRAY);
                drawY -= 10;
                break;
            case DEFEND:
                batch.setColor(Color.CYAN);
                break;
        }

        // Flip Logic
        boolean flipX = isPlayer;
        if (currentFrame.isFlipX() != flipX) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, drawX, drawY, 100, 100, 200, 200, 1, 1, 0);
        batch.setColor(Color.WHITE);
    }

    public void spawnDamageNumber(int amount, boolean isCrit, boolean isPlayerTarget) {
        String text = (amount == 0) ? "MISS" : String.valueOf(amount);
        DamagePopup popup = new DamagePopup(text, ResourceManager.getInstance().getSkin(), isCrit, false);

        float targetX = isPlayerTarget ? heroX : enemyX;
        float targetY = isPlayerTarget ? heroY : enemyY;
        float randomX = targetX + (float)(Math.random() * 40 - 10);

        popup.setPosition(randomX + 50, targetY + 150);
        stage.addActor(popup);
    }

    public void playSkillEffect(String effectName, boolean onPlayer) {
        String path = "";
        int frames = 4;
        float x = onPlayer ? heroX : enemyX;
        float y = onPlayer ? heroY : enemyY;

        switch(effectName) {
            case "Fireball": case "Firestorm": path = "VFX/Fireball.png"; frames = 5; break;
            case "Divine Light": path = "VFX/Divine Light.png"; frames = 4; break;
            case "Blizzard": path = "VFX/Blizzard.png"; frames = 6; break;
            case "Multi Slice": path = "VFX/Slash.png"; frames = 3; break;
            default: return;
        }

        try {
            Animation<TextureRegion> anim = createAnimation(path, frames, 0.08f);
            VisualEffectActor effectActor = new VisualEffectActor(anim, x, y, false);
            stage.addActor(effectActor);
        } catch (Exception e) { /* Ignore */ }
    }

    private Animation<TextureRegion> createAnimation(String path, int cols, float duration) {
        Texture sheet = ResourceManager.getInstance().getTexture(path);
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);
        return new Animation<>(duration, frames);
    }
}
