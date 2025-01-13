package org.hopef.parkour.manager;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class ItemManager {

    private final FileConfiguration config;

    public ItemManager(FileConfiguration config) {
        this.config = config;
    }

    public ItemStack getItem(String id) {
        if (!config.contains("items." + id)) return null;

        String materialName = config.getString("items." + id + ".material");
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material: " + materialName);
        }

        int data = config.getInt("items." + id + ".data", 0); // Obtém o valor 'data', padrão 0
        String displayName = config.getString("items." + id + ".display-name");
        List<String> lore = config.getStringList("items." + id + ".lore");

        ItemStack item = new ItemStack(material, 1, (short) data); // Adiciona o data ao ItemStack
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (displayName != null) meta.setDisplayName(displayName);
            if (lore != null && !lore.isEmpty()) meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void giveItem(Player player, String id, int slot) {
        ItemStack item = getItem(id);
        if (item != null) {
            player.getInventory().setItem(slot, item);
        } else {
            player.sendMessage("§cNo such item: " + id);
        }
    }

    public static ItemStack createItem(Material material, short data, int amount, String displayName, String lore) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        return item;
    }
}

