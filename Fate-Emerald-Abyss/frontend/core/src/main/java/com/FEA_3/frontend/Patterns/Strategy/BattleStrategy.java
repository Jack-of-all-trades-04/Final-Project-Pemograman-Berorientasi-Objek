package com.FEA_3.frontend.Patterns.Strategy;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Patterns.Command.Command;

public interface BattleStrategy {
    // Dulu: void act(GameUnit self, GameUnit target);
    // Sekarang: Mengembalikan Command
    Command decideAction(GameUnit self, GameUnit target);
}
