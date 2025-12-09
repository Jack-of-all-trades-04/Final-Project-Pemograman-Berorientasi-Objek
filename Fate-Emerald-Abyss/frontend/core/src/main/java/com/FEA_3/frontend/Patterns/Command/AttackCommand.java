package com.FEA_3.frontend.Patterns.Command;

import com.FEA_3.frontend.Core.UnitState;
import com.FEA_3.frontend.Utils.GameUnit;

public class AttackCommand implements Command {
    private GameUnit attacker;
    private GameUnit target;

    public AttackCommand(GameUnit attacker, GameUnit target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void execute() {
        // 1. Ubah State Penyerang -> ATTACK
        attacker.setState(UnitState.ATTACK);

        // 2. Hitung Damage
        int dmg = attacker.getAttackPower();

        // 3. Ubah State Korban -> HURT (Lewat takeDamage atau set manual)
        target.takeDamage(dmg);
        // Tips: Bisa tambahkan logic di takeDamage() milik GameUnit agar otomatis set HURT
        target.setState(UnitState.HURT);
    }
}
