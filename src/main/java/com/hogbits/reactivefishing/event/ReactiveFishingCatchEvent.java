package com.hogbits.reactivefishing.event;

import java.util.Objects;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReactiveFishingCatchEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Location location;
    private final FishHook hook;
    private ItemStack defaultCatch;
    private boolean cancelled;

    public ReactiveFishingCatchEvent(
            @NotNull Player player,
            @NotNull Location location,
            @Nullable FishHook hook,
            @Nullable ItemStack defaultCatch
    ) {
        this.player = Objects.requireNonNull(player, "player");
        this.location = Objects.requireNonNull(location, "location");
        this.hook = hook;
        this.defaultCatch = defaultCatch == null ? null : defaultCatch.clone();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Location getLocation() {
        return location.clone();
    }

    @Nullable
    public FishHook getHook() {
        return hook;
    }

    @NotNull
    public Optional<ItemStack> getDefaultCatch() {
        return defaultCatch == null ? Optional.empty() : Optional.of(defaultCatch.clone());
    }

    public void setDefaultCatch(@Nullable ItemStack defaultCatch) {
        this.defaultCatch = defaultCatch == null ? null : defaultCatch.clone();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
