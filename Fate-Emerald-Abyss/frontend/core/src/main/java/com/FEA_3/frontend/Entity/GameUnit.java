package com.FEA_3.frontend.Entity;

import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.FEA_3.frontend.Patterns.Strategy.BattleStrategy;
import com.FEA_3.frontend.Utils.ResourceManager;

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

    // --- STATUS EFFECTS (FLAGS & COUNTERS) ---
    private int burnTurns = 0;
    private int bleedTurns = 0;
    private boolean isStunned = false;
    private int defenseBuffTurns = 0; // Buckle Up
    private int speedBuffTurns = 0;   // Dodge
    private int accuracyDebuffTurns = 0; // Divine Light
    private int slowDebuffTurns = 0;     // Blizzard
    private boolean nextHitGuaranteed = false; // Visual Calculus
    private boolean hasEndure = false; // Passive Endure
    private int attackBuffTurns = 0;
    private int accuracyBuffTurns = 0;
    private int critBuffTurns = 0;

    // --- LOGIC UPDATE TURN (Panggil ini setiap awal giliran) ---
    public void applyTurnEffects() {
        // 1. Handle DOT (Damage Over Time)
        if (burnTurns > 0) {
            int dmg = (int) (stats.getMaxHp() * 0.05); // 5% HP per turn
            takeDamage(dmg, false);
            System.out.println(stats.getName() + " terkena Burn damage: " + dmg);
            burnTurns--;
        }

        if (bleedTurns > 0) {
            int dmg = 10; // Flat damage atau %
            takeDamage(dmg, false);
            System.out.println(stats.getName() + " terkena Bleed damage: " + dmg);
            bleedTurns--;
        }

        // 2. Kurangi Durasi Buff/Debuff
        if (defenseBuffTurns > 0) defenseBuffTurns--;
        if (speedBuffTurns > 0) speedBuffTurns--;
        if (accuracyDebuffTurns > 0) accuracyDebuffTurns--;
        if (slowDebuffTurns > 0) slowDebuffTurns--;
        if (attackBuffTurns > 0) attackBuffTurns--;
        if (accuracyBuffTurns > 0) accuracyBuffTurns--;
        if (critBuffTurns > 0) critBuffTurns--;

        // Reset Stun di awal turn (setelah efek skip turn diproses di BattleScreen)
        // isStunned = false; // Logic reset stun sebaiknya diatur di BattleScreen setelah skip
    }

    // --- UPDATE ATTACK LOGIC (Visual Calculus & Accuracy Debuff) ---
    public void attackTarget(GameUnit target) {
        float hitChance = stats.getAccuracy();

        // Cek Debuff Accuracy (Divine Light)
        if (this.accuracyDebuffTurns > 0) {
            hitChance -= 25.0f;
        }

        if (this.accuracyBuffTurns > 0) hitChance += 30.0f;

        // Cek Visual Calculus (Guaranteed Hit)
        if (this.nextHitGuaranteed) {
            hitChance = 1000.0f; // Pasti kena
            this.nextHitGuaranteed = false; // Reset setelah dipakai
            System.out.println("Visual Calculus activated!");
        }

        // Cek Target Dodge
        if (target.speedBuffTurns > 0) {
            // Logic dodge sederhana: kurangi hit chance drastis
            hitChance -= 25.0f;
        }

        // Roll RNG (0-100)
        if (rng.nextFloat() * 100 > hitChance) {
            System.out.println("MISS! Serangan meleset.");
            // Tampilkan text "MISS" di UI nanti
            return;
        }

        // 2. HITUNG DAMAGE DASAR (ATK vs DEF)
        // Rumus RPG Klasik: Damage = Atk * (100 / (100 + Def))
        int rawAtk = stats.getAttackPower();

        // --- TAMBAHAN LOGIC ATTACK POTION (+30%) ---
        if (this.attackBuffTurns > 0) {
            rawAtk += (int)(rawAtk * 0.30f);
        }

        float damageMitigation = 100.0f / (100.0f + target.getStats().getDefense());
        int finalDamage = (int) (rawAtk * damageMitigation);

        // 3. CEK CRITICAL
        float critRate = stats.getCritChance();
        float critDmgMult = stats.getCritDamage();

        // --- TAMBAHAN LOGIC CRIT POTION (+20% Chance, +20% Damage) ---
        if (this.critBuffTurns > 0) {
            critRate += 20.0f;       // Tambah 20% Chance
            critDmgMult += 20.0f;    // Tambah 20% Damage (misal 150% jadi 170%)
        }

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

    // 1. Method untuk mengganti State & Reset Waktu
    public void setState(UnitState newState) {
        this.state = newState;
        this.stateTimer = 0f; // Reset waktu jadi 0 setiap ganti animasi
    }

    // 2. Method Update Waktu (JANTUNG UTAMA ANIMASI)
    public void update(float delta) {
        stateTimer += delta; // Tambah waktu sesuai frame rate

        // Logika Reset Otomatis:
        // Jika sudah animasi Attack/Hurt selama 0.5 detik, kembalikan ke IDLE
        if (state == UnitState.ATTACK && stateTimer > 0.5f) {
            setState(UnitState.IDLE);
        }
        else if (state == UnitState.HURT && stateTimer > 0.5f) {
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

    public String act(GameUnit target) {
        if (strategy != null) {
            Command command = strategy.decideAction(this, target);
            command.execute();
            return command.getDescription(); // Kembalikan teks deskripsi
        }
        return "";
    }

    public void addObserver(UnitObserver observer) {
        observers.add(observer);
    }

    // Update takeDamage untuk handle UI Critical
    // --- UPDATE TAKE DAMAGE (Endure & Buckle Up) ---
    public void takeDamage(int dmg, boolean isCrit) {
        // 1. Cek Buckle Up (Defense Buff)
        if (defenseBuffTurns > 0) {
            dmg = (int) (dmg * 0.55); // Kurangi 45% (sisa 55%)
            System.out.println("Buckle Up reduced damage!");
        }

        // 2. Logic Damage standar
        if (isDefending) { dmg /= 2; isDefending = false; }
        int current = stats.getCurrentHp();
        int newHp = current - dmg;

        // 3. Cek PASSIVE ENDURE
        if (newHp <= 0 && hasEndure) {
            newHp = 1; // Bertahan hidup dengan 1 HP
            hasEndure = false; // Hanya sekali per battle (atau reset logic lain)
            ResourceManager.getInstance().getSound("Sound_Effects/Endure(also maybe).mp3").play();
            System.out.println(stats.getName() + " ENDURED the attack!");
        } else if (newHp < 0) {
            newHp = 0;
        }

        stats.setCurrentHp(newHp);
        notifyDamageObservers(dmg, isCrit); // Method baru
    }

    private void notifyDamageObservers(int dmg, boolean isCrit) {
        for (UnitObserver obs : observers) {
            obs.onDamageTaken(dmg, isCrit);
        }
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
    public List<Skill> getAllSkills() {
        return skills;
    }
    public void setBurn(int turns) { this.burnTurns = turns; }
    public void setBleed(int turns) { this.bleedTurns = turns; }
    public void setStunned(boolean stun) { this.isStunned = stun; }
    public boolean isStunned() { return isStunned; }
    public void setDefenseBuff(int turns) { this.defenseBuffTurns = turns; }
    public void setSpeedBuff(int turns) { this.speedBuffTurns = turns; }
    public void setAccuracyDebuff(int turns) { this.accuracyDebuffTurns = turns; }
    public void setSlowDebuff(int turns) { this.slowDebuffTurns = turns; }
    public void setNextHitGuaranteed(boolean val) { this.nextHitGuaranteed = val; }
    public void setHasEndure(boolean val) { this.hasEndure = val; }
    public void setAttackBuff(int turns) { this.attackBuffTurns = turns; }
    public void setAccuracyBuff(int turns) { this.accuracyBuffTurns = turns; }
    public void setCritBuff(int turns) { this.critBuffTurns = turns; }
    public int getAttackPower() { return stats.getAttackPower(); }
    public String getName() { return stats.getName(); }
    public int getHp() { return stats.getCurrentHp(); }
    public int getMaxHp() { return stats.getMaxHp(); }
    public UnitStats getStats() {
        return this.stats;
    }

    public boolean isDefending() {
        return isDefending;
    }

    public int getBurnTurns() {
        return burnTurns;
    }

    public int getBleedTurns() {
        return bleedTurns;
    }

    public int getDefenseBuffTurns() {
        return defenseBuffTurns;
    }

    public int getSpeedBuffTurns() {
        return speedBuffTurns;
    }

    public int getAccuracyDebuffTurns() {
        return accuracyDebuffTurns;
    }

    public int getSlowDebuffTurns() {
        return slowDebuffTurns;
    }

    public boolean isNextHitGuaranteed() {
        return nextHitGuaranteed;
    }

    public boolean isHasEndure() {
        return hasEndure;
    }

    public int getAttackBuffTurns() { return attackBuffTurns; }
    public int getAccuracyBuffTurns() { return accuracyBuffTurns; }
    public int getCritBuffTurns() { return critBuffTurns; }
}
