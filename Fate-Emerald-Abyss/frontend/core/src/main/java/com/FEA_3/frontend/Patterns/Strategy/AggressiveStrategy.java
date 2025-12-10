package com.FEA_3.frontend.Patterns.Strategy;

import com.FEA_3.frontend.Patterns.Command.AttackCommand;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Entity.GameUnit;

public class AggressiveStrategy implements BattleStrategy {
    @Override
    public Command decideAction(GameUnit self, GameUnit target) {
        System.out.println("AI: GRRR! SAYA AKAN MENYERANG!");
        // AI memilih menggunakan Command Attack
        return new AttackCommand(self, target);
    }
}
