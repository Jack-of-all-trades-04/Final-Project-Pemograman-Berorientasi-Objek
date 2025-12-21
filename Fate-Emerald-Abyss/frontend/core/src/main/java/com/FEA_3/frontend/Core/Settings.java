package com.FEA_3.frontend.Core;

public class Settings {
    private boolean bgmEnabled = true;
    private boolean sfxEnabled = true;
    private float bgmVolume = 0.5f;
    private float sfxVolume = 1.0f;

    public boolean isBgmEnabled() { return bgmEnabled; }
    public void setBgmEnabled(boolean bgmEnabled) { this.bgmEnabled = bgmEnabled; }

    public boolean isSfxEnabled() { return sfxEnabled; }
    public void setSfxEnabled(boolean sfxEnabled) { this.sfxEnabled = sfxEnabled; }

    public float getBgmVolume() { return bgmVolume; }
    public void setBgmVolume(float bgmVolume) { this.bgmVolume = bgmVolume; }

    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }
}
