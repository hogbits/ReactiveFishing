package com.hogbits.reactivefishing;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigManager {
    private static final Material DEFAULT_FISH_MATERIAL = Material.TROPICAL_FISH;
    private static final int DEFAULT_TIME_LIMIT = 5;
    private static final int DEFAULT_DIFFICULTY = 10;
    private static final String DEFAULT_GUI_TITLE = "Catch the Fish!";
    private static final int DEFAULT_TRIGGER_PERCENT = 100;

    private final ReactiveFishingPlugin plugin;

    private Material fishMaterial = DEFAULT_FISH_MATERIAL;
    private int timeLimitSeconds = DEFAULT_TIME_LIMIT;
    private int difficultyTicks = DEFAULT_DIFFICULTY;
    private String guiTitle = DEFAULT_GUI_TITLE;
    private int triggerPercent = DEFAULT_TRIGGER_PERCENT;

    public ConfigManager(ReactiveFishingPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        fishMaterial = parseMaterial(config.getString("fish"), DEFAULT_FISH_MATERIAL);
        timeLimitSeconds = clamp(config.getInt("time_limit", DEFAULT_TIME_LIMIT), 1, 60);
        difficultyTicks = clamp(config.getInt("difficulty", DEFAULT_DIFFICULTY), 1, 40);
        guiTitle = config.getString("gui_title", DEFAULT_GUI_TITLE);
        triggerPercent = clamp(config.getInt("trigger_percent", DEFAULT_TRIGGER_PERCENT), 1, 100);
    }

    public Material getFishMaterial() {
        return fishMaterial;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getDifficultyTicks() {
        return difficultyTicks;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public int getTriggerPercent() {
        return triggerPercent;
    }

    private Material parseMaterial(String raw, Material fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        Material material = Material.matchMaterial(raw.trim().toUpperCase());
        if (material == null || !material.isItem()) {
            plugin.getLogger().warning("Invalid config material '" + raw + "', using " + fallback + ".");
            return fallback;
        }
        return material;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
