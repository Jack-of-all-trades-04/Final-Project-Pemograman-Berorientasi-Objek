package FEA.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
public class PlayerData {

    @Id
    private String id; // Username

    private String characterName;

    // --- PROGRESSION ---
    private int level;
    private int currentExp;
    private int maxExp;
    private int manaCrystals; // Currency Uang

    // --- BASIC STATS ---
    private int maxHp;
    private int currentHp;
    private int maxMp;      // NEW
    private int currentMp;  // NEW

    // --- COMBAT STATS ---
    private int attackPower;
    private int defense;    // NEW
    private int speed;      // NEW

    // --- PROBABILITY (Simpan sebagai float/double) ---
    private float critChance; // NEW
    private float critDamage; // NEW
    private float accuracy;   // NEW

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    // Constructor Kosong (Wajib JPA)
    public PlayerData() {}

    // Constructor untuk New Game
    public PlayerData(String id, String characterName) {
        this.id = id;
        this.characterName = characterName;

        // --- LEVEL 1 STATS (Sesuai Spreadsheet) ---
        this.level = 1;
        this.currentExp = 0;
        this.maxExp = 100;
        this.manaCrystals = 0;

        // Stats Dasar
        this.maxHp = 98;    // Rumus: 80 + 18*1
        this.currentHp = 98;
        this.maxMp = 148;   // Rumus: 120 + 28*1
        this.currentMp = 148;

        this.attackPower = 31; // Rumus: 25 + 6*1
        this.defense = 12;     // Rumus: 10 + 2.5*1
        this.speed = 12;       // Rumus: 12 + 0.8*1

        // Stats Probabilitas (Float)
        this.critChance = 5.5f;   // 5 + 0.5*1
        this.critDamage = 151.0f; // 150 + 1*1
        this.accuracy = 90.5f;    // 90 + 0.5*1
    }

    // --- GETTERS & SETTERS (Generate All via IDE: Alt+Insert) ---
    // Pastikan Anda men-generate Getter & Setter untuk SEMUA field baru di atas
    // Contoh:
    public int getMaxMp() { return maxMp; }
    public void setMaxMp(int maxMp) { this.maxMp = maxMp; }
    // ... Lanjutkan untuk field lainnya ...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String name) { this.characterName = name; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

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

    public int getManaCrystals() {
        return manaCrystals;
    }

    public void setManaCrystals(int manaCrystals) {
        this.manaCrystals = manaCrystals;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(int currentMp) {
        this.currentMp = currentMp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getCritChance() {
        return critChance;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public float getCritDamage() {
        return critDamage;
    }

    public void setCritDamage(float critDamage) {
        this.critDamage = critDamage;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
    // ... dst (Jangan lupa getter setter sisanya)

    // Inventory

    public List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
        // Helper: Pastikan setiap item tahu siapa pemiliknya
        if (inventoryItems != null) {
            for (InventoryItem item : inventoryItems) {
                item.setPlayer(this);
            }
        }
    }
}