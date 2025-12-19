package com.FEA_3.frontend.Entity;

public class Consumable {
    private String name;
    private ItemType type;
    private int value; // Besar heal/mana
    private int quantity;

    public enum ItemType {
        POTION_HP,
        POTION_MP,
        ELIXIR_BUFF
    }

    public Consumable(String name, ItemType type, int value, int qty) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.quantity = qty;
    }

    public void use(GameUnit target) {
        if (quantity <= 0) return;

        switch (type) {
            case POTION_HP:
                int newHp = target.getStats().getCurrentHp() + value;
                if (newHp > target.getStats().getMaxHp()) newHp = target.getStats().getMaxHp();
                target.getStats().setCurrentHp(newHp);
                System.out.println("Healed " + value + " HP");
                break;

            case POTION_MP:
                int newMp = target.getStats().getCurrentMp() + value;
                if (newMp > target.getStats().getMaxMp()) newMp = target.getStats().getMaxMp();
                // Asumsi ada setter MP
                // target.getStats().setCurrentMp(newMp);
                break;

            case ELIXIR_BUFF:
                // Logic buff turn (kompleks, simpan untuk update berikutnya)
                System.out.println("Buff applied!");
                break;
        }
        quantity--;
    }

    public String getName() { return name + " (x" + quantity + ")"; }
}
