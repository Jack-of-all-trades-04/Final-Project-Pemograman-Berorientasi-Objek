package com.FEA_3.frontend.Patterns.Factory;

import com.FEA_3.frontend.Core.EnemyType;
import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Patterns.Strategy.AggressiveStrategy;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.graphics.Texture;

public class UnitFactory {

    public static GameUnit createEnemy(EnemyType type) {
        GameUnit enemy = null;
        UnitStats stats = null; // Siapkan wadah stats

        switch (type) {
            case SKELETON:
                // 1. Buat Stats dulu
                stats = new UnitStats("Skeleton", 200, 30);
                // 2. Masukkan Stats ke GameUnit
                enemy = new GameUnit(stats);

                // 3. Set Strategy
                enemy.setStrategy(new AggressiveStrategy());
                break;

            case SLIME:
                stats = new UnitStats("Slime", 300, 10);
                enemy = new GameUnit(stats);
                enemy.setStrategy(new AggressiveStrategy());
                break;

            case DRAGON_BOSS:
                stats = new UnitStats("Bahamut", 2000, 150);
                enemy = new GameUnit(stats);
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
