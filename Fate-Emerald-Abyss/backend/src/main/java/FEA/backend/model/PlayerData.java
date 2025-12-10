package FEA.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class PlayerData {

    @Id
    private String id; // Username, misal "User1"

    private String characterName; // Misal "Artoria"
    private int maxHp;
    private int attackPower;
    private int level;
    private int exp;

    // Constructor Kosong (Wajib untuk JPA)
    public PlayerData() {}

    // Constructor biasa
    public PlayerData(String id, String characterName, int maxHp, int attackPower) {
        this.id = id;
        this.characterName = characterName;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.level = 1;
        this.exp = 0;
    }

    // Getter & Setter (Generate pakai Alt+Insert di IntelliJ)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String name) { this.characterName = name; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getAttackPower() { return attackPower; }
    public void setAttackPower(int atk) { this.attackPower = atk; }
    // ... dst
}