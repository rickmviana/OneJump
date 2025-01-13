package org.hopef.parkour.command;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.manager.InventoryFactory;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.GamePlayer;
import org.hopef.parkour.utils.WorldChecker;
import org.hopef.parkour.manager.ItemManager;

public class JumpCommand implements CommandExecutor {

    private final ItemManager itemManager;
    private final WorldChecker worldChecker;
    private final Player player;

    public JumpCommand(ItemManager manager, WorldChecker worldChecker, Player player) {
        this.itemManager = manager;
        this.worldChecker = worldChecker;
        this.player = player;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && worldChecker.isInAllowedWorld(player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        BuildCommand.jumpMode(player, itemManager);

        return true;
    }
}
