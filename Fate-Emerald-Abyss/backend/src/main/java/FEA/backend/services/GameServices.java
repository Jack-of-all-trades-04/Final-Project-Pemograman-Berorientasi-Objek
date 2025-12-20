package FEA.backend.services;

import FEA.backend.model.PlayerData;
import FEA.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServices {

    @Autowired
    private PlayerRepository playerRepository;

    // Logika Bisnis 1: Load data, kalau tidak ada buat karakter baru
    public PlayerData loadOrCreatePlayer(String userId) {
        return playerRepository.findById(userId)
                .orElseGet(() -> {
                    // Logic Buat Baru yang lebih simpel karena Constructor sudah handle default value
                    PlayerData newData = new PlayerData(userId, "Artoria");

                    // Save ke DB
                    return playerRepository.save(newData);
                });
    }

    // Logika Bisnis 2: Simpan data dengan validasi
    public PlayerData savePlayerProgress(PlayerData data) {
        // Di Service inilah tempat yang tepat untuk validasi Anti-Cheat
        // Contoh: Cek apakah level naik drastis? Cek apakah HP negatif?

        if (data.getMaxHp() < 0) {
            data.setMaxHp(1); // Koreksi otomatis jika data rusak
        }

        return playerRepository.save(data);
    }

    public void deletePlayer(String userId) {
        if (playerRepository.existsById(userId)) {
            playerRepository.deleteById(userId);
        }
    }
}