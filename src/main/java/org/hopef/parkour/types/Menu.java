package org.hopef.parkour.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hopef.parkour.manager.ItemManager;

import java.util.HashMap;
import java.util.Map;

import static org.hopef.parkour.types.GlassPaneType.*;

/**
 * The {@code Menu} class manages the creation and display of custom menus (GUIs) in the parkour plugin.
 * It provides methods for building menus with custom items and glass panels for different plugin functionalities.
 *
 * <p>The menus are displayed to players and allow interaction with various systems, such as parkour building,
 * map selection, and checkpoint management.</p>
 *
 * <p>The class extends {@link GUIManager} to provide menu display logic and integrates with {@link ItemManager}
 * for dynamic item management within the menu.</p>
 */
public final class Menu extends GUIManager {

    private static final Map<Integer, Short> CUSTOM_GLASS_BUILD = new HashMap<>();
    private static final Map<Integer, Short> CUSTOM_GLASS_SET_CHECKPOINT = new HashMap<>();
    private static final Map<Integer, Short> CUSTOM_GLASS_SELECT_MAP = new HashMap<>();

    private static final Map<Integer, ItemStack> CUSTOM_ITEM_BUILD = new HashMap<>();
    private static final Map<Integer, ItemStack> CUSTOM_ITEM_SELECT_MAP = new HashMap<>();

    private static ItemManager itemManager;

    /**
     * Constructor for the {@code Menu} class.
     *
     * @param itemManager the item manager that provides the custom items for the menus
     * @throws NullPointerException if the {@code itemManager} is null
     */
    public Menu(ItemManager itemManager) {
        if (itemManager == null) {
            throw new NullPointerException("itemManager is null");
        }
        Menu.itemManager = itemManager;
    }

    /*
     * MENU BUILD
     */

    /**
     * Builds and displays a custom menu to the player.
     * The menu is populated with custom items and glass panels. It can have different color configurations
     * and glass panel positions, as well as items related to parkour functionality.
     *
     * @param player the player who will see the menu
     * @param title  the title of the menu
     * @param size   the size of the menu (must be a multiple of 9)
     */
    public static void buildMenu(Player player, String title, int size) {
        // Creates the map for slot and color settings
        Map<int[], GlassPaneType> slotConfigurations = new HashMap<>();

        // Add slot and color pairs manually
        slotConfigurations.put(new int[]{0, 8, 36, 44}, BLACK);
        slotConfigurations.put(new int[]{9, 17, 18, 26, 27, 35}, WHITE);
        slotConfigurations.put(new int[]{1, 7, 37, 43}, LIGHT_BLUE);
        slotConfigurations.put(new int[]{2, 3, 5, 6, 38, 39, 41, 42}, CYAN);
        slotConfigurations.put(new int[]{40}, BLUE);

        // Iterates over the map to dynamically populate the CUSTOM_GLASS_BUILD
        for (Map.Entry<int[], GlassPaneType> entry : slotConfigurations.entrySet()) {
            int[] slots = entry.getKey();
            GlassPaneType glassType = entry.getValue();

            for (int slot : slots) {
                CUSTOM_GLASS_BUILD.put(slot, glassType.getId());
            }
        }

        CUSTOM_ITEM_BUILD.put(20, itemManager.getItem("return-to-checkpoint"));
        CUSTOM_ITEM_BUILD.put(21, itemManager.getItem("sword"));
        CUSTOM_ITEM_BUILD.put(22, itemManager.getItem("checkpoint"));
        CUSTOM_ITEM_BUILD.put(23, itemManager.getItem("adventure"));
        CUSTOM_ITEM_BUILD.put(24, itemManager.getItem("hide"));
        CUSTOM_ITEM_BUILD.put(30, itemManager.getItem("fly"));
        CUSTOM_ITEM_BUILD.put(32, itemManager.getItem("select-map"));
        CUSTOM_ITEM_BUILD.put(4, itemManager.getItem("info"));

        GUIManager.displayMenu(player, title, size, null, CUSTOM_ITEM_BUILD, CUSTOM_GLASS_BUILD);
    }

    /*
     * MENU MODIFY CHECKPOINT
     */

    public static void modifyMenu(Player player, String title, int size, Location loc) {
        Map<int[], GlassPaneType> slotConfigurations = new HashMap<>();

        slotConfigurations.put(new int[]{0, 8, 18, 26}, BLACK);
        slotConfigurations.put(new int[]{9, 17}, WHITE);
        slotConfigurations.put(new int[]{1, 2, 3, 4, 5, 6, 7, 19, 20, 21, 22, 23, 24, 25}, GRAY);

        for (Map.Entry<int[], GlassPaneType> entry : slotConfigurations.entrySet()) {
            int[] slots = entry.getKey();
            GlassPaneType glassType = entry.getValue();

            for (int slot : slots) {
                CUSTOM_GLASS_SET_CHECKPOINT.put(slot, glassType.getId());
            }
        }

        GUIManager.displayPressure(player, title, size, loc, null, CUSTOM_GLASS_SET_CHECKPOINT);
    }

    /*
     * MENU SELECT MAP
     */

    public static void selectMenu(Player player, String title, int size) {
        Map<int[], GlassPaneType> slotConfigurations = new HashMap<>();

        slotConfigurations.put(new int[]{0, 8, 36, 44}, WHITE);
        slotConfigurations.put(new int[]{1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43}, GREEN); // 1 2 6 7 9 16 25 35 37 38 42 43
        slotConfigurations.put(new int[]{3, 5, 18, 26, 39, 40, 41}, LIME); // 3 5 17 24 39 40 41

        for (Map.Entry<int[], GlassPaneType> entry : slotConfigurations.entrySet()) {
            int[] slots = entry.getKey();
            GlassPaneType glassType = entry.getValue();

            for (int slot : slots) {
                CUSTOM_GLASS_SELECT_MAP.put(slot, glassType.getId());
            }
        }

        CUSTOM_ITEM_SELECT_MAP.put(4, itemManager.getItem("map-info"));

        GUIManager.selectMapMenu(player, title, size, null, CUSTOM_ITEM_SELECT_MAP, CUSTOM_GLASS_SELECT_MAP);
    }
}
