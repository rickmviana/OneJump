package org.hopef.parkour.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.PistonBaseMaterial;

/**
 * Handles player interactions with specific blocks such as pistons, cakes, and iron trapdoors.
 */
public class ItemInteractionListener implements Listener {

    private int pistonClickCount = 0; // Contador de cliques para alternar os estados do pistão

    /**
     * Handles player right-click interactions with blocks.
     *
     * @param event The {@link PlayerInteractEvent} triggered when the player interacts with a block.
     */
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Material blockType = clickedBlock.getType();

                // Handles Iron Trapdoor interactions
                if (blockType == Material.IRON_TRAPDOOR) {
                    toggleIronTrapdoor(clickedBlock);
                }
                // Handles Cake interactions
                else if (blockType == Material.CAKE_BLOCK) {
                    consumeCakeSlice(clickedBlock, event.getPlayer().getGameMode());
                }
                // Handles Piston interactions
                else if (blockType == Material.PISTON_BASE || blockType == Material.PISTON_EXTENSION) {
                    togglePistonState(clickedBlock, event);
                }
            }
        }
    }

    /**
     * Toggles the state of an Iron Trapdoor (open or closed).
     *
     * @param block The {@link Block} representing the Iron Trapdoor.
     */
    private void toggleIronTrapdoor(Block block) {
        byte data = block.getData();
        block.setData((byte) (data ^ 0x04)); // Toggle the open state using XOR
    }

    /**
     * Consumes a slice of a Cake block and resets it to full when completely consumed.
     *
     * @param block    The {@link Block} representing the Cake.
     * @param gameMode The {@link GameMode} of the player interacting with the block.
     */
    private void consumeCakeSlice(Block block, GameMode gameMode) {
        byte data = block.getData();

        if (data < 6) {
            block.setData((byte) (data + 1)); // Increment slice count
        } else {
            if (gameMode == GameMode.CREATIVE) {
                block.setData((byte) 0); // Reset to full cake in creative mode
            } else {
                block.setType(Material.AIR); // Remove the cake block
            }
        }

        block.getState().update(); // Ensure the block state updates visually
    }

    /**
     * Toggles the state of a Piston between three states using a switch:
     * 1. First click: Removes the head of the piston.
     * 2. Second click: Removes the body of the piston and sets the head.
     * 3. Third click: Restores the piston.
     *
     * @param block The {@link Block} representing the Piston.
     */
    private void togglePistonState(Block block, PlayerInteractEvent event) {
        BlockState state = block.getState();
        Material blockType = block.getType();

        if (state.getData() instanceof PistonBaseMaterial) {
            PistonBaseMaterial piston = (PistonBaseMaterial) state.getData();

            // Alterna os estados com base no contador de cliques
            pistonClickCount++;
            switch (pistonClickCount % 4) { // Divide os 3 estados usando o operador modulo
                case 0:
                    // Restaura o pistão completo
                    piston.setPowered(true); // Restaura a energia
                    break;
                case 1:
                    // Remove a cabeça do pistão
                    block.setType(Material.PISTON_EXTENSION);
                    piston.setPowered(true); // Desliga o pistão
                    break;
                case 2:
                    // Remove o corpo do pistão e coloca a cabeça
                    block.setType(Material.PISTON_BASE);
                    piston.setPowered(false);
                    break;
            }

            // Atualiza o estado do bloco
            state.setData(piston);
            state.update();
        }
    }

    /**
     * Verifica se o pistão está energizado
     *
     * @param block O bloco que representa o pistão.
     * @return true se o pistão estiver energizado, false caso contrário.
     */
    private boolean isPistonPowered(Block block) {
        if (block.getState().getData() instanceof PistonBaseMaterial) {
            PistonBaseMaterial piston = (PistonBaseMaterial) block.getState().getData();
            return piston.isPowered();
        }
        return false;
    }
}
