package FEA.backend.controller;

import FEA.backend.model.PlayerData;
import FEA.backend.services.GameServices; // Import Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameServices gameService; // Ganti Repository jadi Service

    @GetMapping("/load/{userId}")
    public PlayerData loadGame(@PathVariable String userId) {
        // Controller cuma terima request -> oper ke Service
        return gameService.loadOrCreatePlayer(userId);
    }

    @PostMapping("/save")
    public PlayerData saveGame(@RequestBody PlayerData data) {
        return gameService.savePlayerProgress(data);
    }
}