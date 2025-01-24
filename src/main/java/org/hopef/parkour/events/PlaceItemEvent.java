package org.hopef.parkour.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceItemEvent implements Listener {

    @EventHandler
    public void onPlayerPlaceItem(PlayerInteractEvent event) {

        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            ItemStack item = event.getItem();

            if (item != null && item.getType() == Material.REDSTONE) {
                event.setCancelled(true);
            }
            if (item != null && item.getType() == Material.FISHING_ROD) {
                event.setCancelled(true);
            }
            if (item != null && item.getType() == Material.ENDER_PEARL) {
                event.setCancelled(true);
            }
        }
    }
}
