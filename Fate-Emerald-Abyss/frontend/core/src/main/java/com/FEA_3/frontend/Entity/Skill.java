package com.FEA_3.frontend.Entity;

// Class untuk data skill
public class Skill {
    private String name;
    private int unlockLevel;
    private int manaCost;
    private SkillType type;
    private SkillEffect effect;

    public enum SkillType {
        ACTIVE, // Dipakai manual (Button)
        PASSIVE // Otomatis aktif (Buff stat permanen)
    }

    // Interface untuk logika skill yang fleksibel
    public interface SkillEffect {
        void apply(GameUnit user, GameUnit target);
    }

    public Skill(String name, int unlockLevel, int manaCost, SkillType type, SkillEffect effect) {
        this.name = name;
        this.unlockLevel = unlockLevel;
        this.manaCost = manaCost;
        this.type = type;
        this.effect = effect;
    }

    // Getters
    public String getName() { return name; }
    public int getUnlockLevel() { return unlockLevel; }
    public int getManaCost() { return manaCost; }
    public SkillType getType() { return type; }
    public void use(GameUnit user, GameUnit target) {
        effect.apply(user, target);
    }
}
