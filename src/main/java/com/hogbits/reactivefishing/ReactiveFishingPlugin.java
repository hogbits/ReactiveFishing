package com.hogbits.reactivefishing;

import com.hogbits.reactivefishing.integration.emf.EMFProcessor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReactiveFishingPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private MinigameManager minigameManager;
    private boolean emfEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.reload();

        minigameManager = new MinigameManager(this, configManager);

        getServer().getPluginManager().registerEvents(new FishingListener(minigameManager), this);

        emfEnabled = EMFProcessor.isEvenMoreFishPresentAndEnabled();
        if (emfEnabled) {
            getServer().getPluginManager().registerEvents(new EMFProcessor(this), this);
            getLogger().info("EvenMoreFish detected. EMF integration enabled.");
        } else {
            getLogger().info("EvenMoreFish not found. Running without EMF integration.");
        }

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

    public boolean isEmfEnabled() {
        return emfEnabled;
    }
}
