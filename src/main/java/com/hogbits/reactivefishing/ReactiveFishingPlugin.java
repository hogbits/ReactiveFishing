package com.hogbits.reactivefishing;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReactiveFishingPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private MinigameManager minigameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.reload();

        minigameManager = new MinigameManager(this, configManager);

        getServer().getPluginManager().registerEvents(new FishingListener(minigameManager), this);

        PluginCommand command = getCommand("reactivefishing");
        if (command != null) {
            CommandHandler commandHandler = new CommandHandler(configManager);
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
        } else {
            getLogger().warning("Command 'reactivefishing' is not defined in plugin.yml.");
        }
    }

    @Override
    public void onDisable() {
        if (minigameManager != null) {
            minigameManager.shutdown();
        }
    }
}
