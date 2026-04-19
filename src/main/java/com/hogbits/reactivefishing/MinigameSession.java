package com.hogbits.reactivefishing;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public final class MinigameSession {
    private final UUID playerId;
    private final FishHook hook;
    private ItemStack pendingLoot;
    private final int catchStartSlot;
    private final int catchEndSlot;

    private Inventory inventory;
    private BukkitTask movementTask;
    private BukkitTask timeoutTask;
    private int fishSlot = 9;
    private int direction = 1;
    private boolean finished;

    public MinigameSession(UUID playerId, FishHook hook, ItemStack pendingLoot, int catchStartSlot) {
        this.playerId = playerId;
        this.hook = hook;
        this.pendingLoot = pendingLoot;
        this.catchStartSlot = catchStartSlot;
        this.catchEndSlot = catchStartSlot + 2;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public FishHook getHook() {
        return hook;
    }

    public ItemStack getPendingLoot() {
        return pendingLoot;
    }

    public void setPendingLoot(ItemStack pendingLoot) {
        this.pendingLoot = pendingLoot;
    }

    public int getCatchStartSlot() {
        return catchStartSlot;
    }

    public int getCatchEndSlot() {
        return catchEndSlot;
    }

    public int getFishSlot() {
        return fishSlot;
    }

    public void advanceFish() {
        if (fishSlot == 17) {
            direction = -1;
        } else if (fishSlot == 9) {
            direction = 1;
        }
        fishSlot += direction;
    }

    public boolean isFishInCatchZone() {
        return fishSlot >= catchStartSlot && fishSlot <= catchEndSlot;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public BukkitTask getMovementTask() {
        return movementTask;
    }

    public void setMovementTask(BukkitTask movementTask) {
        this.movementTask = movementTask;
    }

    public BukkitTask getTimeoutTask() {
        return timeoutTask;
    }

    public void setTimeoutTask(BukkitTask timeoutTask) {
        this.timeoutTask = timeoutTask;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinished() {
        this.finished = true;
    }

    public void cancelTasks() {
        if (movementTask != null) {
            movementTask.cancel();
            movementTask = null;
        }
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

    public boolean isInventory(Inventory inventory) {
        return this.inventory != null && this.inventory.equals(inventory);
    }

    public static final class MinigameInventoryHolder implements org.bukkit.inventory.InventoryHolder {
        private final UUID playerId;
        private Inventory inventory;

        public MinigameInventoryHolder(UUID playerId) {
            this.playerId = playerId;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        @Override
        public Inventory getInventory() {
            return inventory != null ? inventory : Bukkit.createInventory(this, 27);
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }
}
