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

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;

import static org.hopef.parkour.OneJump.playerTemporaryCheckpoint;
import static org.hopef.parkour.OneJump.pressurePlateCheckpoints;

public class SPCCommand implements CommandExecutor {

    private final WorldChecker worldChecker;

    public SPCCommand(WorldChecker checker) {
        this.worldChecker = checker;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Constantes.COMMAND_SENDER.getText());
                return true;
            }

            Player player = (Player) commandSender;
            if (!worldChecker.isInAllowedWorld(player)) {
                player.sendMessage(Constantes.WORLD_ALLOWED.getText());
                return true;
            }

            Location checkpoint = player.getLocation();

            if (!playerTemporaryCheckpoint.containsKey(player.getUniqueId())) {
                player.sendMessage(Constantes.PRESSURE_PLATE_INTERACT.getText());
                return true;
            }

            Location plateLocation = playerTemporaryCheckpoint.get(player.getUniqueId());

            pressurePlateCheckpoints.put(plateLocation, checkpoint);

            player.sendMessage("§r§8[§r§6§lM§r§e§lV§r§8] §r§7Successfully set §r§adesignated coords §r§7for the checkpoint.§r");
            playerTemporaryCheckpoint.remove(player.getUniqueId());

            return true;
    }
}
