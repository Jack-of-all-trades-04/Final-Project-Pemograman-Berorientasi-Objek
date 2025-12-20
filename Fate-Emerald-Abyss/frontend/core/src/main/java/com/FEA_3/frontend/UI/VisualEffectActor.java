package com.FEA_3.frontend.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class VisualEffectActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private boolean loop = false;

    public VisualEffectActor(Animation<TextureRegion> anim, float x, float y, boolean loop) {
        this.animation = anim;
        this.loop = loop;
        setPosition(x, y);
        // Ukuran default efek (sesuaikan dengan ukuran sprite Anda)
        setSize(100, 100);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        // Jika animasi selesai dan tidak looping, hapus actor ini
        if (!loop && animation.isAnimationFinished(stateTime)) {
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, loop);
        if (currentFrame != null) {
            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
        }
    }
}
