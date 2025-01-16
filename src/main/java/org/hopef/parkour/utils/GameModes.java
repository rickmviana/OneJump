package org.hopef.parkour.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.hopef.parkour.manager.InventoryFactory;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.manager.checkpoints.CheckpointManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The GameModes class provides utility methods for managing game modes
 * and handling game-specific logic, such as toggling between adventure
 * and creative modes in a parkour plugin.
 */
public abstract class GameModes {

    // Mapeamento de jogadores para seus modos de jogo
    private static final Map<Player, PlayerMode> playerModes = new HashMap<>();

    /**
     * Sets the player's game mode.
     *
     * @param player The player.
     * @param mode The mode to set.
     */
    public static void setPlayerMode(Player player, PlayerMode mode) {
        playerModes.put(player, mode);
    }

    /**
     * Gets the player's game mode.
     *
     * @param player The player.
     * @return The player's current mode or SPECTATOR as default.
     */
    public static PlayerMode getPlayerMode(Player player) {
        return playerModes.getOrDefault(player, PlayerMode.NORMAL_MODE);
    }

    /**
     * Removes the player from the mapping (e.g. when leaving the server).
     *
     * @param player The player.
     */
    public static void removePlayer(Player player) {
        playerModes.remove(player);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     *  JUMPING MODE
     */

    /**
     * Handles the jump mode toggle based on the display name of the clicked item.
     * If the item's display name matches the jump mode constant, it toggles the player's game mode.
     *
     * @param player       The player who interacted with the item.
     * @param displayName  The display name of the item the player interacted with.
     * @param itemManager  The item manager responsible for giving items to players.
     * @param event        The PlayerInteractEvent to potentially cancel further processing.
     */
    public static void togglePlayerMode(Player player, String displayName, ItemManager itemManager, PlayerInteractEvent event) {
        PlayerMode currentMode = getPlayerMode(player);

        if (displayName.equals(Constantes.JUMP_MODE.getText())) {
            if (currentMode == PlayerMode.JUMP_MODE_ENABLE) {
                setPlayerMode(player, PlayerMode.JUMP_MODE_DISABLE);
            } else {
                setPlayerMode(player, PlayerMode.JUMP_MODE_ENABLE);
            }
            setGameMode(player, itemManager);
            event.setCancelled(true);
            return;
        }

        if (displayName.equals(Constantes.SPECTATOR_ITEM_ENABLE.getText())) {
            if (currentMode != PlayerMode.SPECTATOR_ENABLE) {
                setPlayerMode(player, PlayerMode.SPECTATOR_ENABLE);
            }
            setGameMode(player, itemManager);
            event.setCancelled(true);
            return;
        }

        if (displayName.equals(Constantes.SPECTATOR_ITEM_DISABLE.getText())) {
            if (currentMode == PlayerMode.SPECTATOR_ENABLE) {
                setPlayerMode(player, PlayerMode.SPECTATOR_DISABLE);
            }
            setGameMode(player, itemManager);
            event.setCancelled(true);
            return;
        }

        if (displayName.equals(Constantes.PRACTICE_ITEM_ENABLE.getText())) {
            if (currentMode != PlayerMode.PRACTICE_ENABLE) {
                setPlayerMode(player, PlayerMode.PRACTICE_ENABLE);
            }
            setGameMode(player, itemManager);
            event.setCancelled(true);
            return;
        }

        if (displayName.equals(Constantes.PRACTICE_ITEM_DISABLE.getText())) {
            if (currentMode == PlayerMode.PRACTICE_ENABLE) {
                setPlayerMode(player, PlayerMode.PRACTICE_DISABLE);
            }
            setGameMode(player, itemManager);
            event.setCancelled(true);
        }
    }


    /**
     * Toggles the player's game mode to jump mode (Adventure) or resets to the default (Creative).
     * This version does not require an event or display name for invocation.
     *
     * @param player      The player whose game mode is being toggled.
     * @param itemManager The item manager responsible for giving items to players.
     */
    public static void jumpMode(Player player, ItemManager itemManager) {
        // Obtém o estado atual do jogador
        PlayerMode currentMode = getPlayerMode(player);

        // Alterna o estado do modo Jump
        if (currentMode == PlayerMode.JUMP_MODE_ENABLE) {
            // Desativa o modo Jump
            setPlayerMode(player, PlayerMode.JUMP_MODE_DISABLE);
        } else {
            // Ativa o modo Jump
            removePlayer(player);
            setPlayerMode(player, PlayerMode.JUMP_MODE_ENABLE);
        }

        // Aplica as alterações de estado ao jogador
        setGameMode(player, itemManager);
    }

    public static void spectatorMode(Player player, ItemManager itemManager) {
        PlayerMode currentMode = getPlayerMode(player);

        if (currentMode == PlayerMode.SPECTATOR_ENABLE) {
            setPlayerMode(player, PlayerMode.SPECTATOR_DISABLE);
        } else {
            removePlayer(player);
            setPlayerMode(player, PlayerMode.SPECTATOR_ENABLE);
        }

        setGameMode(player, itemManager);
    }

    public static void practiceMode(Player player, ItemManager itemManager) {
        PlayerMode currentMode = getPlayerMode(player);

        if (currentMode == PlayerMode.PRACTICE_ENABLE) {
            setPlayerMode(player, PlayerMode.PRACTICE_DISABLE);
        } else {
            removePlayer(player);
            setPlayerMode(player, PlayerMode.PRACTICE_ENABLE);
        }

        setGameMode(player, itemManager);
    }

    /**
     * Toggles the player's game mode and manages inventory and teleportation based on the player's current mode.
     * The player's mode is determined by the {@link PlayerMode} enumeration and affects their interaction with the game world.
     *
     * Behavior:
     * - {@link PlayerMode#JUMP_MODE_ENABLE}:
     *   - Saves the player's current inventory and clears it.
     *   - Sets the player's game mode to Adventure.
     *   - Saves the player's current location as a checkpoint.
     *   - Assigns parkour-specific items:
     *     - Checkpoint setter
     *     - Return to last checkpoint
     *     - Adventure navigation tool
     *     - Sword
     *     - Hide item
     *   - Sends a message indicating that Jump Mode has been enabled.
     *
     * - {@link PlayerMode#JUMP_MODE_DISABLE}:
     *   - Sets the player's game mode to Creative.
     *   - Teleports the player to their last saved checkpoint.
     *   - Clears the player's inventory and restores their previously saved inventory.
     *   - Sends a message indicating that Jump Mode has been disabled.
     *
     * - {@link PlayerMode#SPECTATOR_ENABLE}:
     *   - Saves the player's current inventory and clears it.
     *   - Sets the player's game mode to Adventure.
     *   - Saves the player's current location as a checkpoint.
     *   - Assigns spectator-specific items:
     *     - Checkpoint setter
     *     - Return to last checkpoint
     *     - Practice mode navigation tool
     *     - Sword
     *     - Fly tool
     *     - Player teleportation tool
     *     - Disable spectator mode item
     *   - Sends a message indicating that Spectator Mode has been enabled.
     *
     * - {@link PlayerMode#SPECTATOR_DISABLE}:
     *   - Sets the player's game mode to Creative.
     *   - Teleports the player to their last saved checkpoint.
     *   - Clears the player's inventory and restores their previously saved inventory.
     *   - Sends a message indicating that Spectator Mode has been disabled.
     *
     * @param player      The player whose game mode and inventory are being managed.
     * @param itemManager The item manager responsible for assigning items to players.
     */

    private static void setGameMode(Player player, ItemManager itemManager) {
        switch (getPlayerMode(player)) {

            case JUMP_MODE_ENABLE:
                InventoryFactory.saveInventory(player);
                InventoryFactory.clearInventory(player);
                CheckpointManager.setReturnCheckpoint(player, player.getLocation());
                player.setGameMode(GameMode.ADVENTURE);
                itemManager.giveItem(player, "checkpoint", 4);
                itemManager.giveItem(player, "return-to-checkpoint", 2);
                itemManager.giveItem(player, "adventure", 8);
                itemManager.giveItem(player, "sword", 3);
                itemManager.giveItem(player, "hide", 7);
                player.sendMessage(Constantes.JUMP_MODE_ENABLE.getText());
                break;

            case JUMP_MODE_DISABLE:
                player.setGameMode(GameMode.CREATIVE);
                Location loc = CheckpointManager.getReturnCheckpoint(player);
                player.teleport(loc);
                InventoryFactory.clearInventory(player);
                InventoryFactory.restoreInventory(player);
                player.sendMessage(Constantes.JUMP_MODE_DISABLE.getText());
                break;

            case SPECTATOR_ENABLE:
                InventoryFactory.saveInventory(player);
                InventoryFactory.clearInventory(player);
                CheckpointManager.setReturnCheckpoint(player, player.getLocation());
                player.setGameMode(GameMode.ADVENTURE);
                itemManager.giveItem(player, "checkpoint", 4);
                itemManager.giveItem(player, "prac-checkpoint", 2);
                itemManager.giveItem(player, "practice-mode", 8);
                itemManager.giveItem(player, "sword", 3);
                itemManager.giveItem(player, "fly", 7);
                itemManager.giveItem(player, "teleport-player", 5);
                itemManager.giveItem(player, "spectate-disable", 6);
                player.sendMessage(Constantes.SPECTATOR_MESSAGE_ENABLE.getText());
                break;

            case SPECTATOR_DISABLE:
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(CheckpointManager.getReturnCheckpoint(player));
                InventoryFactory.clearInventory(player);
                InventoryFactory.restoreInventory(player);
                player.sendMessage(Constantes.SPECTATOR_MESSAGE_DISABLE.getText());
                break;

            case PRACTICE_ENABLE:
                //InventoryFactory.saveInventory(player);
                InventoryFactory.clearInventory(player);
                if (CheckpointManager.getReturnCheckpoint(player) != null) {
                    player.teleport(CheckpointManager.getReturnCheckpoint(player));
                }
                CheckpointManager.setReturnCheckpoint(player, player.getLocation());
                player.setGameMode(GameMode.ADVENTURE);
                itemManager.giveItem(player, "checkpoint", 4);
                itemManager.giveItem(player, "prac-checkpoint", 2);
                itemManager.giveItem(player, "sword", 3);
                itemManager.giveItem(player, "hide", 5);
                itemManager.giveItem(player, "spectate-enable", 7);
                itemManager.giveItem(player, "disable-practice", 8);
                player.sendMessage(Constantes.PRACTICE_MESSAGE_ENABLE.getText());
                break;

            case PRACTICE_DISABLE:
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(CheckpointManager.getReturnCheckpoint(player));
                InventoryFactory.clearInventory(player);
                InventoryFactory.restoreInventory(player);
                player.sendMessage(Constantes.PRACTICE_MESSAGE_DISABLE.getText());
                break;

            default:
                player.setGameMode(GameMode.CREATIVE);
                InventoryFactory.restoreInventory(player);
                break;
        }
    }
}
