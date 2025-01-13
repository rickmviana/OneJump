package org.hopef.parkour.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.hopef.parkour.manager.InventoryFactory;
import org.hopef.parkour.utils.WorldChecker;

public class WorldChange implements Listener {

    WorldChecker checker;

    public WorldChange(WorldChecker checker) {
        this.checker = checker;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldCharge(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!checker.isInAllowedWorld(player)) return;

        InventoryFactory.clearInventory(player);
        InventoryFactory.clearSavedInventory(player);
        player.setGameMode(GameMode.CREATIVE);
    }
}
