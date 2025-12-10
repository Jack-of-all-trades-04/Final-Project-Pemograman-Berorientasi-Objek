package com.FEA_3.frontend.Entity;

// Ini adalah komponen data mentah Unit
public class UnitStats {
    private String name;
    private int maxHp;
    private int attackPower;

    // Data Dinamis
    private int currentHp;
    private int level;
    // ... data lain seperti EXP, Defense, dll.

    public UnitStats(String name, int maxHp, int attackPower) {
        this.name = name;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.currentHp = maxHp; // Default: HP penuh saat dibuat
        this.level = 1;
    }

    // --- Getter & Setter ---
    // (Gunakan Getter/Setter untuk semua field)
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public String getName() { return name; }
    // ...
}
