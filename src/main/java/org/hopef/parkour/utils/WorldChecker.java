package org.hopef.parkour.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldChecker {

    private final String allowedWorld;

    public WorldChecker(File dataFolder) {
        this.allowedWorld = getWorldNameFromSpawn(dataFolder);
    }

    public  boolean isInAllowedWorld(Player player) {
        if (allowedWorld == null) {
            // Log para depuração
            System.out.println("Nenhum mundo permitido configurado.");
            return true; // Se não houver restrição de mundo, permitir qualquer mundo
        }

        String worldName = player.getWorld().getName();
        return allowedWorld.equalsIgnoreCase(worldName); // Lista de mundos permitidos
    }

    private String getWorldNameFromSpawn(File dataFolder) {
        File spawnFile = new File(dataFolder, "spawn.yml");
        if (!spawnFile.exists()) {
            System.out.println("spawn.yml não encontrado, utilizando o mundo padrão.");  // Log de depuração
            return "world"; // Retorna o mundo padrão caso o arquivo não exista
        }

        YamlConfiguration spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        String worldName = spawnConfig.getString("lobby.world", "world"); // "world" é o valor padrão
        System.out.println("Mundo permitido carregado do spawn.yml: " + worldName); // Log de depuração
        return worldName;
    }
}
