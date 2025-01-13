package org.hopef.parkour.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LobbyManager {

    private final File file;
    private final FileConfiguration config;

    public LobbyManager(File dataFolder) {
        file = new File(dataFolder, "spawn.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void setLobby(Location spawn){
        config.set("lobby.world", spawn.getWorld().getName());
        config.set("lobby.x", spawn.getX());
        config.set("lobby.y", spawn.getY());
        config.set("lobby.z", spawn.getZ());
        config.set("lobby.yaw", spawn.getYaw());
        config.set("lobby.pitch", spawn.getPitch());
        save();
    }

    public Location getLobby() {
        if (!config.contains("lobby")) return null;

        String worldName = config.getString("lobby.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            System.out.println("World '" + worldName + "' is not loaded or does not exist!");
            return null;
        }

        double x = config.getDouble("lobby.x");
        double y = config.getDouble("lobby.y");
        double z = config.getDouble("lobby.z");
        float yaw = (float) config.getDouble("lobby.yaw");
        float pitch = (float) config.getDouble("lobby.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public World getParkourWorld() {
        // Obtém o mundo de parkour da chave correta 'parkour-world'
        String worldName = config.getString("lobby.world", "world"); // Valor padrão é "world"
        return Bukkit.getWorld(worldName);
    }

    private void save(){
        try{
            config.save(file);
        }catch (IOException e){
            System.out.println("Error saving lobby file");
            e.printStackTrace();
        }
    }

}
