package com.FEA_3.frontend.Entity;

import java.util.ArrayList;
import java.util.List;

public class UnitStats {
    private String name;

    // --- BASIC STATS ---
    private int level;
    private int maxHp;
    private int currentHp;
    private int maxMp;      // NEW
    private int currentMp;  // NEW

    // --- COMBAT STATS ---
    private int attackPower;
    private int defense;    // NEW
    private int speed;      // NEW (0-100)

    // --- PROBABILITY STATS ---
    private float critChance; // 0.0 - 100.0
    private float critDamage; // 100.0 - 200.0 (Multiplier)
    private float accuracy;   // 0 - 100

    // --- PROGRESSION ---
    private int currentExp;
    private int maxExp;
    private int manaCrystals;

    // --- INVENTORY ---
    private List<Consumable> inventory;

    // --- REWARDS ---
    private int expReward;
    private int crystalReward;

    public UnitStats(String name, int hp, int mp, int atk, int def, int spd) {
        this.name = name;
        this.level = 1;
        this.manaCrystals = 0;

        this.maxHp = hp;
        this.currentHp = hp;
        this.maxMp = mp;
        this.currentMp = mp;

        this.attackPower = atk;
        this.defense = def;
        this.speed = spd; // Base speed

        // Default Stats (Bisa di-set manual nanti)
        this.critChance = 5.0f;    // 5% Base
        this.critDamage = 150.0f;  // 150% Base Damage
        this.accuracy = 95.0f;     // 95% Base Hit rate

        this.currentExp = 0;
        this.maxExp = 100;

        this.inventory = new ArrayList<>();
    }

    // --- INVENTORY LOGIC ---
    public List<Consumable> getInventory() {
        return inventory;
    }

    public void setInventory(List<Consumable> inventory) {
        this.inventory = inventory;
    }

    /**
     * Method 1: Dipakai ShopScreen (Deteksi Otomatis)
     * Menambahkan item ke inventory. Jika ada, tambah quantity.
     */
    public void addItem(String itemName, int quantity) {
        // 1. Cek Stack: Jika item sudah ada, cukup tambah jumlahnya
        for (Consumable item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                item.addQuantity(quantity);
                System.out.println("Inventory: " + itemName + " quantity updated to " + item.getQuantity());
                return;
            }
        }

        // 2. Jika item baru, deteksi tipe manual (Simple Logic)
        Consumable.ItemType type = Consumable.ItemType.POTION_HP; // Default
        int value = 0;

        if (itemName.contains("Health") || itemName.contains("HP")) {
            type = Consumable.ItemType.POTION_HP;
            value = 50;
        } else if (itemName.contains("Mana") || itemName.contains("MP")) {
            type = Consumable.ItemType.POTION_MP;
            value = 30;
        } else if (itemName.contains("Iron") || itemName.contains("Elixir") || itemName.contains("DEF")) {
            type = Consumable.ItemType.BUFF_DEF;
            value = 30;
        }

        inventory.add(new Consumable(itemName, type, value, quantity));
        System.out.println("Inventory: Added new item " + itemName);
    }

    /**
     * Method 2: Dipakai NetworkManager (Load Data Server)
     * Menambah item dengan data lengkap.
     */
    public void addItem(String name, Consumable.ItemType type, int value, int qty) {
        for (Consumable item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.addQuantity(qty);
                return;
            }
        }
        inventory.add(new Consumable(name, type, value, qty));
    }

    // --- LOGIC LEVEL UP & SCALING ---
    public void addExp(int amount) {
        if (this.level >= 20) return; // MAX LEVEL 20

        this.currentExp += amount;
        while (this.currentExp >= maxExp && this.level < 20) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.currentExp -= this.maxExp;
        this.maxExp = (int) (this.maxExp * 1.5); // Exp curve

        // --- SCALING STATUS (STATUS++) ---
        // Sesuai request: Level 1-3, 4-8, 10-13, 15-18 naik status
        // Kita simplifikasi: Setiap naik level, status naik fix
        this.maxHp += 50;
        this.maxMp += 10;
        this.attackPower += 5;
        this.defense += 3;

        // Speed scaling (Max 100)
        if (this.speed < 100) this.speed += 2;

        // Pulihkan HP/MP saat level up
        this.currentHp = this.maxHp;
        this.currentMp = this.maxMp;

        System.out.println(name + " LEVEL UP! Now Level " + level);
    }

    // --- MANA SYSTEM ---
    public boolean consumeMana(int amount) {
        if (currentMp >= amount) {
            currentMp -= amount;
            return true;
        }
        return false;
    }

    //

    // --- GETTERS & SETTERS (Generate All) ---
    public int getLevel() { return level; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public float getCritChance() { return critChance; }
    public float getCritDamage() { return critDamage; }
    public float getAccuracy() { return accuracy; }

    // Setter Reward
    public void setExpReward(int exp) { this.expReward = exp; }
    public int getExpReward() { return expReward; }
    public void setCrystalReward(int c) { this.crystalReward = c; }
    public int getCrystalReward() { return crystalReward; }
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(int currentMp) {
        this.currentMp = currentMp;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public void setCritDamage(float critDamage) {
        this.critDamage = critDamage;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }
    // 1. Getter (Untuk ambil jumlah uang)
    public int getManaCrystals() {
        return manaCrystals;
    }

    // 2. Setter (Untuk NetworkManager mengisi uang dari Database)
    public void setManaCrystals(int amount) {
        this.manaCrystals = amount;
    }

    // 3. Helper (Untuk menambah uang saat menang battle)
    public void addManaCrystals(int amount) {
        this.manaCrystals += amount;
    }

    // ... Buat setter lain jika perlu
}
