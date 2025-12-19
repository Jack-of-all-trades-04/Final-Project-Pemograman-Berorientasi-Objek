package com.FEA_3.frontend.Patterns.Command;

import com.FEA_3.frontend.Core.UnitState;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.audio.Sound;

public class AttackCommand implements Command {
    private GameUnit attacker;
    private GameUnit target;

    public AttackCommand(GameUnit attacker, GameUnit target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void execute() {
        // 1. Play Sound
        Sound sfx = ResourceManager.getInstance().getSound("Audio/Sound_Effect/Attack1.wav");
        // Mainkan dengan pitch acak sedikit (0.9 - 1.1) agar tidak terdengar monoton
        long id = sfx.play();
        sfx.setPitch(id, 0.9f + (float)(Math.random() * 0.2f));

        // 2. Logic Attack
        attacker.setState(UnitState.ATTACK);
        int dmg = attacker.getAttackPower();
        target.takeDamage(dmg, false);
        target.setState(UnitState.HURT);
    }
}
