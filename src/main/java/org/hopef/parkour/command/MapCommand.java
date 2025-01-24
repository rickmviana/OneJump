package org.hopef.parkour.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;
import org.hopef.parkour.types.YmlManager;


public class MapCommand implements CommandExecutor {

    private static WorldChecker worldChecker;

    public MapCommand(WorldChecker checker) {
        MapCommand.worldChecker = checker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!worldChecker.isInAllowedWorld(player)) {
            sender.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        switch (label.toLowerCase()) {
            case "setmap":
                if (args.length != 1) {
                    player.sendMessage("§cUso correto: /setmap <nome_do_mapa>");
                    return true;
                }
                String mapName = args[0];
                Location location = player.getLocation();
                YmlManager.saveMap(mapName, location);
                player.sendMessage("§aMapa '" + mapName + "' salvo com sucesso!");
                break;

            case "removemap":
                if (args.length != 1) {
                    player.sendMessage("§cUso correto: /removemap <nome_do_mapa>");
                    return true;
                }
                String mapToRemove = args[0];
                if (YmlManager.getMap(mapToRemove) == null) {
                    player.sendMessage("§cMapa '" + mapToRemove + "' não encontrado.");
                    return true;
                }
                YmlManager.removeMap(mapToRemove);
                player.sendMessage("§aMapa '" + mapToRemove + "' removido com sucesso!");
                break;

            default:
                player.sendMessage("§cComando desconhecido.");
                break;
        }
        return true;
    }
}