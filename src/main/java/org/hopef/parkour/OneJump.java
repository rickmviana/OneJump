package org.hopef.parkour;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.hopef.parkour.command.*;
import org.hopef.parkour.events.*;
import org.hopef.parkour.manager.*;
import org.hopef.parkour.manager.checkpoints.CheckpointManager;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.JSONManager;
import org.hopef.parkour.utils.WorldChecker;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author hopef
 */
public final class OneJump extends JavaPlugin implements Listener {

    private final Map<Location, Location> pressurePlateCheckpoints = new HashMap<>();
    private final Map<UUID, Location> playerTemporaryCheckpoint = new HashMap<>();
    private final Set<Player> playersOnPlate = new HashSet<>();
    private File checkpointFile;

    private CheckpointManager checkpointManager;
    private LobbyManager lobbyManager;
    private WorldChecker worldChecker;
    private Player player;

    @Override
    public void onEnable() {
        getCommand("spc").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Constantes.COMMAND_SENDER.getText());
                return true;
            }

            Player player = (Player) sender;
            if (!worldChecker.isInAllowedWorld(player)) {
                player.sendMessage(Constantes.WORLD_ALLOWED.getText());
                return true;
            }

            Location checkpoint = player.getLocation();

            if (!playerTemporaryCheckpoint.containsKey(player.getUniqueId())) {
                player.sendMessage(Constantes.PRESSURE_PLATE_INTERACT.getText());
                return true;
            }

            Location plateLocation = playerTemporaryCheckpoint.get(player.getUniqueId());

            // Atualiza ou adiciona o checkpoint associado à pressure plate
            pressurePlateCheckpoints.put(plateLocation, checkpoint);

            player.sendMessage("§r§8[§r§6§lM§r§e§lV§r§8] §r§7Successfully set §r§adesignated coords §r§7for the checkpoint.§r");
            playerTemporaryCheckpoint.remove(player.getUniqueId());

            return true;
        });

        saveDefaultConfig();

        lobbyManager = new LobbyManager(getDataFolder());
        checkpointManager = new CheckpointManager();
        worldChecker = new WorldChecker(getDataFolder());

        registerCommands();
        registerEvents();
        loadCheckpoints();
    }


    @Override
    public void onDisable() {
        saveAllCheckpoints();
    }

    @EventHandler
    public void onPressurePlateClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.IRON_PLATE) return;

        Player player = event.getPlayer();
        if (!worldChecker.isInAllowedWorld(player)) return;

        Location plateLocation = event.getClickedBlock().getLocation();

        // Abre o menu de checkpoint para definir ou sobrescrever
        openCheckpointMenu(player, plateLocation);
    }

    @EventHandler
    public void onPressurePlateStep(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.IRON_PLATE) {
                Player player = event.getPlayer();

                if (!worldChecker.isInAllowedWorld(player)) return;

                event.setCancelled(true);

                if (!playersOnPlate.contains(player)) {
                    // Adiciona o jogador ao conjunto e toca o som
                    playersOnPlate.add(player);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 2.0F);

                    Location plateLocation = event.getClickedBlock().getLocation();

                    // Verifica se a pressure plate está associada a um checkpoint
                    if (pressurePlateCheckpoints.containsKey(plateLocation)) {
                        Location checkpoint = pressurePlateCheckpoints.get(plateLocation);
                        checkpointManager.updateCheckpointForPlayer(player, checkpoint);
                    } else {
                        player.sendMessage(Constantes.NO_ASSOCIATION.getText());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Verifica se o jogador saiu da placa de pressão
        if (playersOnPlate.contains(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            // Confirma que o jogador saiu da placa de pressão
            if (from.getBlockX() != to.getBlockX() ||
                    from.getBlockY() != to.getBlockY() ||
                    from.getBlockZ() != to.getBlockZ()) {

                Block blockBelow = to.getBlock();
                if (blockBelow.getType() != Material.IRON_PLATE) {
                    playersOnPlate.remove(player); // Remove o jogador do conjunto
                }
            }
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!worldChecker.isInAllowedWorld(player)) return;
        ItemStack item = player.getInventory().getItemInHand();

        // Verifica se o item é "§cBack to checkpoint"
        if (item != null && item.hasItemMeta() && "§cBack to checkpoint §8[§7Right-Click§8]".equals(item.getItemMeta().getDisplayName())) {
            Location checkpointLocation = checkpointManager.getPlayerCheckpoint(player);

            if (checkpointLocation != null) {
                player.teleport(checkpointLocation);
            }

            event.setCancelled(true); // Impede que o item seja consumido
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.IRON_PLATE) {
            Location plateLocation = event.getBlock().getLocation();

            if (pressurePlateCheckpoints.containsKey(plateLocation)) {
                pressurePlateCheckpoints.remove(plateLocation);

                try {
                    String content = new String(Files.readAllBytes(checkpointFile.toPath()));
                    if (!content.isEmpty()) {
                        JSONObject json = new JSONObject(content);
                        String plateKey = plateLocation.getWorld().getName() + ":" +
                                plateLocation.getBlockX() + "," +
                                plateLocation.getBlockY() + "," +
                                plateLocation.getBlockZ();

                        json.remove(plateKey);

                        Files.write(checkpointFile.toPath(), json.toString(4).getBytes());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                event.getPlayer().sendMessage(Constantes.REMOVE_CHECKPOINT.getText());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8Modify Checkpoint")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.IRON_PLATE &&
                    "§bSet designated coordinates".equals(clickedItem.getItemMeta().getDisplayName())) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();

                // Verifica se o jogador já interagiu com a plate
                if (playerTemporaryCheckpoint.containsKey(player.getUniqueId())) {
                    player.sendMessage(Constantes.SAVE_LOCATION.getText());
                } else {
                    player.sendMessage("§cNo temporary checkpoint set.");
                }
            }
        }
    }

    private void openCheckpointMenu(Player player, Location plateLocation) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8Modify Checkpoint");

        // Criando os itens de vidro com os diferentes tipos
        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15); // Vidro preto
        ItemMeta blackMeta = blackGlass.getItemMeta();
        blackMeta.setDisplayName(" "); // Sem nome
        blackGlass.setItemMeta(blackMeta);

        ItemStack whiteGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0); // Vidro branco
        ItemMeta whiteMeta = whiteGlass.getItemMeta();
        whiteMeta.setDisplayName(" "); // Sem nome
        whiteGlass.setItemMeta(whiteMeta);

        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7); // Vidro cinza
        ItemMeta grayMeta = grayGlass.getItemMeta();
        grayMeta.setDisplayName(" "); // Sem nome
        grayGlass.setItemMeta(grayMeta);

        // Preenchendo a parte de fora do inventário com os vidros corretos
        for (int i = 0; i < 27; i++) {
            if (i == 0 || i == 8 || i == 18 || i == 26) { // Pontas (Black Glass)
                inventory.setItem(i, blackGlass);
            } else if (i == 9 || i == 17) { // Meio das pontas (White Glass)
                inventory.setItem(i, whiteGlass);
            } else if (i >= 1 && i <= 7 || i >= 19 && i <= 25) { // Linha superior e inferior (Gray Glass)
                inventory.setItem(i, grayGlass);
            }
        }

        // Item central para definir checkpoint
        ItemStack pressurePlateItem = new ItemStack(Material.IRON_PLATE);
        ItemMeta plateMeta = pressurePlateItem.getItemMeta();
        plateMeta.setDisplayName("§bSet designated coordinates");

        // Verifica se já existe um checkpoint para essa plate e exibe as coordenadas
        Location checkpointLocation = pressurePlateCheckpoints.get(plateLocation);
        List<String> lore = getStrings(checkpointLocation);

        plateMeta.setLore(lore);

        pressurePlateItem.setItemMeta(plateMeta);

        inventory.setItem(13, pressurePlateItem); // Colocando o item central

        player.openInventory(inventory);

        // Armazena a localização temporária da plate para o jogador
        playerTemporaryCheckpoint.put(player.getUniqueId(), plateLocation);
    }

    private static List<String> getStrings(Location checkpointLocation) {
        List<String> lore = new ArrayList<>();

        // Adiciona a linha separadora
        lore.add("§7--------------------");

        if (checkpointLocation != null) {
            // Adiciona as coordenadas uma embaixo da outra
            lore.add("§7X: " + checkpointLocation.getX());
            lore.add("§7Y: " + String.format("%.1f", checkpointLocation.getY()));
            lore.add("§7Z: " + checkpointLocation.getZ());
            lore.add("§7Yaw: " + String.format("%.5f", checkpointLocation.getYaw()));
            lore.add("§7Pitch: " + String.format("%.5f", checkpointLocation.getPitch()));
        } else {
            lore.add("§cNo checkpoint set");
        }
        return lore;
    }

    private void loadCheckpoints() {
        checkpointFile = new File(getDataFolder(), "checkpoints.json");
        if (!checkpointFile.exists()) {
            try {
                checkpointFile.getParentFile().mkdirs();
                checkpointFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String content = new String(Files.readAllBytes(checkpointFile.toPath()));
            if (content.isEmpty()) {
                content = "{}";
            }
            JSONObject json = new JSONObject(content);
            for (String key : json.keySet()) {
                JSONObject plateData = json.getJSONObject(key);
                JSONObject checkpointData = plateData.getJSONObject("checkpoint");

                Location plateLocation = deserializeLocation(plateData.getJSONObject("plate"));
                Location checkpointLocation = deserializeLocation(checkpointData);

                if (plateLocation != null && checkpointLocation != null) {
                    pressurePlateCheckpoints.put(plateLocation, checkpointLocation);
                } else {
                    getLogger().warning("Skipping invalid checkpoint entry: " + key);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveAllCheckpoints() {
        JSONObject json = new JSONObject();

        for (Map.Entry<Location, Location> entry : pressurePlateCheckpoints.entrySet()) {
            Location plateLocation = entry.getKey();
            Location checkpointLocation = entry.getValue();

            // Dados do checkpoint
            JSONObject checkpointData = serializeLocation(checkpointLocation);

            // Dados da pressure plate
            JSONObject plateData = new JSONObject();
            plateData.put("plate", serializeLocation(plateLocation));
            plateData.put("checkpoint", checkpointData);

            // Use as coordenadas da pressure plate como chave
            String plateKey = plateLocation.getWorld().getName() + ":" +
                    plateLocation.getBlockX() + "," +
                    plateLocation.getBlockY() + "," +
                    plateLocation.getBlockZ();

            json.put(plateKey, plateData);
        }

        try {
            Files.write(checkpointFile.toPath(), json.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Location deserializeLocation(JSONObject locationData) {
        try {
            String worldName = locationData.getString("world");
            World world = Bukkit.getWorld(worldName);

            // Força o carregamento do mundo, caso ele ainda não tenha sido carregado
            if (world == null) {
                // Criar e carregar o mundo se ele não estiver carregado
                world = Bukkit.createWorld(new WorldCreator(worldName));
                if (world == null) {
                    world = Bukkit.getWorlds().get(0); // Fallback para o mundo padrão
                    getLogger().warning("World not found: " + worldName + ". Using default world.");
                }
            }

            double x = locationData.getDouble("x");
            double y = locationData.getDouble("y");
            double z = locationData.getDouble("z");
            float pitch = (float) locationData.getDouble("pitch");
            float yaw = (float) locationData.getDouble("yaw");

            return new Location(world, x, y, z, yaw, pitch);
        } catch (JSONException e) {
            getLogger().warning("Failed to deserialize location: " + locationData + ". Error: " + e.getMessage());
            return null;
        }
    }

    private JSONObject serializeLocation(Location location) {
        JSONObject locationData = new JSONObject();
        locationData.put("world", location.getWorld().getName()); // Nome do mundo deve ser salvo corretamente
        locationData.put("x", location.getX());
        locationData.put("y", location.getY());
        locationData.put("z", location.getZ());
        locationData.put("yaw", location.getYaw());
        locationData.put("pitch", location.getPitch());
        return locationData;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new FallDamageEvent(lobbyManager), this);
        Bukkit.getPluginManager().registerEvents(new BuildCommand(new ItemManager(getConfig()), checkpointManager, worldChecker, player), this);
        Bukkit.getPluginManager().registerEvents(new PlaceItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HungerControlListener(worldChecker), this);
        Bukkit.getPluginManager().registerEvents(new PressurePlateListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldChange(worldChecker), this);
    }

    private void registerCommands() {
        LobbyManager lobbyManager = new LobbyManager(getDataFolder());
        ItemManager itemManager = new ItemManager(getConfig());

        getCommand("parkour").setExecutor(new ParkourCommand(lobbyManager, worldChecker));
        getCommand("psetlobby").setExecutor(new LobbyCommand(lobbyManager));
        getCommand("jump").setExecutor(new JumpCommand(itemManager, worldChecker, player));
        getCommand("pbuild").setExecutor(new BuildCommand(itemManager, checkpointManager, worldChecker, player));
        getCommand("setcoord").setExecutor(new SetCoordCommand());
        getCommand("tpmap").setExecutor(new TpMapCommand(worldChecker));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        player = event.getPlayer();
    }
}
