package org.hopef.parkour.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.hopef.parkour.command.SetCoordCommand;
import org.hopef.parkour.utils.JSONManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PressurePlateListener implements Listener {

    private static final Map<String, Location> locationCache = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.GOLD_PLATE) {
            Player player = event.getPlayer();
            String id = SetCoordCommand.getPlayerId(player); // Pega o ID associado ao jogador

            if (id == null) {
                player.sendMessage("§cNo ID associated with this action. Use /setcoord (ID) first!");
                return;
            }

            // Salva a localização com o ID do jogador
            JSONManager.saveLocation(block.getLocation(), id);
            player.sendMessage("§aPressure plate saved with ID: §e" + id);

            // Cache para referência rápida
            locationCache.put(id, block.getLocation());

            // Remove o ID associado ao jogador
            SetCoordCommand.removePlayerId(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.GOLD_PLATE) {
            // Localização do bloco quebrado
            Location blockLocation = block.getLocation();

            // Verifica se a localização existe no cache ou JSON
            String matchingId = null;
            for (Map.Entry<String, Location> entry : locationCache.entrySet()) {
                if (entry.getValue().equals(blockLocation)) {
                    matchingId = entry.getKey();
                    break;
                }
            }

            // Caso não esteja no cache, verificar diretamente no JSON
            if (matchingId == null) {
                JSONObject data = JSONManager.loadData();
                for (String id : data.keySet()) {
                    JSONObject locData = data.getJSONObject(id);
                    if (locData != null && locData.has("world")) {
                        Location savedLocation = new Location(
                                Bukkit.getWorld(locData.getString("world")),
                                locData.getDouble("x"),
                                locData.getDouble("y"),
                                locData.getDouble("z")
                        );
                        if (savedLocation.equals(blockLocation)) {
                            matchingId = id;
                            break;
                        }
                    }
                }
            }

            if (matchingId != null) {
                // Remove a localização do JSON
                JSONManager.removeLocation(matchingId);
                locationCache.remove(matchingId);

                Player player = event.getPlayer();
                player.sendMessage("§aLocation with ID: §e" + matchingId + " §aremoved!");
            }
        }
    }
}
