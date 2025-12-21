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
        // DATA SABER (BUFFED VERSION)
        // OLD HP: 80+18*L  -> NEW HP: 130 + 25*L (Darah jauh lebih tebal)
        // OLD DEF: 10+2.5*L -> NEW DEF: 18 + 3.5*L (Defense awal tinggi untuk nahan Assassin)
        // OLD ATK: 25+6*L   -> NEW ATK: 32 + 7*L (Supaya battle tidak kelamaan)
        // OLD MP: 120+28*L  -> NEW MP: 140 + 30*L (Bisa spam skill)

        UnitData saber = new UnitData("Artoria",
            130, 25,    // HP: Base 130, Growth 25
            140, 30,    // MP: Base 140, Growth 30
            32, 7,      // ATK: Base 32, Growth 7
            18, 3.5f,   // DEF: Base 18, Growth 3.5
            14, 1.0f,   // SPD: Base 14, Growth 1.0 (Biar bisa ngebalap turn musuh)
            5, 0.5f,    // Crit Rate
            150, 1,     // Crit Dmg
            95, 0.5f,   // Accuracy (Agak dinaikkan biar jarang Miss di awal)
            0, 0,       // Exp Reward (Player gak drop exp)
            0, 0);      // Crystal Reward

        return buildStats(saber, level);
    }

    public static UnitStats createEnemyStats(EnemyType type, int level) {
        UnitData data = null;

        switch (type) {
            // ============================================================
            // MAIN QUEST BOSSES (BUFFED!!)
            // ============================================================

            case ASSASSIN: // Level 1 & 15 (Speed & Crit Threat)
                // OLD HP: 60+10*L -> NEW HP: 120 + 25*L (Biar gak 3x pukul mati)
                // OLD ATK: 30+8*L -> NEW ATK: 35 + 9*L (Sakit di awal)
                // OLD SPD: 16 -> NEW SPD: 18 (Biar lebih sering jalan duluan/dodge)
                data = new UnitData("Assassin",
                    120, 25,  90, 18,   35, 9,    5, 1.5f,   18, 1.5f,
                    20, 1.5f, 180, 2,   95, 0.5f,
                    150, 0,   200, 0); // Reward digedein dikit biar worth it
                break;

            case LANCER: // Level 5 (The Wall / Tank Check)
                // OLD HP: 50 -> NEW HP: 250 + 40*L (Darah Tebal!)
                // OLD DEF: 18 -> NEW DEF: 20 + 5*L (Keras!)
                // Player butuh level tinggi atau skill "Through/Pierce" atau Magic
                data = new UnitData("Lancer",
                    250, 40,  160, 35,  32, 6,   20, 5.0f,  10, 0.5f,
                    5, 0,     150, 0,   88, 0.4f,
                    500, 0,   600, 0);
                break;

            case RIDER: // Level 10 (High Damage Check)
                // OLD HP: 70 -> NEW HP: 350 + 35*L
                // OLD ATK: 32 -> NEW ATK: 45 + 8*L (Harus sakit biar player panik heal)
                data = new UnitData("Rider",
                    350, 35,  130, 26,  45, 8.0f, 15, 3.0f,  16, 1.2f,
                    12, 0.5f, 160, 0,   92, 0.4f,
                    800, 0,   800, 0);
                break;

            case PRETENDER: // Level 15 (Final Boss)
                // OLD HP: 100 -> NEW HP: 800 + 50*L (Harus terasa seperti Boss Akhir)
                // Stats lain di-buff rata agar sulit
                data = new UnitData("Pretender",
                    800, 50,  200, 30,  50, 8.5f, 20, 4,     18, 1,
                    15, 0.5f, 170, 0,   100, 0,
                    2000, 0,  2000, 0);
                break;

            // ============================================================
            // GRINDING ENEMIES (BALANCED FOR GRINDING)
            // ============================================================
            // Dibuat sedikit lebih keras supaya player butuh heal sesekali

            case ANOMIMUS: // Forest (Standard Mob)
                // HP Buff dikit biar ga 1 hit kill
                data = new UnitData("Anomimus",
                    80, 22,   50, 10,   20, 5.0f, 8, 2.0f,   14, 1,
                    5, 0,     150, 0,   85, 0.3f,
                    25, 12,   40, 5f); // Reward Exp/Cry dinaikkan agar grinding terasa progressnya
                break;

            case BEELING: // Forest (Low HP, High Spd/Atk)
                data = new UnitData("Beeling",
                    60, 18,   40, 8,    22, 5.5f, 5, 1.2f,   18, 1.5f,
                    10, 0.5f, 150, 0,   90, 0.3f,
                    20, 10,   35, 5f);
                break;

            case FARHAT: // Boss Forest (Mid-Boss)
                // Buff HP biar berasa lawan Boss Area
                data = new UnitData("Farhat",
                    200, 30,  100, 20,  35, 7.5f, 15, 4,     14, 0.8f,
                    8, 0,     150, 0,   92, 0,
                    250, 20,  400, 20);
                break;

            // --- RUINS ENEMIES (Higher Difficulty Area) ---

            case SLIME: // Ruins (Tanky Mob)
                // Punya skill Heal, jadi HP jgn terlalu tebal, tapi DEF lumayan
                data = new UnitData("Slime",
                    120, 35,  50, 15,   25, 5,    15, 3.5f,  8, 0.5f,
                    5, 0,     150, 0,   85, 0.3f,
                    35, 15,   50, 5f);
                break;

            case GOLEM: // Ruins (Hard Hitter)
                data = new UnitData("Golem",
                    180, 40,  60, 12,   40, 8,    25, 6,     6, 0.2f,
                    5, 0,     150, 0,   85, 0.3f,
                    45, 18,   80, 5f);
                break;

            case MANDA: // Boss Ruins
                data = new UnitData("Manda",
                    500, 45,  100, 20,  55, 9,    25, 6,     12, 0.5f,
                    10, 0,    160, 0,   95, 0,
                    600, 30,  800, 30);
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
