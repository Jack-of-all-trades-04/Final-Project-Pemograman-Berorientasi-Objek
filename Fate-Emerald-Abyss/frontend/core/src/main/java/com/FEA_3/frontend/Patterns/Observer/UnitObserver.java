package com.FEA_3.frontend.Patterns.Observer;

public interface UnitObserver {
    void onHealthChanged(int currentHp, int maxHp);
    void onDamageTaken(int amount, boolean isCrit);
}
