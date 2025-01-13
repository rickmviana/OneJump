package org.hopef.parkour.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class InventoryFactory {

    private static final Map<UUID, SavedInventory> savedInventories = new HashMap<>();

    /**
     * Saves the player's current inventory.
     *
     * @param player The player whose inventory will be saved.
     */
    public static void saveInventory(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack[] inventoryContents = player.getInventory().getContents();
        ItemStack[] armorContents = player.getInventory().getArmorContents();

        SavedInventory savedInventory = new SavedInventory(inventoryContents, armorContents);
        savedInventories.put(uuid, savedInventory);
    }

    /**
     * Restores the player's saved inventory, if any.
     *
     * @param player The player whose inventory will be restored.
     */
    public static void restoreInventory(Player player) {
        UUID uuid = player.getUniqueId();
        if (!savedInventories.containsKey(uuid)) {
            return;
        }

        SavedInventory savedInventory = savedInventories.get(uuid);
        player.getInventory().setContents(savedInventory.getInventoryContents());
        player.getInventory().setArmorContents(savedInventory.getArmorContents());

        player.updateInventory();

        // Optional: remove saved inventory after restoring
        savedInventories.remove(uuid);
    }

    /**
     * Clears a player's saved inventory, if any.
     *
     * @param player The player whose saved inventory will be cleared.
     */
    public static void clearSavedInventory(Player player) {
        UUID uuid = player.getUniqueId();
        savedInventories.remove(uuid);
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    /**
     *  Class to store inventory contents.
     */
    private static class SavedInventory {
        private final ItemStack[] inventoryContents;
        private final ItemStack[] armorContents;

        public SavedInventory(ItemStack[] inventoryContents, ItemStack[] armorContents) {
            this.inventoryContents = inventoryContents.clone();
            this.armorContents = armorContents.clone();
        }

        public ItemStack[] getInventoryContents() {
            return inventoryContents;
        }

        public ItemStack[] getArmorContents() {
            return armorContents;
        }
    }
}
