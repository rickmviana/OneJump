package org.hopef.parkour.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.hopef.parkour.utils.Constantes;
import org.hopef.parkour.utils.WorldChecker;

public class SetBlockComand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final WorldChecker checker;

    public SetBlockComand(JavaPlugin plugin, WorldChecker checker) {
        this.plugin = plugin;
        this.checker = checker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Constantes.COMMAND_SENDER.getText());
            return true;
        }

        Player player = (Player) sender;

        if (!checker.isInAllowedWorld(player)) {
            sender.sendMessage(Constantes.WORLD_ALLOWED.getText());
            return true;
        }

        // Verifica se o jogador está segurando um item na mão principal
        ItemStack itemInHand = player.getInventory().getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage("§cYou need to be holding a block to use this command!");
            return true;
        }

        Material blockType = itemInHand.getType();

        // Verifica se o item é um bloco
        if (!blockType.isBlock()) {
            player.sendMessage("§cThe item in your hand is not a valid block!");
            return true;
        }

        // Define o bloco na posição do jogador
        Bukkit.getScheduler().runTask(plugin, () -> {
            Block block = player.getLocation().getBlock();
            block.setType(blockType);

            // Configura a variação do bloco (se aplicável)
            byte data = itemInHand.getData().getData();
            block.setData(data);
        });

        return true;
    }
}
