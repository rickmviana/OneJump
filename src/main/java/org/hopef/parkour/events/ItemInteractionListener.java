package org.hopef.parkour.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles player interactions with specific blocks such as pistons, cakes, and iron trapdoors.
 */
public class ItemInteractionListener implements Listener {

    // Map to store the state of pistons
    private final Map<Block, Integer> pistonStates = new HashMap<>();

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
                else if (blockType == Material.PISTON_BASE || blockType == Material.PISTON_EXTENSION || blockType == Material.PISTON_STICKY_BASE) {
                    togglePistonState(clickedBlock);
                }
                // Handles Iron Door interactions
                else if (blockType == Material.IRON_DOOR) {
                    toggleIronDoor(clickedBlock);
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
     * Toggles the state of an Iron Door between open and closed.
     *
     * @param block The {@link Block} representing the Iron Door.
     */
    private void toggleIronDoor(Block block) {
        // Verifica se o bloco está sendo alimentado por um sinal de redstone
        boolean isPowered = block.isBlockPowered(); // Verifica se o bloco está alimentado

        // Configura a porta com base no estado de alimentação
        if (isPowered) {
            // A porta de ferro está fechada quando não há sinal de redstone
            block.setData((byte) 0); // Fecha a porta
        } else {
            // A porta de ferro está aberta quando há sinal de redstone
            block.setData((byte) 8); // Abre a porta
        }

        // Força a atualização do estado visual do bloco
        block.getState().update(true, true);
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
    private void togglePistonState(Block block) {
        // Obtém o estado atual do pistão
        int currentState = pistonStates.getOrDefault(block, 0);
        int nextState = (currentState + 1) % 3; // Próximo estado (0 -> 1 -> 2 -> 0)
        pistonStates.put(block, nextState);

        // Pega a orientação atual do pistão
        byte orientation = block.getData(); // Armazena a orientação do pistão

        // Define o estado do bloco baseado no estado
        switch (nextState) {
            case 0: // Posição inicial (pistão completo)
                block.setType(Material.PISTON_BASE);
                block.setData((byte) (orientation & 0x7));
                //block.setData(orientation); // Mantém a orientação original
                break;

            case 1: // Apenas corpo do pistão
                block.setType(Material.PISTON_BASE);
                block.setData((byte) (orientation | 0x8)); // Define como "powered"
                break;

            case 2: // Apenas cabeça (remover pistão)
                block.setType(Material.PISTON_EXTENSION);
                block.setData(orientation); // Mantém a orientação da cabeça
                break;
        }

        // Força a atualização do estado no mundo
        block.getState().update(true, true);
    }
}