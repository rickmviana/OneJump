package org.hopef.parkour.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.GameModes;
import org.hopef.parkour.utils.WorldChecker;

public class PracticeCommand implements CommandExecutor {

    private final ItemManager itemManager;
    private final WorldChecker worldChecker;

    public PracticeCommand(ItemManager itemManager, WorldChecker worldChecker) {
        this.itemManager = itemManager;
        this.worldChecker = worldChecker;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) commandSender;

        if (!worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        GameModes.spectatorMode(player, itemManager);
        return true;
    }
}
