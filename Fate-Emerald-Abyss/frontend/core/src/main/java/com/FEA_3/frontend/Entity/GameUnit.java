package com.FEA_3.frontend.Entity;

import com.FEA_3.frontend.Core.UnitState;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.FEA_3.frontend.Patterns.Strategy.BattleStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameUnit {
    private String name;
    private UnitStats stats;
    private Random rng = new Random();
    private BattleStrategy strategy;
    private List<UnitObserver> observers = new ArrayList<>();
    private UnitState state = UnitState.IDLE;
    private float stateTimer = 0f;
    private boolean isDefending = false;

    public void attackTarget(GameUnit target) {
        // 1. CEK ACCURACY (Level Difference Penalty)
        float hitChance = stats.getAccuracy();
        int levelDiff = target.getStats().getLevel() - this.stats.getLevel();

        // Jika musuh lebih tinggi levelnya, akurasi turun 5% per level
        if (levelDiff > 0) {
            hitChance -= (levelDiff * 5.0f);
        }

        // Roll RNG (0-100)
        if (rng.nextFloat() * 100 > hitChance) {
            System.out.println("MISS! Serangan meleset.");
            // Tampilkan text "MISS" di UI nanti
            return;
        }

        // 2. HITUNG DAMAGE DASAR (ATK vs DEF)
        // Rumus RPG Klasik: Damage = Atk * (100 / (100 + Def))
        float damageMitigation = 100.0f / (100.0f + target.getStats().getDefense());
        int finalDamage = (int) (stats.getAttackPower() * damageMitigation);

        // 3. CEK CRITICAL
        boolean isCrit = false;
        if (rng.nextFloat() * 100 < stats.getCritChance()) {
            isCrit = true;
            // Crit Damage: 150% -> 1.5x
            finalDamage *= (stats.getCritDamage() / 100.0f);
        }

        // 4. EKSEKUSI DAMAGE KE TARGET
        target.takeDamage(finalDamage, isCrit);
    }

    public void setDefending(boolean defending) {
        this.isDefending = defending;
    }

    public GameUnit(UnitStats stats){
        this.stats = stats;
    }

    // Tambahkan List Skill
    private List<Skill> skills = new ArrayList<>();
    private List<Skill> activeSkills = new ArrayList<>(); // Skill yang sudah unlocked

    public void addSkill(Skill s) {
        skills.add(s);
        checkUnlockSkills();
    }

    // Panggil ini setiap Level Up
    public void checkUnlockSkills() {
        activeSkills.clear();
        int currentLvl = stats.getLevel();

        for (Skill s : skills) {
            if (currentLvl >= s.getUnlockLevel()) {
                activeSkills.add(s);

                // Jika Passive, langsung apply efeknya sekali saja (atau tiap battle start)
                if (s.getType() == Skill.SkillType.PASSIVE) {
                    s.use(this, null);
                }
            }
        }
    }

    public List<Skill> getUnlockedSkills() {
        return activeSkills;
    }

    // Method untuk ganti state
    public void setState(UnitState newState) {
        this.state = newState;
        this.stateTimer = 0f; // Reset timer tiap ganti state
    }

    public void update(float delta) {
        stateTimer += delta;

        // Logika kembali ke IDLE otomatis setelah animasi selesai
        if (state == UnitState.ATTACK && stateTimer > 0.5f) { // Animasi Attack 0.5 detik
            setState(UnitState.IDLE);
        }
        if (state == UnitState.HURT && stateTimer > 0.3f) { // Animasi Hurt 0.3 detik
            setState(UnitState.IDLE);
        }
    }

    // Getter
    public UnitState getState() { return state; }
    public float getStateTimer() { return stateTimer; }

    // Constructor UPDATE (tambah parameter strategy opsional)
    // Atau buat setter saja biar gampang
    public void setStrategy(BattleStrategy strategy) {
        this.strategy = strategy;
    }

    public void act(GameUnit target) {
        if (strategy != null) {
            // Minta strategi membuat keputusan, lalu eksekusi
            Command command = strategy.decideAction(this, target);
            command.execute();
        }
    }

    public void addObserver(UnitObserver observer) {
        observers.add(observer);
    }

    // Update takeDamage untuk handle UI Critical
    public void takeDamage(int dmg, boolean isCrit) {
        if (isDefending) {
            dmg /= 2;
            isDefending = false;
        }

        int current = stats.getCurrentHp();
        int newHp = current - dmg;
        if (newHp < 0) newHp = 0;
        stats.setCurrentHp(newHp);

        // Log
        String critText = isCrit ? " (CRITICAL!)" : "";
        System.out.println(stats.getName() + " terkena " + dmg + " damage" + critText);

        notifyObservers(); // Perlu update observer untuk kirim status crit nanti
    }

    private void notifyObservers() {
        for (UnitObserver observer : observers) {
            observer.onHealthChanged(stats.getCurrentHp(), stats.getMaxHp());
        }
    }

    public boolean isDead() {
        // Cek nyawa dari stats, BUKAN dari variabel lokal
        return stats.getCurrentHp() <= 0;
    }
    public int getAttackPower() { return stats.getAttackPower(); }
    public String getName() { return stats.getName(); }
    public int getHp() { return stats.getCurrentHp(); }
    public int getMaxHp() { return stats.getMaxHp(); }
    public UnitStats getStats() {
        return this.stats;
    }
}
