package org.hopef.parkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class JSONManager {

    private static final File file = new File("plugins/OneJump/data.json");

    // Salvar localização
    public static void saveLocation(Location loc, String id) {
        try {
            JSONObject data = file.exists() ? loadData() : new JSONObject();


            JSONObject locData = getPosition(loc);

            data.put(id, locData);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(data.toString(4)); // Formatação com indentação
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getPosition(Location loc) {
        JSONObject locData = new JSONObject();
        if (loc.getWorld() != null) {
            locData.put("world", loc.getWorld().getName());
            locData.put("x", loc.getX());
            locData.put("y", loc.getY());
            locData.put("z", loc.getZ());
            locData.put("yaw", loc.getYaw());
            locData.put("pitch", loc.getPitch());
        }

        return locData;
    }

    // Obter localização pelo ID
    public static Location getLocationById(String id) {
        try {
            if (!file.exists()) return null;

            JSONObject data = loadData();
            if (!data.has(id)) return null;

            JSONObject locData = data.getJSONObject(id);

            return new Location(
                    Bukkit.getWorld(locData.getString("world")),
                    locData.getDouble("x"),
                    locData.getDouble("y"),
                    locData.getDouble("z")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Carregar JSON do arquivo
    public static JSONObject loadData() {
        try (FileReader reader = new FileReader(file)) {
            StringBuilder content = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                content.append((char) ch);
            }
            return new JSONObject(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    // Remover localização pelo ID
    public static void removeLocation(String id) {
        try {
            if (!file.exists()) return;

            JSONObject data = loadData();
            if (data.has(id)) {
                data.remove(id);

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(data.toString(4)); // Atualiza o arquivo com a entrada removida
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
