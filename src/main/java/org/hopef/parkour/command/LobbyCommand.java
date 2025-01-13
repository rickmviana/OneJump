package org.hopef.parkour.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.manager.LobbyManager;
import org.hopef.parkour.utils.Constantes;

public class LobbyCommand implements CommandExecutor {

    private final LobbyManager lobbyManager;

    public LobbyCommand(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("onejump.setlobby")){
            player.sendMessage("You need onejump.setlobby permission to use this command");
            return true;
        }

        Location loc = player.getLocation();
        lobbyManager.setLobby(loc);
        player.sendMessage("§bSpawn lobby successfully set!");
        return true;
    }
}
