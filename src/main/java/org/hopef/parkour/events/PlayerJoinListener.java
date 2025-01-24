package org.hopef.parkour.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.hopef.parkour.utils.Scoreboard;

public class PlayerJoinListener implements Listener {

    private final Scoreboard scoreboardManager;

    public PlayerJoinListener(JavaPlugin plugin) {
        this.scoreboardManager = new Scoreboard(plugin, plugin.getDataFolder());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scoreboardManager.setupScoreboard(player);
    }
}
