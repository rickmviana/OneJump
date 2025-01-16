package org.hopef.parkour.utils;

public enum Constantes {

    LOBBY_COMMAND("This command can only be used in the lobby"),
    CHECK_PRESSURE_PLATE_CHECKPOINT("§eThis pressure plate already has a checkpoint set."),
    COMMAND_SENDER("§7Only players can use this command."),
    WORLD_ALLOWED("§cThis command can only be used on permitted worlds."),
    PRESSURE_PLATE_INTERACT("§cYou need to interact with a pressure plate first!"),
    SET_CHECKPOINT_PRESSURE("§aCheckpoint set for this pressure plate."),
    TELEPORT_ITEM("§eTeleport item added to your inventory."),
    NO_ASSOCIATION("§cNo checkpoint associated with this pressure plate."),
    CHECKPOINT_NOT_FOUND("§cNo checkpoint found for this item."),
    REMOVE_CHECKPOINT("§cCheckpoint associated with this plate has been removed."),
    SAVE_LOCATION("§r§8[§r§6§lM§r§e§lV§r§8] §r§eSet designated coords with §r§6/spc §r§ecommand.§r"),
    LOBBY_NOT_DEFINED("§7The parkour lobby has not yet been defined."),
    TELEPORT_FOR_LOBBY("§7You have been teleported to the parkour lobby!"),
    NEW_CHECKPOINT("§r§8[§r§6§lM§r§e§lV§r§8] §r§7Set §r§echeckpoint!§r"),
    BACK_CHECKPOINT("§cReturn Checkpoint"),
    SET_NEW_CHECKPOINT("§aSet new checkpoint §8[§7Right-Click§8]"),
    BACK_TO_CHECKPOINT("§cBack to checkpoint §8[§7Right-Click§8]"),
    HIDE_PLAYERS("§aHide player §8[§7Right-Click§8]"),
    HIDE_LORE("§7Hide all players"),
    HIDE_MESSAGE("§r§8[§r§7§l!§r§8] §r§cHide §r§7all players.§r"),
    SHOW_PLAYERS("§7Show players §8[§7Right-Click§8]"),
    SHOW_LORE("§7Show all players"),
    SHOW_MESSAGE("§r§8[§r§2§l!§r§8] §r§aShowed §r§7all players.§r"),
    JUMP_MODE("§bJump Mode §8[§7Right-Click§8]"),
    JUMP_MODE_DISABLE("§r§8[§r§6§l!§r§8] §r§7Disabled §r§eJump Mode§r§7.§r"),
    JUMP_MODE_ENABLE("§r§8[§r§6§l!§r§8] §r§7Enabled §r§eJump Mode§r§7.§r"),
    FLY_ITEM_ENABLE("§aEnable Fly §8[§7Right-Click§8]"),
    FLY_ITEM_DISABLE("§7Disable Fly §8[§7Right-Click§8]"),
    FLY_MESSAGE_ENABLE("§r§8[§r§2§l!§r§8] §r§aEnabled fly§r§7.§r"),
    FLY_MESSAGE_DISABLE("§r§8[§r§7§l!§r§8] §r§7Disabled fly.§r"),
    FLY_ITEM_LORE_ENABLE("§7Enable fly with this item"),
    FLY_ITEM_LORE_DISABLE("§7Disable fly with this item"),
    SPECTATOR_MESSAGE_ENABLE("§r§8[§r§2§l!§r§8] §r§7Enabled §r§aSpectate Mode§r§7.§r"),
    SPECTATOR_MESSAGE_DISABLE("§r§8[§r§2§l!§r§8] §r§7Disabled §r§aSpectate Mode§r§7.§r"),
    SPECTATOR_ITEM_ENABLE("§aSpectate Mode §8[§7Right-Click§8]"),
    SPECTATOR_ITEM_DISABLE("§cDisable Spectate Mode §8[§7Right-Click§8]"),
    PRACTICE_MESSAGE_ENABLE("§r§8[§r§3§l!§r§8] §r§7Enabled §r§bPractice Mode§r§7.§r"),
    PRACTICE_MESSAGE_DISABLE("§r§8[§r§3§l!§r§8] §r§7Disabled §r§bPractice Mode.§r"),
    PRACTICE_ITEM_ENABLE("§bPractice Mode §8[§7Right-Click§8]"),
    PRACTICE_ITEM_DISABLE("§cDisable practice mode §8[§7Right-Click§8]"),
    PRACTICE_ITEM_CHECKPOINT("§eReturn to Checkpoint §8[§7Right-Click§8]");

    private final String TEXT;

    Constantes(String text) {
        this.TEXT = text;
    }

    public String getText() {
        return TEXT;
    }

}
