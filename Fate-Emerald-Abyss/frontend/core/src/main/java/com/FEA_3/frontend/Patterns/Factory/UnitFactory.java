package com.FEA_3.frontend.Patterns.Factory;

import com.FEA_3.frontend.Core.EnemyType;
import com.FEA_3.frontend.Patterns.Strategy.AggressiveStrategy;
//import com.FEA_3.frontend.Patterns.Strategy.DefensiveStrategy;
import com.FEA_3.frontend.Utils.GameUnit;
import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.graphics.Texture;

public class UnitFactory {

    public static GameUnit createEnemy(EnemyType type) {
        GameUnit enemy = null;

        switch (type) {
            case SKELETON:
                // Kroco biasa: HP Kecil, Attack Sedang
                enemy = new GameUnit("Skeleton", 200, 30);
                enemy.setStrategy(new AggressiveStrategy()); // AI Agresif
                break;

            case SLIME:
                // Musuh lemah: HP Tebal, Attack Lemah
                enemy = new GameUnit("Slime", 300, 10);
                enemy.setStrategy(new AggressiveStrategy());
                // Nanti bisa ganti strategi 'Passive' jika ada
                break;

            case DRAGON_BOSS:
                // BOSS: HP Tebal, Attack Sakit
                enemy = new GameUnit("Bahamut", 2000, 150);
                enemy.setStrategy(new AggressiveStrategy());
                break;
        }

        return enemy;
    }

    // Helper untuk mengambil texture berdasarkan tipe (Opsional, agar rapi di Screen)
    public static Texture getEnemyTexture(EnemyType type) {
        // Disini kita mapping Tipe -> File Gambar
        // Sementara pakai placeholder "Idle1.png" untuk semua
        // Nanti bisa di-switch case juga
        return ResourceManager.getInstance().getTexture("Entity/Enemy/Idle1.png");
    }
}
