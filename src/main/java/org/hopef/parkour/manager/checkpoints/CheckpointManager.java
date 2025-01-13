package org.hopef.parkour.manager.checkpoints;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class CheckpointManager {

    private final Map<String, Location> playerCheckpoints = new HashMap<>();

    public CheckpointManager() {
    }

    public void setPlayerCheckpoint(Player player, Location loc) {
        playerCheckpoints.put(player.getName(), loc);
    }

    public Location getPlayerCheckpoint(Player player) {
        return playerCheckpoints.get(player.getName());
    }

    public void updateCheckpointForPlayer(Player player, Location newCheckpoint) {
        playerCheckpoints.put(player.getName(), newCheckpoint);
    }

}
