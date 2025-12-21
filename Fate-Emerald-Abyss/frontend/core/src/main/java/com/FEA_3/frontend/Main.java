package com.FEA_3.frontend;

import com.FEA_3.frontend.Core.MainMenuScreen;
import com.FEA_3.frontend.Core.Settings;
import com.FEA_3.frontend.Entity.UnitStats;
import com.FEA_3.frontend.Utils.UnitDatabase;
import com.badlogic.gdx.Game;

public class Main extends Game {
    public UnitStats playerStats;
    public Settings settings;

    @Override
    public void create() {
        playerStats = UnitDatabase.createPlayerStats(1);
        settings = com.FEA_3.frontend.Core.SettingsManager.load();
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
