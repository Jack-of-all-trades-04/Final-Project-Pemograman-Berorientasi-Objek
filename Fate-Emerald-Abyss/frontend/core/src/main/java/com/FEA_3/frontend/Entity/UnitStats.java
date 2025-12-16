package com.FEA_3.frontend.Entity;

// Ini adalah komponen data mentah Unit
public class UnitStats {
    private String name;
    private int maxHp;
    private int attackPower;

    // Data Dinamis
    private int currentHp;
    private int level;
    // Data Milik Player
    private int currentExp;
    private int maxExp;
    private int manaCrystals; // Currency

    // Data Milik Musuh (Reward jika dikalahkan)
    private int expReward;
    private int crystalReward;
    // ... data lain seperti EXP, Defense, dll.

    public UnitStats(String name, int maxHp, int attackPower) {
        this.name = name;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.currentHp = maxHp; // Default: HP penuh saat dibuat
        this.level = 1;
        this.currentExp = 0;
        this.maxExp = 100;
        this.level = 1;
        this.manaCrystals = 0;
    }

    // --- Getter & Setter ---
    // (Gunakan Getter/Setter untuk semua field)

    public String getName() {
        return name;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getExpReward() { return expReward; }
    public void setExpReward(int expReward) { this.expReward = expReward; }

    public int getCrystalReward() { return crystalReward; }
    public void setCrystalReward(int crystalReward) { this.crystalReward = crystalReward; }

    public int getManaCrystals() { return manaCrystals; }
    public void addManaCrystals(int amount) { this.manaCrystals += amount; }

    public void addExp(int amount) {
        this.currentExp += amount;
        // Logic Level Up sederhana
        if (this.currentExp >= maxExp) {
            this.level++;
            this.currentExp -= maxExp;
            this.maxExp *= 1.5; // Butuh exp lebih banyak untuk level selanjutnya
            System.out.println("LEVEL UP! Now Level " + level);
            // Nanti bisa tambah stat HP/Attack disini
        }
    }
    // ...
}
