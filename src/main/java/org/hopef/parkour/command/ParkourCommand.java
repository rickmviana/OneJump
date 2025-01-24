package org.hopef.parkour.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.manager.LobbyManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;

public class ParkourCommand implements CommandExecutor{

    private final LobbyManager lobbyManager;
    private final WorldChecker worldChecker;

    public ParkourCommand(LobbyManager lobbyManager, WorldChecker worldChecker) {
        this.lobbyManager = lobbyManager;
        this.worldChecker = worldChecker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!player.getWorld().getName().equalsIgnoreCase("lobby") && !worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.LOBBY_COMMAND.getText());
            return true;
        }

        Location lobby = lobbyManager.getLobby();
        if (lobby == null) {
            player.sendMessage(Constantes.LOBBY_NOT_DEFINED.getText());
            return true;
        }

        ItemStack item = ItemManager.createItem(
                Material.COMPASS,
                "§dMain Menu §8[§7Right-Click§8]",
                1,
                "§7Use this item to select the map you want to go and practice."
        );
        player.teleport(lobby);
        player.sendMessage(Constantes.TELEPORT_FOR_LOBBY.getText());
        player.getInventory().setItem(3, item);
        return true;
    }
}
