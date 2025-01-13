package org.hopef.parkour.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.hopef.parkour.manager.LobbyManager;

public class FallDamageEvent implements Listener {

    private final LobbyManager lobbyManager;

    public FallDamageEvent(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World parkourWorld = lobbyManager.getParkourWorld(); // Obtém o mundo de parkour

            if (player.getWorld().equals(parkourWorld)) { // Compara o objeto World
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);  // Cancela o dano de queda
                }
            }
        }
    }
}
