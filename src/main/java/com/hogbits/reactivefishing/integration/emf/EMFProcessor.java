package com.hogbits.reactivefishing.integration.emf;

import com.hogbits.reactivefishing.ReactiveFishingPlugin;
import com.hogbits.reactivefishing.event.ReactiveFishingCatchEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import java.util.Map;

public final class EMFProcessor implements Listener {
    private final ReactiveFishingPlugin plugin;

    public EMFProcessor(ReactiveFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onReactiveFishingCatch(ReactiveFishingCatchEvent event) {
        if (!plugin.isEmfEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        FishHook hook = event.getHook();
        if (hook == null || !hook.isValid()) {
            return;
        }

        ItemStack item = event.getDefaultCatch().orElseGet(() -> new ItemStack(Material.COD));
        Location location = event.getLocation();
        Item caughtEntity = location.getWorld().dropItem(location, item.clone());
        caughtEntity.setPickupDelay(Integer.MAX_VALUE);

        PlayerFishEvent syntheticCatch = new PlayerFishEvent(
                player,
                caughtEntity,
                hook,
                PlayerFishEvent.State.CAUGHT_FISH
        );
        Bukkit.getPluginManager().callEvent(syntheticCatch);

        // EMF (or other fishing integrations) should now control the reward path.
        event.setCancelled(true);

        if (syntheticCatch.isCancelled()) {
            removeIfValid(caughtEntity);
            return;
        }

        // Move any resulting item entity into the player's inventory instead of leaving it at the hook.
        if (!caughtEntity.isValid()) {
            return;
        }

        ItemStack reward = caughtEntity.getItemStack().clone();
        caughtEntity.remove();

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(reward);
        leftovers.values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private void removeIfValid(Entity entity) {
        if (entity != null && entity.isValid()) {
            entity.remove();
        }
    }

    public static boolean isEvenMoreFishPresentAndEnabled() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EvenMoreFish");
        return plugin != null && plugin.isEnabled();
    }
}
