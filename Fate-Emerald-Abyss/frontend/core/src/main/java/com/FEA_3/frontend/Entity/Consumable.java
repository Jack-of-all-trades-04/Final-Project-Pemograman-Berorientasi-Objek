package com.FEA_3.frontend.Entity;

public class Consumable {
    private String name;
    private ItemType type;
    private int value; // Besar heal/mana
    private int quantity;

    public enum ItemType {
        POTION_HP,
        POTION_MP,
        BUFF_DEF,
        BUFF_ATK,
        BUFF_ACC,
        BUFF_CRIT
    }

    public Consumable(String name, ItemType type, int value, int qty) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.quantity = qty;
    }

    // Method ini WAJIB ADA karena dipanggil oleh UnitStats.addItem()
    public void addQuantity(int amount) {
        this.quantity += amount;
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
                target.getStats().setCurrentMp(newMp);
                System.out.println("Recovered " + value + " MP");
                break;

            case BUFF_DEF:
                int currentDef = target.getStats().getDefense();
                // Hitung kenaikan: Defense Saat Ini * (Value / 100)
                // Contoh: Def 20, Value 30 (30%) -> Kenaikan = 20 * 0.3 = 6
                int increaseAmount = (int) (currentDef * (value / 100.0f));
                // Pastikan minimal naik 1 jika persentase terlalu kecil
                if (increaseAmount < 1) increaseAmount = 1;
                int newDef = currentDef + increaseAmount;
                target.getStats().setDefense(newDef);
                System.out.println("Defense increased by " + increaseAmount + " (" + value + "%)");
                System.out.println("Buff applied! Total DEF: " + newDef);
                break;

            case BUFF_ATK:
                // Value = Persentase (misal 30)
                target.setAttackBuff(3); // Durasi 3 Turn
                System.out.println("Attack Buff applied for 3 turns!");
                break;

            case BUFF_ACC:
                target.setAccuracyBuff(3); // Durasi 3 Turn
                System.out.println("Accuracy Buff applied for 3 turns!");
                break;

            case BUFF_CRIT:
                target.setCritBuff(2); // Durasi 2 Turn
                System.out.println("Crit Buff applied for 2 turns!");
                break;
        }
        quantity--;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() { return name + " (x" + quantity + ")"; }
}
