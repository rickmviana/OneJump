package org.hopef.parkour.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.hopef.parkour.OneJump.playerTemporaryCheckpoint;
import static org.hopef.parkour.OneJump.pressurePlateCheckpoints;
import static org.hopef.parkour.types.YmlManager.maps;

/**
 * The {@code GUIManager} class provides functionality to create and display dynamic
 * inventory menus (GUIs) in a Minecraft plugin. This includes automatic glass panel
 * filling for menu borders and custom item placement.
 */
public abstract class GUIManager {

    public static final Map<Location, String> lorebook = new HashMap<>();

    /**
     * Displays a custom menu (GUI) to a player.
     *
     * @param player          the player to whom the menu will be displayed
     * @param title           the title of the menu
     * @param size            the size of the menu (must be a multiple of 9)
     * @param glassSlots      a set of inventory slots to fill with default glass panels; if null, fills the borders
     * @param customItems     a map of inventory slots to custom {@link ItemStack} items
     * @param customGlassSlots a map of specific inventory slots to glass panel colors (key: slot, value: color)
     * @throws IllegalArgumentException if the size is not a positive multiple of 9
     */
    public static void displayMenu(Player player, String title, int size, Set<Integer> glassSlots,
                                   Map<Integer, ItemStack> customItems, Map<Integer, Short> customGlassSlots) {
        if (size % 9 != 0 || size <= 0) {
            throw new IllegalArgumentException("Size defined must be a number that is a multiple of 9.");
        }

        Inventory menu = Bukkit.createInventory(player, size, title);

        fillWithGlass(menu, glassSlots, customGlassSlots);

        if (customItems != null) {
            for (Map.Entry<Integer, ItemStack> entry : customItems.entrySet()) {
                int slot = entry.getKey();
                ItemStack item = entry.getValue();

                if (slot >= 0 && slot < size) {
                    menu.setItem(slot, item);
                }
            }
        }
        player.openInventory(menu);
    }

    public static void selectMapMenu(Player player, String title, int size, Set<Integer> glassSlots, Map<Integer, ItemStack> customItem, Map<Integer, Short> customGlassSlots) {
        if (size % 9 != 0 || size <= 0) {
            throw new IllegalArgumentException("Size defined must be a number that is a multiple of 9.");
        }

        Inventory menu = Bukkit.createInventory(player, size, title);

        fillWithGlass(menu, glassSlots, customGlassSlots);

        if (customItem != null) {
            for (Map.Entry<Integer, ItemStack> entry : customItem.entrySet()) {
                int slot = entry.getKey();
                ItemStack item = entry.getValue();

                if (slot >= 0 && slot < size) {
                    menu.setItem(slot, item);
                }
            }
        }

        for (Map.Entry<String, Location> entry : maps.entrySet()) {
            ItemStack item = new ItemStack(Material.MAP);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(entry.getKey());
            item.setItemMeta(meta);
            menu.addItem(item);
        }
        player.openInventory(menu);
    }

    /**
     * Displays a custom menu (GUI) to a player.
     *
     * @param player          the player to whom the menu will be displayed
     * @param title           the title of the menu
     * @param size            the size of the menu (must be a multiple of 9)
     * @param glassSlots      a set of inventory slots to fill with default glass panels; if null, fills the borders
     * @param customGlassSlots a map of specific inventory slots to glass panel colors (key: slot, value: color)
     * @throws IllegalArgumentException if the size is not a positive multiple of 9
     */
    public static void displayPressure(Player player, String title, int size, Location plateLocatio, Set<Integer> glassSlots,
                                       Map<Integer, Short> customGlassSlots) {
        if (size % 9 != 0 || size <= 0) {
            throw new IllegalArgumentException("Size defined must be a number that is a multiple of 9.");
        }

        Inventory menu = Bukkit.createInventory(player, size, title);

        fillWithGlass(menu, glassSlots, customGlassSlots);

        ItemStack ironPlate = new ItemStack(Material.IRON_PLATE);
        ItemMeta meta = ironPlate.getItemMeta();
        meta.setDisplayName("§bSet designated coordinates");
        ItemStack book_quill = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta metas = book_quill.getItemMeta();
        metas.setDisplayName("§aWrite strategy");

        Location loc = pressurePlateCheckpoints.get(plateLocatio);
        List<String> lore = getString(loc);

        String stg = lorebook.get(plateLocatio);
        List<String> lore2 = getString(stg);

        meta.setLore(lore);
        metas.setLore(lore2);
        ironPlate.setItemMeta(meta);
        book_quill.setItemMeta(metas);
        menu.setItem(11, ironPlate);
        menu.setItem(15, book_quill);
        player.openInventory(menu);
        playerTemporaryCheckpoint.put(player.getUniqueId(), plateLocatio);
    }

    /**
     * Fills specified slots in the inventory with glass panels. If {@code glassSlots} is null,
     * automatically fills the borders (top, bottom, left, and right) of the inventory.
     *
     * @param menu             the inventory to fill
     * @param glassSlots       a set of slots to fill with default glass panels; if null, fills the borders
     * @param customGlassColors a map of specific slots to custom glass panel colors (key: slot, value: color)
     */
    private static void fillWithGlass(Inventory menu, Set<Integer> glassSlots, Map<Integer, Short> customGlassColors) {

        ItemStack defaultGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3); // Azul claro
        ItemMeta defaultMeta = defaultGlass.getItemMeta();
        defaultMeta.setDisplayName(" ");
        defaultGlass.setItemMeta(defaultMeta);

        for (int i = 0; i < menu.getSize(); i++) {
            if (glassSlots == null && (i < 9 || i >= menu.getSize() - 9 || i % 9 == 0 || (i + 1) % 9 == 0)) {
                menu.setItem(i, defaultGlass);
            } else if (glassSlots != null && glassSlots.contains(i)) {
                menu.setItem(i, defaultGlass);
            }
        }

        if (customGlassColors != null) {
            for (Map.Entry<Integer, Short> entry : customGlassColors.entrySet()) {
                int slot = entry.getKey();
                short color = entry.getValue();

                if (slot >= 0 && slot < menu.getSize()) {
                    ItemStack customGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
                    ItemMeta customMeta = customGlass.getItemMeta();
                    customMeta.setDisplayName(" ");
                    customGlass.setItemMeta(customMeta);
                    menu.setItem(slot, customGlass);
                }
            }
        }
    }

    // TODO: ADD DOC...
    private static List<String> getString(Location checkpointLocation) {
        List<String> lore = new ArrayList<>();

        lore.add("§7―――――――――――――――");

        if (checkpointLocation != null) {
            lore.add("§7x: " + checkpointLocation.getX());
            lore.add("§7y: " + checkpointLocation.getY());
            lore.add("§7z: " + checkpointLocation.getZ());
            lore.add("§7yaw: " + checkpointLocation.getYaw());
            lore.add("§7pitch: " + checkpointLocation.getPitch());
        }else {
            lore.add("§cNo checkpoint set");
        }
        return lore;
    }

    private static List<String> getString(String lore) {
        List<String> loreList = new ArrayList<>();

        loreList.add("§7――――――――――――――");

        if (lore != null) {
            loreList.add("§6Current Strategya");
            loreList.add("§7» §e-");
        } else {
            loreList.add("§6Current Strategy");
            loreList.add("§7» §e-");
        }

        return loreList;
    }

//    /**
//     * Adiciona itens de navegação (próxima página e página anterior) ao menu.
//     *
//     * @param menu          O inventário do menu.
//     * @param page          Número da página atual.
//     * @param totalItems    Número total de itens a serem exibidos.
//     * @param itemsPerPage  Número de itens por página.
//     */
//    private static void addNavigationItems(Inventory menu, int page, int totalItems, int itemsPerPage) {
//        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
//
//        // Botão para página anterior
//        if (page > 1) {
//            ItemStack prevPage = new ItemStack(Material.ARROW);
//            ItemMeta prevMeta = prevPage.getItemMeta();
//            prevMeta.setDisplayName("§ePágina Anterior");
//            prevPage.setItemMeta(prevMeta);
//            menu.setItem(menu.getSize() - 9, prevPage); // Slot inferior esquerdo
//        }
//
//        // Botão para próxima página
//        if (page < totalPages) {
//            ItemStack nextPage = new ItemStack(Material.ARROW);
//            ItemMeta nextMeta = nextPage.getItemMeta();
//            nextMeta.setDisplayName("§ePróxima Página");
//            nextPage.setItemMeta(nextMeta);
//            menu.setItem(menu.getSize() - 1, nextPage); // Slot inferior direito
//        }
//    }

}
