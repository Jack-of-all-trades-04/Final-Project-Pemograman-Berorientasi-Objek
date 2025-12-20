package FEA.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import wajib agar tidak loop saat convert JSON

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    // Ubah nama kolom di DB karena "value" adalah kata terlarang di H2
    @Column(name = "item_value")
    private int value;

    private int quantity;

    // --- RELASI KE PLAYER (PENTING) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id") // Nama kolom foreign key di database
    @JsonIgnore // Wajib ada: Agar saat Player diload, item tidak mencoba load Player lagi (Infinite Loop)
    private PlayerData player;

    // 1. Constructor Kosong (WAJIB untuk JPA)
    public InventoryItem() {}

    // 2. Constructor dengan Parameter (Untuk kemudahan coding)
    public InventoryItem(String name, String type, int value, int quantity) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.quantity = quantity;
    }

    // --- GETTER & SETTER ---

    // Method ini yang dicari oleh error "Cannot resolve method setPlayer"
    public void setPlayer(PlayerData player) {
        this.player = player;
    }

    public PlayerData getPlayer() {
        return player;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}