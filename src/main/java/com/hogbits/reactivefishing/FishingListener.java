package com.hogbits.reactivefishing;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class FishingListener implements Listener {
    private final MinigameManager minigameManager;

    public FishingListener(MinigameManager minigameManager) {
        this.minigameManager = minigameManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFish(PlayerFishEvent event) {
        minigameManager.onPlayerFish(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.FISHING_ROD) {
                    minigameManager.tryStartMinigame(event.getPlayer());
                }
            }
            default -> {
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!minigameManager.isMinigameInventory(player, event.getView().getTopInventory())) {
            return;
        }

        event.setCancelled(true);
        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
            minigameManager.handleInventoryClick(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!minigameManager.isMinigameInventory(player, event.getInventory())) {
            return;
        }

        minigameManager.handleInventoryClose(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        minigameManager.handleQuit(event.getPlayer());
    }
}
