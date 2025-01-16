package org.hopef.parkour.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.manager.ItemManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.GameModes;
import org.hopef.parkour.utils.WorldChecker;

public class SpectatorCommand implements CommandExecutor {

    private final ItemManager itemManager;
    private final WorldChecker worldChecker;

    public SpectatorCommand(ItemManager itemManager, WorldChecker worldChecker) {
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

        GameModes.spectatorMode(player, itemManager);
        return true;
    }
}
