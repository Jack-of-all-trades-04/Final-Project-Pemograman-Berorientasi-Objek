package com.FEA_3.frontend.Patterns.Factory;

import com.FEA_3.frontend.Core.EnemyType;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Patterns.Strategy.AggressiveStrategy;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.graphics.Texture;

public class UnitFactory {

    public static GameUnit createEnemy(EnemyType type) {
        GameUnit enemy = null;
        UnitStats stats = null;
        // Buat Stats Player (HP, MP, ATK, DEF, SPD)
        stats = new UnitStats("Artoria", 1000, 200, 50, 20, 10);
        GameUnit hero = new GameUnit(stats);

        // --- DAFTAR SKILL SABER ---

        // LEVEL 4 (ACTIVE): Heavy Slash - Deal 150% ATK
        hero.addSkill(new Skill("Mana Burst", 4, 30, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 1.5);
            target.takeDamage(dmg, false);
            System.out.println("Used Mana Burst!");
        }));

        // LEVEL 5 (PASSIVE): Magic Resistance - Def +10
        hero.addSkill(new Skill("Magic Res", 5, 0, Skill.SkillType.PASSIVE, (user, target) -> {
            // Naikkan Defense permanen (Hati-hati logic ini harus dipanggil sekali aja)
            // Cara simpel: Buff status
            System.out.println("Passive Active: Def UP");
        }));

        // LEVEL 9 (ACTIVE): Excalibur (Mini) - Deal 200% ATK
        hero.addSkill(new Skill("Excalibur", 9, 80, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = user.getStats().getAttackPower() * 2;
            target.takeDamage(dmg, true); // Force Crit
        }));

        // Cek unlock awal (kalau start level > 1)
        hero.checkUnlockSkills();// Siapkan wadah stats

        switch (type) {
            case SKELETON:
                // 1. Buat Stats dulu
                stats = new UnitStats("Skeleton", 200, 30,10,10,10);
                // 2. Masukkan Stats ke GameUnit
                enemy = new GameUnit(stats);
                stats.setExpReward(50);       // Dapat 50 EXP
                stats.setCrystalReward(10);   // Dapat 10 Crystal

                // 3. Set Strategy
                enemy.setStrategy(new AggressiveStrategy());
                break;

            case SLIME:
                stats = new UnitStats("Slime", 300, 10,10,10,10);
                enemy = new GameUnit(stats);
                stats.setExpReward(50);       // Dapat 50 EXP
                stats.setCrystalReward(10);   // Dapat 10 Crystal
                enemy.setStrategy(new AggressiveStrategy());
                break;

            case DRAGON_BOSS:
                stats = new UnitStats("Bahamut", 2000, 150,10,10,10);
                enemy = new GameUnit(stats);
                stats.setExpReward(50);       // Dapat 50 EXP
                stats.setCrystalReward(10);   // Dapat 10 Crystal
                enemy.setStrategy(new AggressiveStrategy());
                break;
        }

        return enemy;
    }

    // Method getEnemyTexture tetap sama...
    public static Texture getEnemyTexture(EnemyType type) {
        return ResourceManager.getInstance().getTexture("Entity/Enemy/Idle1.png");
    }
}
