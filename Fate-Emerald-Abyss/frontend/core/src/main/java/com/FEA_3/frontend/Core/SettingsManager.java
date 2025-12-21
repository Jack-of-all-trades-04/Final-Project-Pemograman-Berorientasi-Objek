package com.FEA_3.frontend.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private static final String PREF_NAME = "FEA_Settings";
    private static final String KEY_BGM_ENABLED = "bgmEnabled";
    private static final String KEY_SFX_ENABLED = "sfxEnabled";
    private static final String KEY_BGM_VOL = "bgmVolume";
    private static final String KEY_SFX_VOL = "sfxVolume";

    public static void save(Settings s) {
        if (s == null) return;
        Preferences p = Gdx.app.getPreferences(PREF_NAME);
        p.putBoolean(KEY_BGM_ENABLED, s.isBgmEnabled());
        p.putBoolean(KEY_SFX_ENABLED, s.isSfxEnabled());
        p.putFloat(KEY_BGM_VOL, s.getBgmVolume());
        p.putFloat(KEY_SFX_VOL, s.getSfxVolume());
        p.flush();
    }

    public static Settings load() {
        Preferences p = Gdx.app.getPreferences(PREF_NAME);
        Settings s = new Settings();
        s.setBgmEnabled(p.getBoolean(KEY_BGM_ENABLED, true));
        s.setSfxEnabled(p.getBoolean(KEY_SFX_ENABLED, true));
        s.setBgmVolume(p.getFloat(KEY_BGM_VOL, 0.5f));
        s.setSfxVolume(p.getFloat(KEY_SFX_VOL, 1.0f));
        return s;
    }
}
