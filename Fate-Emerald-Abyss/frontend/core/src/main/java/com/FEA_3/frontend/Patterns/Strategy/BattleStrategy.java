package com.FEA_3.frontend.Patterns.Strategy;

import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Entity.GameUnit;

public interface BattleStrategy {
    // AI memutuskan mau melakukan Command apa (Serang? Heal?)
    Command decideAction(GameUnit self, GameUnit target);
}
