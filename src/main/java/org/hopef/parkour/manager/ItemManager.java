package org.hopef.parkour.manager;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

/**
 * The ItemManager class handles the creation and management of custom items
 * based on a configuration file. It also provides methods to give items to players.
 */
public class ItemManager {

    private final FileConfiguration config;

    /**
     * Constructs an ItemManager instance.
     *
     * @param config The configuration file containing item definitions.
     */
    public ItemManager(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Retrieves an ItemStack from the configuration file based on its ID.
     *
     * @param id The unique identifier of the item in the configuration file.
     * @return The corresponding ItemStack, or null if the item does not exist.
     * @throws IllegalArgumentException If the material specified in the configuration is invalid.
     */
    public ItemStack getItem(String id) {
        if (!config.contains("items." + id)) return null;

        String materialName = config.getString("items." + id + ".material");
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material: " + materialName);
        }

        int data = config.getInt("items." + id + ".data", 0); // Default data value is 0
        String displayName = config.getString("items." + id + ".display-name");
        List<String> lore = config.getStringList("items." + id + ".lore");

        // Create the item with specified material, data, and other properties
        ItemStack item = new ItemStack(material, 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (displayName != null) meta.setDisplayName(displayName);
            if (lore != null && !lore.isEmpty()) meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Gives a custom item to the specified player by placing it in a specific inventory slot.
     *
     * @param player The player to receive the item.
     * @param id     The unique identifier of the item in the configuration file.
     * @param slot   The inventory slot where the item will be placed.
     */
    public void giveItem(Player player, String id, int slot) {
        ItemStack item = getItem(id);
        if (item != null) {
            player.getInventory().setItem(slot, item);
        } else {
            player.sendMessage("§cNo such item: " + id);
        }
    }

    /**
     * Creates a custom ItemStack with the specified properties.
     *
     * @param material    The material of the item.
     * @param amount      The quantity of the item in the stack.
     * @param displayName The display name of the item.
     * @param lore        The lore (description) of the item.
     * @return A customized ItemStack with the specified properties.
     */
    public static ItemStack createItem(Material material, String displayName, int amount, String lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a custom ItemStack with the specified properties.
     *
     * @param material    The material of the item.
     * @param data        The data value of the item (e.g., for colored blocks).
     * @param amount      The quantity of the item in the stack.
     * @param displayName The display name of the item.
     * @param lore        The lore (description) of the item.
     * @return A customized ItemStack with the specified properties.
     */
    public static ItemStack createItem(Material material, short data, int amount, String displayName, String lore) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        return item;
    }

}

