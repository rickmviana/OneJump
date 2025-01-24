package org.hopef.parkour.manager.checkpoints;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class CheckpointManager {

    private static final Map<String, Location> playerCheckpoints = new HashMap<>();
    private static final Map<String, Location> returnCheckpoint = new HashMap<>();
    private static final Map<String, Location> practiceCheckpoint = new HashMap<>();

    /*
     *  For pressure plate and back to checkpoint
     */

    public static void setPlayerCheckpoint(Player player, Location loc) {
        playerCheckpoints.put(player.getName(), loc);
    }

    public static Location getPlayerCheckpoint(Player player) {
        return playerCheckpoints.get(player.getName());
    }

    public static void updateCheckpointForPlayer(Player player, Location newCheckpoint) {
        playerCheckpoints.put(player.getName(), newCheckpoint);
    }

    /*
     *  When entering and exiting Jump Mode
     */

    public static Location getReturnCheckpoint(Player player) {
        return returnCheckpoint.get(player.getName());
    }

    public static void setReturnCheckpoint(Player player, Location location) {
        returnCheckpoint.put(player.getName(), location);
    }

    /*
     * Practice Mode Checkpoint
     */

    public static Location getPracticeCheckpoint(Player player) {
        return practiceCheckpoint.get(player.getName());
    }

    public static void setPracticeCheckpoint(Player player, Location location) {
        practiceCheckpoint.put(player.getName(), location);
    }

}
