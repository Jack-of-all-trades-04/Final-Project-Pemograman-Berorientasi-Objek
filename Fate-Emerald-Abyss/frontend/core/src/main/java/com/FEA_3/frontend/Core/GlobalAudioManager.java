package com.FEA_3.frontend.Core;

import com.FEA_3.frontend.Utils.ResourceManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class GlobalAudioManager {
    private static GlobalAudioManager instance;
    private Music currentBGM;
    private String currentBgmPath = null;
    private final Map<String, Music> musicCache = new HashMap<>();
    private final Map<String, Sound> soundCache = new HashMap<>();

    private boolean bgmEnabled = true;
    private boolean sfxEnabled = true;
    private float bgmVolume = 0.5f;
    private float sfxVolume = 1.0f;

    private GlobalAudioManager() {
        // Load persisted settings
        Settings s = SettingsManager.load();
        applySettings(s);
    }

    public static GlobalAudioManager getInstance() {
        if (instance == null) instance = new GlobalAudioManager();
        return instance;
    }

    public void applySettings(Settings s) {
        if (s == null) return;
        this.bgmEnabled = s.isBgmEnabled();
        this.sfxEnabled = s.isSfxEnabled();
        this.bgmVolume = clamp(s.getBgmVolume(), 0f, 1f);
        this.sfxVolume = clamp(s.getSfxVolume(), 0f, 1f);
        if (currentBGM != null) {
            currentBGM.setVolume(bgmEnabled ? bgmVolume : 0f);
        }
    }

    public void playBGM(String path, boolean loop) {
        if (path == null) return;
        if (currentBgmPath != null && currentBgmPath.equals(path) && currentBGM != null) {
            if (!currentBGM.isPlaying() && bgmEnabled) currentBGM.play();
            currentBGM.setLooping(loop);
            currentBGM.setVolume(bgmEnabled ? bgmVolume : 0f);
            return;
        }
        if (currentBGM != null) {
            currentBGM.stop();
        }
        Music m = musicCache.get(path);
        if (m == null) {
            m = ResourceManager.getInstance().getMusic(path);
            if (m != null) musicCache.put(path, m);
        }
        currentBGM = m;
        currentBgmPath = path;
        if (currentBGM != null) {
            currentBGM.setLooping(loop);
            currentBGM.setVolume(bgmEnabled ? bgmVolume : 0f);
            if (bgmEnabled) currentBGM.play();
        }
    }

    public void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBgmPath = null;
            currentBGM = null;
        }
    }

    public void playSfx(String path) {
        if (!sfxEnabled || path == null) return;
        Sound s = soundCache.get(path);
        if (s == null) {
            s = ResourceManager.getInstance().getSound(path);
            if (s != null) soundCache.put(path, s);
        }
        if (s != null) s.play(sfxVolume);
    }

    public void setBgmVolume(float v) {
        bgmVolume = clamp(v, 0f, 1f);
        if (currentBGM != null) currentBGM.setVolume(bgmEnabled ? bgmVolume : 0f);
    }

    public void setSfxVolume(float v) { sfxVolume = clamp(v, 0f, 1f); }

    public void setBgmEnabled(boolean enabled) {
        bgmEnabled = enabled;
        if (currentBGM != null) {
            if (bgmEnabled) currentBGM.play();
            currentBGM.setVolume(bgmEnabled ? bgmVolume : 0f);
            if (!bgmEnabled) currentBGM.pause();
        }
    }

    public void setSfxEnabled(boolean enabled) { sfxEnabled = enabled; }

    public void dispose() {
        if (currentBGM != null) {
            currentBGM.stop();
            currentBGM = null;
        }
        for (Music m : musicCache.values()) {
            try { m.dispose(); } catch (Exception ignored) {}
        }
        musicCache.clear();
        for (Sound s : soundCache.values()) {
            try { s.dispose(); } catch (Exception ignored) {}
        }
        soundCache.clear();
    }

    private float clamp(float v, float a, float b) {
        return Math.max(a, Math.min(b, v));
    }

    // Getters for UI
    public boolean isBgmEnabled() { return bgmEnabled; }
    public boolean isSfxEnabled() { return sfxEnabled; }
    public float getBgmVolume() { return bgmVolume; }
    public float getSfxVolume() { return sfxVolume; }
}
