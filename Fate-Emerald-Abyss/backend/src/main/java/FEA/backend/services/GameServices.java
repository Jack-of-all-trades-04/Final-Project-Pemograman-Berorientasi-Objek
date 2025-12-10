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
                    // Logic pembuatan karakter awal dipindah ke sini
                    // Nanti bisa ditambah logic: Cek apakah user sudah daftar akun, dll.
                    PlayerData newData = new PlayerData(userId, "Artoria", 1000, 50);
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
}