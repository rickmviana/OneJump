package org.hopef.parkour.command;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.hopef.parkour.utils.GameModes;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;
import org.hopef.parkour.manager.ItemManager;

public class JumpCommand implements CommandExecutor {

    private final ItemManager itemManager;
    private final WorldChecker worldChecker;

    public JumpCommand(ItemManager manager, WorldChecker worldChecker) {
        this.itemManager = manager;
        this.worldChecker = worldChecker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        // Verifica se o jogador está no mundo permitido
        if (!worldChecker.isInAllowedWorld(player)) {
            player.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        GameModes.jumpMode(player, itemManager);

        return true;
    }
}
