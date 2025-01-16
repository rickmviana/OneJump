package org.hopef.parkour.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Enum that represents all types of stained glass panes in Minecraft,
 * including their color IDs and corresponding color names.
 */
public enum GlassPaneType {
    WHITE((short) 0, "White"),
    ORANGE((short) 1, "Orange"),
    MAGENTA((short) 2, "Magenta"),
    LIGHT_BLUE((short) 3, "Light Blue"),
    YELLOW((short) 4, "Yellow"),
    LIME((short) 5, "Lime"),
    PINK((short) 6, "Pink"),
    GRAY((short) 7, "Gray"),
    LIGHT_GRAY((short) 8, "Light Gray"),
    CYAN((short) 9, "Cyan"),
    PURPLE((short) 10, "Purple"),
    BLUE((short) 11, "Blue"),
    BROWN((short) 12, "Brown"),
    GREEN((short) 13, "Green"),
    RED((short) 14, "Red"),
    BLACK((short) 15, "Black");

    private final short id;
    private final String colorName;

    /**
     * Constructor for the enum.
     *
     * @param id        The ID of the color.
     * @param colorName The name of the color.
     */
    GlassPaneType(short id, String colorName) {
        this.id = id;
        this.colorName = colorName;
    }

    /**
     * Retrieves the ID of the color.
     *
     * @return The color ID.
     */
    public short getId() {
        return id;
    }

    /**
     * Retrieves the name of the color.
     *
     * @return The color name.
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * Creates an ItemStack of the stained glass pane based on this type.
     *
     * @return The corresponding ItemStack.
     */
    public ItemStack toItemStack() {
        ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, id);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName("§f" + colorName + " Glass Pane");
        glassPane.setItemMeta(meta);
        return glassPane;
    }
}
