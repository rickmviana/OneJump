package org.hopef.parkour.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.types.ItemFactory;
import org.hopef.parkour.types.Menu;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.GameModes;
import org.hopef.parkour.utils.WorldChecker;

public class BuildCommand extends ItemFactory implements CommandExecutor, Listener {

    private final ItemManager itemManager;
    private final WorldChecker worldChecker;

    public BuildCommand(ItemManager itemManager , WorldChecker worldChecker, FileConfiguration config) {
        super(config);
        this.itemManager = itemManager;
        this.worldChecker = worldChecker;
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
        Menu.buildMenu(player, "§8Build Menu", 45);

        return true;
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

                ItemFactory.handleItemInteraction(player, displayName, clickedItem);
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

            GameModes.togglePlayerMode(player, displayName, itemManager, event);
            ItemFactory.handleItemClick(player, displayName, item);
            player.updateInventory();

        }
    }
}
