package com.FEA_3.frontend.Entity;

public class Skill {
    private String name;
    private String description;
    private int unlockLevel;
    private int manaCost;
    private SkillType type;
    private SkillEffect effect;
    private String iconPath;

    public enum SkillType { ACTIVE, PASSIVE }

    public interface SkillEffect {
        void apply(GameUnit user, GameUnit target);
    }

    // Update Constructor
    public Skill(String name, String description, String iconPath, int unlockLevel, int manaCost, SkillType type, SkillEffect effect) {
        this.name = name;
        this.description = description;
        this.iconPath = iconPath; // Simpan path
        this.unlockLevel = unlockLevel;
        this.manaCost = manaCost;
        this.type = type;
        this.effect = effect;
    }

    // Getter Baru
    public String getDescription() { return description; }

    // Getter Lama
    public String getName() { return name; }
    public int getUnlockLevel() { return unlockLevel; }
    public int getManaCost() { return manaCost; }
    public String getIconPath() {
        return iconPath;
    }
    public SkillType getType() { return type; }
    public void use(GameUnit user, GameUnit target) { effect.apply(user, target); }
}
