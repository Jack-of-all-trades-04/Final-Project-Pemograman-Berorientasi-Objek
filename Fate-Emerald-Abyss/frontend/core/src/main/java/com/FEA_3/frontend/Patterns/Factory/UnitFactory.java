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

    // Helper untuk menentukan level musuh (Bisa dibuat random range nanti)
    private static int getLevelForEnemy(EnemyType type) {
        switch (type) {
            case ASSASSIN: return 1; // Atau 1 sesuai progress story
            case LANCER: return 5;
            case RIDER: return 10;
            case PRETENDER: return 15;
            case FARHAT: return 10;
            case MANDA: return 20;
            default: return 1; // Slime, Anomimus, Beeling start lvl 1
        }
    }

    public static GameUnit createEnemy(EnemyType type) {
        // 1. Tentukan Level (Bisa statis atau dinamis tergantung area map nanti)
        int level = getLevelForEnemy(type);

        // 2. Minta Stats dari Database
        UnitStats stats = UnitDatabase.createEnemyStats(type, level);

        // 3. Buat Unit
        GameUnit enemy = new GameUnit(stats);
        enemy.setStrategy(new SmartAIStrategy());

        // 4. Load Skill Musuh dari Database
        SkillDatabase.loadEnemySkills(enemy, type);

        return enemy;
    }

    public static void loadSkillsForPlayer(GameUnit hero) {
        SkillDatabase.loadSaberSkills(hero);
    }

    public static Texture getEnemyTexture(EnemyType type) {
        String path;

        switch (type) {
            // --- MAIN QUEST BOSSES ---
            case ASSASSIN:
                path = "Entity/Enemy/Assasin/Idle1.png";
                break;
            case LANCER:
                path = "Entity/Enemy/Lancer/Idle1.png";
                break;
            case RIDER:
                path = "Entity/Enemy/Rider/Idle1.png";
                break;
            case PRETENDER:
                path = "Entity/Enemy/Pretender/Idle1.png";
                break;

            // --- FOREST ENEMIES ---
            case ANOMIMUS:
                path = "Entity/Enemy/Anomimus/Idle/Idle0.png"; // Musuh topi hitam kecil di screenshot
                break;
            case BEELING:
                path = "Entity/Enemy/Beeling/Idle/Idle0.png"; // Misal musuh lebah
                break;
            case FARHAT: // Boss Forest
                path = "Entity/Enemy/Farhat/Idle/Idle (1).png";
                break;

            // --- RUINS ENEMIES ---
            case SLIME:
                path = "Entity/Enemy/Slime/SlimeBasic_00007.png";
                break;
            case GOLEM:
                path = "Entity/Enemy/Golem/Idle/Idle0.png";
                break;
            case MANDA: // Boss Ruins
                path = "Entity/Enemy/Manda/Idle/Warrior_Idle_1.png";
                break;

            // --- DEFAULT / FALLBACK ---
            default:
                path = "Entity/Enemy/Idle1.png"; // Gambar default jika tipe belum ada
                break;
        }

        // Return Texture dari ResourceManager
        // Pastikan Anda menangani error jika gambar tidak ditemukan di ResourceManager
        try {
            return ResourceManager.getInstance().getTexture(path);
        } catch (Exception e) {
            System.err.println("Texture not found: " + path + ". Using default.");
            return ResourceManager.getInstance().getTexture("Entity/Enemy/Idle1.png");
        }
    }
}
