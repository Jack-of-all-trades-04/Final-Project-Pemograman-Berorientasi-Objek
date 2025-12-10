package FEA.backend.repository;

import FEA.backend.model.PlayerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerData, String> {
    // Kosong saja, JpaRepository sudah menyediakan method .findById(), .save(), dll.
}