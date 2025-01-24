package org.hopef.parkour.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.hopef.parkour.manager.InventoryFactory;
import org.hopef.parkour.utils.Scoreboard;
import org.hopef.parkour.utils.WorldChecker;

public class WorldChange implements Listener {

    private final JavaPlugin plugin;
    WorldChecker checker;

    public WorldChange(JavaPlugin plugin, WorldChecker checker) {
        this.plugin = plugin;
        this.checker = checker;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldCharge(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!checker.isInAllowedWorld(player)) return;

        if (!player.getAllowFlight()){
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        switch (player.getGameMode()) {
            case SURVIVAL:
            case ADVENTURE:
                player.setGameMode(GameMode.CREATIVE);
                break;
            default:
                player.setGameMode(GameMode.ADVENTURE);
                break;
        }
        InventoryFactory.clearSavedInventory(player);
        InventoryFactory.clearInventory(player);
        Scoreboard.setupScoreboard(player);
    }
}
