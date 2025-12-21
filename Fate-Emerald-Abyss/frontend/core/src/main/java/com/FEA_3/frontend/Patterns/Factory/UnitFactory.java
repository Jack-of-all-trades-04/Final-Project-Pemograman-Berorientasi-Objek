package com.FEA_3.frontend.Patterns.Factory;

import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Patterns.Strategy.SmartAIStrategy;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SkillDatabase;
import com.FEA_3.frontend.Utils.UnitDatabase;
import com.badlogic.gdx.graphics.Texture;

public class UnitFactory {

    // Helper: Logic penentuan Level disini
    private static int calculateEnemyLevel(EnemyType type, UnitStats playerStats) {
        int pLvl = playerStats.getLevel();
        int pChap = playerStats.getUnlockedChapter();

        switch (type) {
            // --- A. GRINDING ENEMIES (Scale: Player Level atau +1) ---
            case ANOMIMUS:
            case BEELING:
            case SLIME:
            case GOLEM:
                // 50% kemungkinan sama, 50% kemungkinan level + 1
                return pLvl + (Math.random() > 0.5 ? 1 : 0);

            // --- B. STORY BOSSES (Logic Khusus) ---
            case ASSASSIN:
                // Prologue (Ch 1) = Lvl 1. Chapter 3 dst = Lvl 15.
                if (pChap >= 3) return 15;
                return 1;

            case LANCER: return 5;
            case RIDER: return 10;
            case PRETENDER: return 15;

            // --- C. AREA BOSSES (Fixed Level) ---
            case FARHAT: return 10;
            case MANDA: return 20;

            default: return 1;
        }
    }

    // UPDATE: Method ini sekarang butuh data Player
    public static GameUnit createEnemy(EnemyType type, UnitStats playerStats) {
        // 1. Hitung Level
        int level = calculateEnemyLevel(type, playerStats);

        // 2. Minta Stats dari Database
        UnitStats stats = UnitDatabase.createEnemyStats(type, level);

        // 3. Buat Unit & Strategy
        GameUnit enemy = new GameUnit(stats);
        enemy.setStrategy(new SmartAIStrategy());

        // 4. Load Skill
        SkillDatabase.loadEnemySkills(enemy, type);

        return enemy;
    }

    public static void loadSkillsForPlayer(GameUnit hero) {
        SkillDatabase.loadSaberSkills(hero);
    }

    public static int getEnemyFrameCount(EnemyType type) {
        switch (type) {
            // Sesuaikan angka ini dengan jumlah gambar di Sprite Sheet Anda!

            case ASSASSIN:  return 10;
            case LANCER:    return 8;
            case RIDER:     return 7;
            case PRETENDER: return 3;

            // Bosses
            case FARHAT:    return 1;
            case MANDA:     return 1;

            // Grinding Mobs
            case ANOMIMUS:  return 1;
            case BEELING:   return 1;
            case SLIME:     return 1;
            case GOLEM:     return 1;

            default: return 1; // Fallback (Gambar diam/statis)
        }
    }

    public static Texture getEnemyTexture(EnemyType type) {
        // ... (Kode texture Anda tetap sama, tidak perlu diubah) ...
        String path;
        switch (type) {
            case ASSASSIN: path = "Entity/Enemy/Assasin/Assasin.png"; break;
            case LANCER: path = "Entity/Enemy/Lancer/Lancer.png"; break;
            case RIDER: path = "Entity/Enemy/Rider/Rider.png"; break;
            case PRETENDER: path = "Entity/Enemy/Pretender/Pretender.png"; break;
            case ANOMIMUS: path = "Entity/Enemy/Anomimus/Idle/Idle0.png"; break;
            case BEELING: path = "Entity/Enemy/Beeling/Idle/Idle0.png"; break;
            case FARHAT: path = "Entity/Enemy/Farhat/Idle/Idle (1).png"; break;
            case SLIME: path = "Entity/Enemy/Slime/SlimeBasic_00007.png"; break;
            case GOLEM: path = "Entity/Enemy/Golem/Idle/Idle0.png"; break;
            case MANDA: path = "Entity/Enemy/Manda/Idle/Warrior_Idle_1.png"; break;
            default: path = "Entity/Enemy/Idle1.png"; break;
        }
        try {
            return ResourceManager.getInstance().getTexture(path);
        } catch (Exception e) {
            System.err.println("Texture not found: " + path);
            return ResourceManager.getInstance().getTexture("Entity/Enemy/Idle1.png");
        }
    }
}
