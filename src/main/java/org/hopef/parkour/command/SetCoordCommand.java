package org.hopef.parkour.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hopef.parkour.utils.Constantes;

import java.util.HashMap;
import java.util.Map;

public class SetCoordCommand implements CommandExecutor {

    private static final Map<Player, String> playerIds = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /setcoord (ID)");
            return true;
        }

        String id = args[0];
        playerIds.put(player, id); // Associa o ID ao jogador

        ItemStack pressure = new ItemStack(Material.GOLD_PLATE);
        ItemMeta meta = pressure.getItemMeta();
        meta.setDisplayName("§eSet Location: " + id);
        pressure.setItemMeta(meta);

        player.getInventory().addItem(pressure);
        player.sendMessage("§aPressure plate created with ID: §e" + id);
        return true;
    }

    public static String getPlayerId(Player player) {
        return playerIds.get(player);
    }

    public static void removePlayerId(Player player) {
        playerIds.remove(player);
    }
}