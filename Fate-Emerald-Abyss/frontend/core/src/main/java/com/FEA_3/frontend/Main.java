package com.FEA_3.frontend;

import com.FEA_3.frontend.Core.MainMenuScreen;
import com.FEA_3.frontend.Entity.UnitStats;
import com.badlogic.gdx.Game;

public class Main extends Game {
    public UnitStats playerStats;

    @Override
    public void create() {
        playerStats = new UnitStats("Artoria", 1000, 200, 50, 20, 10);
        // Mulai dari Main Menu
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // PENTING: Panggil super.render() agar BattleScreen.render() dijalankan
        super.render();
    }

    @Override
    public void dispose() {
        // Panggil dispose pada screen yang sedang aktif
        if (screen != null) screen.dispose();
    }
}
