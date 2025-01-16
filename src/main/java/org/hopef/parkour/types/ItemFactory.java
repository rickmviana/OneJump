package org.hopef.parkour.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.manager.checkpoints.CheckpointManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.GameModes;
import org.hopef.parkour.utils.PlayerMode;

/**
 * The {@code ItemFactory} class provides utility methods for handling item interactions and actions
 * triggered by players in the Parkour plugin. It processes specific item names to determine the
 * associated actions, such as setting checkpoints, toggling visibility, or enabling flight.
 *
 * <p>This abstract class contains methods that are used to:
 * <ul>
 *     <li>Add items to the player's inventory based on item display names.</li>
 *     <li>Perform specific actions when players interact with items.</li>
 *     <li>Apply enchantments to items programmatically.</li>
 * </ul>
 *
 * <p>Example usage includes setting player checkpoints, teleporting to saved locations, hiding
 * other players, and toggling flight mode.
 */
public abstract class ItemFactory extends ItemManager {


    /**
     * Constructs an ItemManager instance.
     *
     * @param config The configuration file containing item definitions.
     */
    public ItemFactory(FileConfiguration config) {
        super(config);
    }

    /**
     * Handles item interactions by adding items to the player's inventory based on the display name.
     * If the item does not match any predefined names, a default message is sent to the player.
     *
     * @param player      The {@link Player} interacting with the item.
     * @param displayName The display name of the item.
     * @param item        The {@link ItemStack} being interacted with.
     */
    protected static void handleItemInteraction(Player player, String displayName, ItemStack item) {
        switch (displayName) {
            case "§aSet new checkpoint §8[§7Right-Click§8]":
            case "§cBack to checkpoint §8[§7Right-Click§8]":
            case "§aHide player §8[§7Right-Click§8]":
            case "§aEnable Fly §8[§7Right-Click§8]":
            case "§bJump Mode §8[§7Right-Click§8]":
            case "§6Setup coordinates":
            case "§6Finish Plate":
            case "§dMain Menu":
                player.getInventory().addItem(item);
                break;

            default:
                player.sendMessage("§cAction not configured for this item.");
                break;
        }
    }

    /**
     * Processes the player's interaction with items and executes specific actions based on the
     * item's display name. Supported actions include setting checkpoints, teleportation, toggling
     * player visibility, and enabling or disabling flight.
     *
     * @param player      The {@link Player} interacting with the item.
     * @param displayName The display name of the item.
     * @param item        The {@link ItemStack} being interacted with.
     */
    protected static void handleItemClick(Player player, String displayName, ItemStack item) {
        switch (displayName){

            case "§aSet new checkpoint §8[§7Right-Click§8]":
                CheckpointManager.setPlayerCheckpoint(player, player.getLocation());
                CheckpointManager.setPracticeCheckpoint(player, player.getLocation());
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                player.sendMessage(Constantes.NEW_CHECKPOINT.getText());
                break;

            case "§cBack to checkpoint §8[§7Right-Click§8]":
                Location checkpoint = CheckpointManager.getPlayerCheckpoint(player);
                if (checkpoint != null) {
                    player.teleport(checkpoint);
                } else {
                    player.sendMessage("§cYou do not have a saved checkpoint.");
                }
                break;

            case "§aHide player §8[§7Right-Click§8]":
                for (Player other : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(other);
                }
                ItemStack showPlayersItem = createItem(Material.INK_SACK, (short) 8, 1,
                        Constantes.SHOW_PLAYERS.getText(), Constantes.SHOW_LORE.getText());
                isEnchantment(showPlayersItem);
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), showPlayersItem);
                player.sendMessage(Constantes.HIDE_MESSAGE.getText());
                break;

            case "§7Show players §8[§7Right-Click§8]":
                for (Player other : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(other);
                }
                ItemStack hidePlayersItem = createItem(Material.INK_SACK, (short) 10, 1,
                        Constantes.HIDE_PLAYERS.getText(), Constantes.HIDE_LORE.getText());
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), hidePlayersItem);
                player.sendMessage(Constantes.SHOW_MESSAGE.getText());
                break;

            case "§7Disable Fly §8[§7Right-Click§8]":
                if (item.getType() == Material.FEATHER){
                    if (player.getAllowFlight()){
                        ItemStack disableFly = createItem(Material.FEATHER, Constantes.FLY_ITEM_ENABLE.getText(),
                                1, Constantes.FLY_ITEM_LORE_ENABLE.getText());
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), disableFly);
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.sendMessage(Constantes.FLY_MESSAGE_DISABLE.getText());
                    }
                }
                break;

            case "§aEnable Fly §8[§7Right-Click§8]":
                if (item.getType() == Material.FEATHER){
                    if (!player.getAllowFlight()){
                        ItemStack enableFly = createItem(Material.FEATHER, Constantes.FLY_ITEM_DISABLE.getText(),
                                1, Constantes.FLY_ITEM_LORE_DISABLE.getText());
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), enableFly);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage(Constantes.FLY_MESSAGE_ENABLE.getText());
                    }
                }
                break;

            case "§eReturn to Checkpoint §8[§7Right-Click§8]":
                Location practice_checkpoint = CheckpointManager.getPracticeCheckpoint(player);
                if (practice_checkpoint != null) {
                    player.teleport(practice_checkpoint);
                } else {
                    player.sendMessage("§cYou do not have a saved checkpoint.");
                }
                break;

            default:
                break;
        }
    }

    /**
     * Adds the durability enchantment to the specified item if it has valid metadata.
     *
     * @param item The {@link ItemStack} to which the enchantment will be applied.
     */
    private static void isEnchantment(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
    }

}
