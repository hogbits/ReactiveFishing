package com.hogbits.reactivefishing;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class MinigameManager {
    private final ReactiveFishingPlugin plugin;
    private final ConfigManager configManager;
    private final GUIBuilder guiBuilder;
    private final Map<UUID, PendingCatch> pendingCatches = new ConcurrentHashMap<>();
    private final Map<UUID, MinigameSession> sessions = new ConcurrentHashMap<>();

    public MinigameManager(ReactiveFishingPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.guiBuilder = new GUIBuilder(configManager);
    }

    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() == PlayerFishEvent.State.BITE) {
            if (!shouldTrigger()) {
                return;
            }

            cancelAndCleanup(player.getUniqueId(), false, false);

            pendingCatches.put(player.getUniqueId(), new PendingCatch(event.getHook(), null));
            sendConfiguredMessage(player, ChatColor.AQUA + "A fish is nibbling! Right-click with your rod now!");
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            PendingCatch pending = pendingCatches.get(player.getUniqueId());
            MinigameSession session = sessions.get(player.getUniqueId());
            if (pending == null && session == null) {
                return;
            }

            if (event.getCaught() instanceof Item itemEntity) {
                ItemStack caughtLoot = itemEntity.getItemStack().clone();
                if (pending != null) {
                    pending.loot = caughtLoot;
                }
                if (session != null) {
                    session.setPendingLoot(caughtLoot);
                }
                itemEntity.remove();
            }

            event.setCancelled(true);
        }
    }

    public void tryStartMinigame(Player player) {
        UUID playerId = player.getUniqueId();
        PendingCatch pending = pendingCatches.remove(playerId);
        if (pending == null) {
            return;
        }

        cancelAndCleanup(playerId, false, false);

        int catchStart = ThreadLocalRandom.current().nextInt(9, 16);
        MinigameSession session = new MinigameSession(playerId, pending.hook, pending.loot, catchStart);
        sessions.put(playerId, session);

        Inventory inventory = guiBuilder.create(session, configManager.getGuiTitle());
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.1f);

        session.setMovementTask(plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (session.isFinished() || !player.isOnline()) {
                        return;
                    }
                    session.advanceFish();
                    guiBuilder.render(session);
                },
                configManager.getDifficultyTicks(),
                configManager.getDifficultyTicks()
        ));

        session.setTimeoutTask(plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> finishSession(player, false, "Too slow! The fish got away."),
                configManager.getTimeLimitSeconds() * 20L
        ));
    }

    public boolean isMinigameInventory(Player player, Inventory inventory) {
        MinigameSession session = sessions.get(player.getUniqueId());
        return session != null && session.isInventory(inventory);
    }

    public void handleInventoryClick(Player player) {
        MinigameSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        boolean success = session.isFishInCatchZone();
        String message = success
                ? ChatColor.GREEN + "Perfect catch!"
                : ChatColor.RED + "Missed timing. Catch failed.";
        finishSession(player, success, message);
    }

    public void handleInventoryClose(Player player) {
        MinigameSession session = sessions.get(player.getUniqueId());
        if (session == null || session.isFinished()) {
            return;
        }
        finishSession(player, false, ChatColor.RED + "You closed the minigame. Catch failed.");
    }

    public void handleQuit(Player player) {
        cancelAndCleanup(player.getUniqueId(), true, true);
    }

    public void shutdown() {
        for (MinigameSession session : sessions.values()) {
            session.markFinished();
            session.cancelTasks();
        }
        sessions.clear();
        pendingCatches.clear();
    }

    private void finishSession(Player player, boolean success, String message) {
        MinigameSession session = sessions.get(player.getUniqueId());
        if (session == null || session.isFinished()) {
            return;
        }

        session.markFinished();
        session.cancelTasks();
        sessions.remove(player.getUniqueId());
        retractHook(session);

        if (success) {
            ItemStack reward = session.getPendingLoot() != null
                    ? session.getPendingLoot().clone()
                    : new ItemStack(configManager.getFishIndicatorMaterial());
            player.getInventory().addItem(reward);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
        }

        sendConfiguredMessage(player, message);
        if (player.getOpenInventory().getTopInventory().equals(session.getInventory())) {
            player.closeInventory();
        }
    }

    private void cancelAndCleanup(UUID playerId, boolean removePending, boolean silent) {
        MinigameSession existingSession = sessions.remove(playerId);
        if (existingSession != null) {
            existingSession.markFinished();
            existingSession.cancelTasks();
            retractHook(existingSession);
        }
        if (removePending) {
            pendingCatches.remove(playerId);
        }
        if (!silent && existingSession != null) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
    }

    private boolean shouldTrigger() {
        int roll = ThreadLocalRandom.current().nextInt(1, 101);
        return roll <= configManager.getTriggerPercent();
    }

    private void sendConfiguredMessage(Player player, String message) {
        if (configManager.isShowChat()) {
            player.sendMessage(message);
        }
    }

    private void retractHook(MinigameSession session) {
        FishHook hook = session.getHook();
        if (hook != null && hook.isValid()) {
            hook.remove();
        }
    }

    private static final class PendingCatch {
        private final FishHook hook;
        private ItemStack loot;

        private PendingCatch(FishHook hook, ItemStack loot) {
            this.hook = hook;
            this.loot = loot;
        }
    }
}
