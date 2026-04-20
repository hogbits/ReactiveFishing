package com.hogbits.reactivefishing;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GUIBuilder {
    private static final int[] DECORATIVE_SEAGRASS_SLOTS = {18, 20, 21, 23, 24, 26};
    private static final int[] DECORATIVE_KELP_SLOTS = {19, 22, 25};
    private final ConfigManager configManager;

    public GUIBuilder(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Inventory create(MinigameSession session, String title) {
        MinigameSession.MinigameInventoryHolder holder =
                new MinigameSession.MinigameInventoryHolder(session.getPlayerId());
        Inventory inventory = org.bukkit.Bukkit.createInventory(holder, 27, Component.text(title));
        holder.setInventory(inventory);
        session.setInventory(inventory);
        render(session);
        return inventory;
    }

    public void render(MinigameSession session) {
        Inventory inventory = session.getInventory();
        if (inventory == null) {
            return;
        }

        for (int slot = 0; slot <= 8; slot++) {
            Material mat = (slot % 2 == 0) ? Material.CYAN_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            inventory.setItem(slot, pane(mat));
        }

        for (int slot = 9; slot <= 17; slot++) {
            Material mat = ((slot - 9) % 2 == 0) ? Material.LIGHT_BLUE_STAINED_GLASS_PANE : Material.CYAN_STAINED_GLASS_PANE;
            inventory.setItem(slot, pane(mat));
        }

        for (int slot : DECORATIVE_SEAGRASS_SLOTS) {
            inventory.setItem(slot, plain(Material.SEAGRASS));
        }
        for (int slot : DECORATIVE_KELP_SLOTS) {
            inventory.setItem(slot, plain(Material.KELP));
        }

        for (int slot = session.getCatchStartSlot(); slot <= session.getCatchEndSlot(); slot++) {
            inventory.setItem(slot, pane(Material.LIME_STAINED_GLASS_PANE));
        }

        inventory.setItem(session.getFishSlot(), fishIcon());
    }

    private ItemStack pane(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" "));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack plain(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" "));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack fishIcon() {
        ItemStack fish = new ItemStack(configManager.getFishIndicatorMaterial());
        ItemMeta meta = fish.getItemMeta();
        meta.displayName(Component.text("Fish"));
        fish.setItemMeta(meta);
        return fish;
    }
}
