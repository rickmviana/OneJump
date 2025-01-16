package org.hopef.parkour.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.hopef.parkour.OneJump.playerTemporaryCheckpoint;
import static org.hopef.parkour.OneJump.pressurePlateCheckpoints;

/**
 * The {@code GUIManager} class provides functionality to create and display dynamic
 * inventory menus (GUIs) in a Minecraft plugin. This includes automatic glass panel
 * filling for menu borders and custom item placement.
 */
public abstract class GUIManager {

    private static final Map<UUID, Integer> playerPageMap = new HashMap<>();

//    /**
//     * Creates and displays a specific menu page for the player.
//     *
//     * @param player Player who will see the menu.
//     * @param title Base title of the menu.
//     * @param size Menu size (multiple of 9).
//     * @param page Current page number.
//     * @param glassSlots Slots that will receive decorative glass.
//     * @param customItems List of items to be displayed paginated.
//     * @param customGlassSlots a map of specific inventory slots to glass panel colors (key: slot, value: color)
//     */
//    public static void createMenuPage(Player player, String title, int size, int page,
//                                      Set<Integer> glassSlots, Map<Integer, ItemStack> customItems,
//                                      Map<Integer, Short> customGlassSlots) {
//        if (size % 9 != 0 || size <= 0) {
//            throw new IllegalArgumentException("Size must be a multiple of 9.");
//        }
//
//        Inventory menu = Bukkit.createInventory(player, size, title + " - Page " + page);
//
//        // Preenche os slots com vidro decorativo
//        fillWithGlass(menu, glassSlots, customGlassSlots);
//
//        // Adiciona itens personalizados do config.yml
//        if (customItems != null) {
//            for (Map.Entry<Integer, ItemStack> entry : customItems.entrySet()) {
//                int slot = entry.getKey();
//                ItemStack item = entry.getValue();
//
//                if (slot >= 0 && slot < size) {
//                    menu.setItem(slot, item);
//                }
//            }
//        }
//
//        // Calcula itens desta página e adiciona ao inventário
//        int itemsPerPage = size - 9; // Slots disponíveis excluindo a linha de navegação
//        int startIndex = (page - 1) * itemsPerPage;
//        int endIndex = Math.min(startIndex + itemsPerPage, customItems.size());
//
//        List<ItemStack> itemsList = new ArrayList<>(customItems.values());
//        for (int i = startIndex; i < endIndex; i++) {
//            if (i < itemsList.size()) {
//                menu.addItem(itemsList.get(i));
//            }
//        }
//
//        // Adiciona itens de navegação
//        addNavigationItems(menu, page, customItems.size(), itemsPerPage);
//
//        // Abre o menu para o jogador e salva a página atual
//        player.openInventory(menu);
//        playerPageMap.put(player.getUniqueId(), page);
//    }

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

        ItemStack item = new ItemStack(Material.IRON_PLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bSet designated coordinates");

        Location loc = pressurePlateCheckpoints.get(plateLocatio);
        List<String> lore = getString(loc);
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(13, item);
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

        lore.add("§7---------------------");

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

    /**
     * Adiciona itens de navegação (próxima página e página anterior) ao menu.
     *
     * @param menu          O inventário do menu.
     * @param page          Número da página atual.
     * @param totalItems    Número total de itens a serem exibidos.
     * @param itemsPerPage  Número de itens por página.
     */
    private static void addNavigationItems(Inventory menu, int page, int totalItems, int itemsPerPage) {
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // Botão para página anterior
        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§ePágina Anterior");
            prevPage.setItemMeta(prevMeta);
            menu.setItem(menu.getSize() - 9, prevPage); // Slot inferior esquerdo
        }

        // Botão para próxima página
        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§ePróxima Página");
            nextPage.setItemMeta(nextMeta);
            menu.setItem(menu.getSize() - 1, nextPage); // Slot inferior direito
        }
    }

//    /**
//     * Handles menu clicks, managing navigation between pages.
//     *
//     * @param event Inventory click event.
//     * @param titleBase Base of the menu title (without page number).
//     * @param size Inventory size (multiple of 9).
//     * @param items Complete list of items to be displayed paginated.
//     */
//    public static void handleMenuClick(InventoryClickEvent event, String titleBase, int size, Map<Integer, ItemStack> items) {
//        if (!(event.getWhoClicked() instanceof Player)) return;
//
//        Player player = (Player) event.getWhoClicked();
//        UUID playerId = player.getUniqueId();
//        Inventory inventory = event.getInventory();
//
//        // Verifica se o menu é relevante
//        if (!inventory.getTitle().startsWith(titleBase)) return;
//
//        event.setCancelled(true); // Impede interações normais
//
//        ItemStack clickedItem = event.getCurrentItem();
//        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
//
//        String displayName = clickedItem.getItemMeta().getDisplayName();
//        int currentPage = playerPageMap.getOrDefault(playerId, 1);
//
//        // Navegação entre páginas
//        if ("§ePróxima Página".equals(displayName)) {
//            createMenuPage(player, titleBase, size, currentPage + 1, null, items);
//        } else if ("§ePágina Anterior".equals(displayName) && currentPage > 1) {
//            createMenuPage(player, titleBase, size, currentPage - 1, null, items);
//        }
//    }

}
