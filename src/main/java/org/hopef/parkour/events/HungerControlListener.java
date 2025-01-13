package org.hopef.parkour.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.hopef.parkour.utils.WorldChecker;

public class HungerControlListener implements Listener {

    private final WorldChecker worldChecker;

    public HungerControlListener(WorldChecker checker) {
        this.worldChecker = checker;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // Verifica se o evento foi causado por um jogador
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (!worldChecker.isInAllowedWorld(player)) return;

            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20.0f);

        }
    }
}
