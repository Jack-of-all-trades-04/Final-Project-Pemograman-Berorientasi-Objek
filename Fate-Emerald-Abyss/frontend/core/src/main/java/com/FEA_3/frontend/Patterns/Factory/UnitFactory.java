package com.FEA_3.frontend.Patterns.Factory;

import com.FEA_3.frontend.Core.EnemyType;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Patterns.Strategy.AggressiveStrategy;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.FEA_3.frontend.Utils.SkillDatabase;
import com.badlogic.gdx.graphics.Texture;

public class UnitFactory {

    public static GameUnit createEnemy(EnemyType type) {
        GameUnit enemy = null;
        UnitStats stats = null;

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

    public static void loadSkillsForPlayer(GameUnit hero) {
        // Kita delegasikan tugasnya ke SkillDatabase
        SkillDatabase.loadSaberSkills(hero);
    }

    public static Texture getEnemyTexture(EnemyType type) {
        // Anda bisa tambahkan switch case disini jika musuh punya gambar beda-beda
        return ResourceManager.getInstance().getTexture("Entity/Enemy/Idle1.png");
    }
}
