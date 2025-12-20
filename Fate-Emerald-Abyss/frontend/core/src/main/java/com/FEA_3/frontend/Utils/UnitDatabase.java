package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Entity.UnitStats;

public class UnitDatabase {

    // Class kecil untuk menyimpan rumus: Base + (Growth * Level)
    private static class StatConfig {
        float base, growth;
        public StatConfig(float base, float growth) { this.base = base; this.growth = growth; }
        public int calculate(int level) { return (int) (base + (growth * level)); }
        public float calculateFloat(int level) { return base + (growth * level); }
    }

    // Struktur Data lengkap untuk 1 Unit
    private static class UnitData {
        String name;
        StatConfig hp, mp, atk, def, spd;
        StatConfig cRate, cDmg, acc;
        StatConfig expReward, crystalReward;

        public UnitData(String name,
                        float hpB, float hpG,
                        float mpB, float mpG,
                        float atkB, float atkG,
                        float defB, float defG,
                        float spdB, float spdG,
                        float crB, float crG,   // Crit Rate
                        float cdB, float cdG,   // Crit Dmg
                        float accB, float accG, // Accuracy
                        float expB, float expG,
                        float cryB, float cryG) {
            this.name = name;
            this.hp = new StatConfig(hpB, hpG);
            this.mp = new StatConfig(mpB, mpG);
            this.atk = new StatConfig(atkB, atkG);
            this.def = new StatConfig(defB, defG);
            this.spd = new StatConfig(spdB, spdG);
            this.cRate = new StatConfig(crB, crG);
            this.cDmg = new StatConfig(cdB, cdG);
            this.acc = new StatConfig(accB, accG);
            this.expReward = new StatConfig(expB, expG);
            this.crystalReward = new StatConfig(cryB, cryG);
        }
    }

    // --- METHOD UTAMA: HITUNG STATS ---
    public static UnitStats createPlayerStats(int level) {
        // DATA SABER (Sesuai Spreadsheet)
        // HP: 80+18*L, MP: 120+28*L, ATK: 25+6*L, DEF: 10+2.5*L, SPD: 12+0.8*L
        // CR: 5+0.5*L, CD: 150+1*L, ACC: 90+0.5*L
        UnitData saber = new UnitData("Artoria",
            80, 18,   120, 28,   25, 6,   10, 2.5f,   12, 0.8f,
            5, 0.5f,  150, 1,    90, 0.5f,
            0, 0,     0, 0); // Player tidak drop exp/crystal

        return buildStats(saber, level);
    }

    public static UnitStats createEnemyStats(EnemyType type, int level) {
        UnitData data = null;

        switch (type) {
            // --- MAIN QUEST ENEMIES ---
            case ASSASSIN: // Level 1 & 15
                data = new UnitData("Assassin",
                    60, 10,   90, 18,   30, 8,   8, 1.8f,   16, 1.2f,
                    15, 1,    180, 2,   92, 0.6f,
                    100, 0,   100, 0); // Exp/Cry fix (simplifikasi dari 100 & 800)
                break;

            case LANCER: // Level 5
                data = new UnitData("Lancer",
                    50, 8,    160, 35,  28, 5,   18, 3.5f,  10, 0.5f,
                    5, 0,     150, 0,   88, 0.4f,
                    400, 0,   500, 0);
                break;

            case RIDER: // Level 10
                data = new UnitData("Rider",
                    70, 12,   130, 26,  32, 6.5f, 14, 2.5f,  14, 1,
                    8, 0.4f,  160, 0,   90, 0.4f,
                    700, 0,   700, 0);
                break;

            case PRETENDER: // Level 15
                data = new UnitData("Pretender",
                    100, 20,  110, 22,  26, 5.5f, 12, 2,     15, 1,
                    10, 0,    155, 0,   95, 0,
                    800, 0,   1000, 0);
                break;

            // --- FOREST ENEMIES ---
            case ANOMIMUS:
                data = new UnitData("Anomimus",
                    70, 20,   50, 10,   18, 4.5f, 6, 1.5f,   14, 1,
                    5, 0,     150, 0,   85, 0.3f,
                    20, 10,    50, 0.2f); // Exp: 20 + 10*Lvl
                break;

            case BEELING:
                data = new UnitData("Beeling",
                    60, 18,   40, 8,    15, 4,    5, 1.2f,   16, 1.2f,
                    5, 0,     150, 0,   85, 0.3f,
                    10, 2.5f, 50, 0.2f);
                break;

            case FARHAT: // Boss Forest
                data = new UnitData("Farhat",
                    50, 15,   60, 20,   30, 7,    15, 4,     12, 0.6f,
                    5, 0,     150, 0,   90, 0,
                    150, 15,  300, 10);
                break;

            // --- RUINS ENEMIES ---
            case SLIME:
                data = new UnitData("Slime",
                    10, 30,   30, 10,   10, 3,    3, 2.5f,   6, 0.3f,
                    5, 0,     150, 0,   85, 0.3f,
                    9, 2,     40, 0.2f);
                break;

            case GOLEM:
                data = new UnitData("Golem",
                    70, 10,   50, 12,   20, 6,    20, 5,     5, 0.2f,
                    5, 0,     150, 0,   85, 0.3f,
                    20, 4,    70, 0.2f);
                break;

            case MANDA: // Boss Ruins
                data = new UnitData("Manda",
                    70, 17,   30, 10,   35, 6,    15, 6,     10, 0.5f,
                    5, 0,     150, 0,   90, 0,
                    220, 20,  300, 10);
                break;
        }

        if (data == null) return new UnitStats("Unknown", 100, 10, 10, 10, 10); // Fallback

        return buildStats(data, level);
    }

    private static UnitStats buildStats(UnitData d, int level) {
        // Hitung stats berdasarkan level
        int hp = d.hp.calculate(level);
        int mp = d.mp.calculate(level);
        int atk = d.atk.calculate(level);
        int def = d.def.calculate(level);
        int spd = d.spd.calculate(level);

        UnitStats stats = new UnitStats(d.name, hp, mp, atk, def, spd);

        // Set Probabilitas (Float)
        stats.setCritChance(d.cRate.calculateFloat(level));
        stats.setCritDamage(d.cDmg.calculateFloat(level));
        stats.setAccuracy(d.acc.calculateFloat(level));

        // Set Level & Reward
        stats.setLevel(level);
        stats.setExpReward(d.expReward.calculate(level));
        stats.setCrystalReward(d.crystalReward.calculate(level));

        // Pastikan HP/MP penuh saat spawn
        stats.setCurrentHp(hp);
        stats.setCurrentMp(mp);

        return stats;
    }
}
