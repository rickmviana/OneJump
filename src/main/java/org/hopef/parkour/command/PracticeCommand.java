/*
 *     Copyright (C) 2025 rickmviana
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

        GameModes.practiceMode(player, itemManager);
        return true;
    }
}
