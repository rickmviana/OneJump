package org.hopef.parkour.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.JSONManager;
import org.hopef.parkour.utils.WorldChecker;

public class TpMapCommand implements CommandExecutor {

    private final WorldChecker worldChecker;

    public TpMapCommand(WorldChecker checker) {
        this.worldChecker = checker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Uso: /tpmap <id>");
            return true;
        }

        String id = args[0];
        Location loc = JSONManager.getLocationById(id);
        if (loc == null) {
            player.sendMessage("§cNenhuma localização encontrada para o ID: §e" + id);
            return true;
        }

        player.teleport(loc);
        player.sendMessage("§aTeleportado para a localização do ID: §e" + id);
        return true;
    }
}
