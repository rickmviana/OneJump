package org.hopef.parkour.command;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hopef.parkour.manager.InventoryFactory;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.manager.checkpoints.CheckpointManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;

import java.util.HashSet;
import java.util.Set;

import static org.hopef.parkour.manager.ItemManager.createItem;

public class BuildCommand implements CommandExecutor, Listener {

    private final ItemManager itemManager;
    private final CheckpointManager checkpointManager;
    private final WorldChecker worldChecker;
    private final Player player;
    private final Set<Player> hiddenPlayers = new HashSet<>();

    public BuildCommand(ItemManager itemManager, CheckpointManager checkpointManager, WorldChecker worldChecker, Player player) {
        this.itemManager = itemManager;
        this.checkpointManager = checkpointManager;
        this.worldChecker = worldChecker;
        this.player = player;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        Inventory menu = Bukkit.createInventory(null, 45, "§8Build Menu");
        fillWithGlass(menu);

        // Adicionar os itens configurados
        menu.setItem(20, itemManager.getItem("return-to-checkpoint"));
        menu.setItem(21, itemManager.getItem("sword"));
        menu.setItem(22, itemManager.getItem("checkpoint"));
        menu.setItem(23, itemManager.getItem("adventure"));
        menu.setItem(24, itemManager.getItem("hide"));

        player.openInventory(menu);
        return true;
    }

    private void fillWithGlass(Inventory menu) {
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < menu.getSize(); i++) {
            if (i < 9 || i >= menu.getSize() - 9 || i % 9 == 0 || (i + 1) % 9 == 0) {
                menu.setItem(i, glass);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Lógica para o menu Build
        if (event.getView().getTitle().equals("§8Build Menu")) {
            event.setCancelled(true); // Impede que os itens sejam movidos

            if (event.getCurrentItem() == null || event.getClick() != ClickType.LEFT) return; // Garante que a ação só acontece com o botão esquerdo

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName() != null) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                // Verifica se o jogador já tem o item no inventário
                if (player.getInventory().contains(clickedItem)) {
                    player.sendMessage("§cYou already have this item in your inventory.");
                    return;
                }

                switch (displayName) {
                    case "§aSet new checkpoint §8[§7Right-Click§8]":
                    case "§cBack to checkpoint §8[§7Right-Click§8]":
                    case "§aHide player §8[§7Right-Click§8]":
                    case "§bJump Mode §8[§7Right-Click§8]":
                    case "§6Setup coordinates":
                    case "§6Finish Plate":
                        player.getInventory().addItem(clickedItem);
                        break;

                    default:
                        player.sendMessage("§cAction not configured for this item.");
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

            jumpMode(player, displayName, itemManager, event);

            switch (displayName){
                case "§aSet new checkpoint §8[§7Right-Click§8]":
                    checkpointManager.setPlayerCheckpoint(player, player.getLocation());
                    player.sendMessage(Constantes.NEW_CHECKPOINT.getText());
                    break;
                case "§cBack to checkpoint §8[§7Right-Click§8]":
                    Location checkpoint = checkpointManager.getPlayerCheckpoint(player);
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
                    isEnchantment(showPlayersItem, Enchantment.DURABILITY);
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

                default:
                    break;
            }
        }
    }

    public static void jumpMode(Player player, String displayName, ItemManager itemManager, PlayerInteractEvent event) {
        if (displayName.equals(Constantes.JUMP_MODE.getText())) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                player.setGameMode(GameMode.CREATIVE);
                InventoryFactory.clearInventory(player);
                InventoryFactory.restoreInventory(player);
                player.sendMessage(Constantes.JUMP_MODE_DISABLE.getText());
            } else {
                InventoryFactory.saveInventory(player);
                player.setGameMode(GameMode.ADVENTURE);
                InventoryFactory.clearInventory(player);
                itemManager.giveItem(player, "checkpoint", 4);
                itemManager.giveItem(player, "return-to-checkpoint", 2);
                itemManager.giveItem(player, "adventure", 8);
                itemManager.giveItem(player, "sword", 3);
                itemManager.giveItem(player, "hide", 7);
                player.sendMessage(Constantes.JUMP_MODE_ENABLE.getText());
            }
            event.setCancelled(true);
        }
    }

    public static void jumpMode(Player player, ItemManager itemManager) {
        if (player.getGameMode() == GameMode.ADVENTURE) {
            player.setGameMode(GameMode.CREATIVE);
            InventoryFactory.clearInventory(player);
            InventoryFactory.restoreInventory(player);
            player.sendMessage(Constantes.JUMP_MODE_DISABLE.getText());
        } else {
            InventoryFactory.saveInventory(player);
            player.setGameMode(GameMode.ADVENTURE);
            InventoryFactory.clearInventory(player);
            itemManager.giveItem(player, "checkpoint", 4);
            itemManager.giveItem(player, "return-to-checkpoint", 2);
            itemManager.giveItem(player, "adventure", 8);
            itemManager.giveItem(player, "sword", 3);
            itemManager.giveItem(player, "hide", 7);
            player.sendMessage(Constantes.JUMP_MODE_ENABLE.getText());
        }
    }

    public static void isEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, 1, true);
        item.setItemMeta(meta);
    }

}
