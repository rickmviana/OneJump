package org.hopef.parkour.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YmlManager implements Listener {

    protected static final Map<String, Location> maps = new HashMap<>();
    private static final File FILE = new File("plugins/OneJump", "maps.yml");
    private static final YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);

    private final JavaPlugin plugin;

    public YmlManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void saveMap(String mapName, Location location) {
        config.set("maps." + mapName + ".world", location.getWorld().getName());
        config.set("maps." + mapName + ".x", location.getX());
        config.set("maps." + mapName + ".y", location.getY());
        config.set("maps." + mapName + ".z", location.getZ());
        config.set("maps." + mapName + ".yaw", location.getYaw());
        config.set("maps." + mapName + ".pitch", location.getPitch());
        maps.put(mapName, location); // Atualiza o cache
        saveConfigFile();
    }

    public static void loadMaps() {
        if (config.contains("maps")) {
            for (String key : config.getConfigurationSection("maps").getKeys(false)) {
                String worldName = config.getString("maps." + key + ".world");
                double x = config.getDouble("maps." + key + ".x");
                double y = config.getDouble("maps." + key + ".y");
                double z = config.getDouble("maps." + key + ".z");
                float yaw = (float) config.getDouble("maps." + key + ".yaw");
                float pitch = (float) config.getDouble("maps." + key + ".pitch");
                World world = Bukkit.getWorld(worldName);

                if (world != null) {
                    maps.put(key, new Location(world, x, y, z, yaw, pitch));
                }
            }
        }
    }

    public static void removeMap(String mapName) {
        config.set("maps." + mapName, null);
        maps.remove(mapName); // Remove do cache
        saveConfigFile();
    }

    private static void saveConfigFile() {
        try {
            config.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8Maps Menu")) {
            event.setCancelled(true); // Impede que o jogador retire itens do menu

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.MAP) {
                String mapName = event.getCurrentItem().getItemMeta().getDisplayName();
                Player player = (Player) event.getWhoClicked();

                if (maps.containsKey(mapName)) {
                    Location location = maps.get(mapName);
                    player.teleport(location);
                    player.sendMessage("§aTeleportado para o mapa: §e" + mapName);
                } else {
                    player.sendMessage("§cMapa não encontrado!");
                }

                player.closeInventory(); // Fecha o menu após o clique
            }
        }
    }

    public static Location getMap(String mapName) {
        return maps.get(mapName);
    }
}
