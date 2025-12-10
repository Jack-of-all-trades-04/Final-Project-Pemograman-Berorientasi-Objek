package com.FEA_3.frontend.Entity;

import com.FEA_3.frontend.Core.UnitState;
import com.FEA_3.frontend.Patterns.Command.Command;
import com.FEA_3.frontend.Patterns.Observer.UnitObserver;
import com.FEA_3.frontend.Patterns.Strategy.BattleStrategy;

import java.util.ArrayList;
import java.util.List;

public class GameUnit {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private BattleStrategy strategy;
    private UnitState state = UnitState.IDLE;
    private float stateTimer = 0f;
    private UnitStats stats;

    public GameUnit(UnitStats stats){
        this.stats = stats;
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

    // List pendengar (Observer)
    private List<UnitObserver> observers = new ArrayList<>();

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

    public void takeDamage(int dmg) {
        int newHp = stats.getCurrentHp() - dmg;
        if (newHp < 0) newHp = 0;

        stats.setCurrentHp(newHp); // Update stat

        System.out.println(stats.getName() + " terkena " + dmg + " damage!");

        // BERITAHU SEMUA OBSERVER BAHWA HP BERUBAH
        notifyObservers();
    }

    private void notifyObservers() {
        for (UnitObserver observer : observers) {
            observer.onHealthChanged(hp, maxHp);
        }
    }

    public boolean isDead() { return hp <= 0; }
    public int getAttackPower() { return stats.getAttackPower(); }
    public String getName() { return stats.getName(); }
    public int getHp() { return stats.getCurrentHp(); }
    public int getMaxHp() { return stats.getMaxHp(); }
}
