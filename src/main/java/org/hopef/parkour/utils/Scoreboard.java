/*
 *     Copyright (C) 2025 rickmviana
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.hopef.parkour.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.skycraft.scoreboards.FastBoard;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private static final Gson gson = new Gson();
    private static File dataFile;
    private static JavaPlugin plugin;

    public Scoreboard(JavaPlugin plugin, File dataFolder) {
        Scoreboard.plugin = plugin;
        dataFile = new File(dataFolder, "playerData.json");
        loadPlayerData();
    }

    public static void setupScoreboard(Player player) {
        FastBoard board = new FastBoard(player);

        String title = plugin.getConfig().getString("scoreboard.title", "§b§lMPK§7-§f§lParkour");
        board.updateTitle(ChatColor.translateAlternateColorCodes('&', title));

        PlayerData data = getPlayerData(player.getUniqueId());

        board.updateLines(
                "",
                "§3User: §f" + player.getName(),
                "",
                "§fCargo: §7" + getRole(player),
                "§fNível: §7(Em breve)",
                "",
                "§6Parkour Score: §e" + data.getParkourScore(),
                "§2Jump Count: §a" + data.getJumpCount(),
                "",
                "Jogadores: §a" + Bukkit.getOnlinePlayers().size()
        );
    }

    private static String getRole(Player player) {
        LuckPerms luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);
        if (luckPerms == null) {
            return "Desconhecido";
        }
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "Membro";
        }
        String primaryGroup = user.getPrimaryGroup();
        Group group = luckPerms.getGroupManager().getGroup(primaryGroup);
        return group != null ? group.getFriendlyName() : "Membro";
    }

    public static void incrementJumpCount(Player player) {
        PlayerData data = getPlayerData(player.getUniqueId());
        data.incrementJumpCount();
        savePlayerData();
    }

    public static int getJumpCount(Player player) {
        return getPlayerData(player.getUniqueId()).getJumpCount();
    }

    public static void addParkourScore(Player player, int score) {
        PlayerData data = getPlayerData(player.getUniqueId());
        data.addParkourScore(score);
        savePlayerData();
    }

    public void removeScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.delete();
    }

    private static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> new PlayerData());
    }

    private static void savePlayerData() {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(playerDataMap, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Erro ao salvar dados dos jogadores: " + e.getMessage());
        }
    }

    private static void loadPlayerData() {
        if (!dataFile.exists()) {
            return;
        }
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>() {}.getType();
            Map<UUID, PlayerData> data = gson.fromJson(reader, type);
            if (data != null) {
                playerDataMap.putAll(data);
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Erro ao carregar dados dos jogadores: " + e.getMessage());
        }
    }

    public static class PlayerData {
        private int jumpCount;
        private int parkourScore;

        public int getJumpCount() {
            return jumpCount;
        }

        public void incrementJumpCount() {
            this.jumpCount++;
        }

        public int getParkourScore() {
            return parkourScore;
        }

        public void addParkourScore(int score) {
            this.parkourScore += score;
        }
    }
}