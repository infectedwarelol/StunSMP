package dev.infectedware.stunsmp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class SpawnCommand implements CommandExecutor {

    private static final String SET_SPAWN_PERMISSION = "stunsmp.setspawn";
    private static final String DELETE_SPAWN_PERMISSION = "stunsmp.delspawn";
    private static final String SPAWN_PERMISSION = "stunsmp.spawn";

    private final SpawnManager spawnManager;
    private final SpawnTeleportManager spawnTeleportManager;
    private final Messages messages;

    SpawnCommand(SpawnManager spawnManager, SpawnTeleportManager spawnTeleportManager, Messages messages) {
        this.spawnManager = spawnManager;
        this.spawnTeleportManager = spawnTeleportManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        switch (command.getName()) {
            case "setspawn" -> setSpawn(sender);
            case "delspawn" -> deleteSpawn(sender);
            case "spawn" -> spawn(sender);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void setSpawn(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return;
        }
        if (!player.hasPermission(SET_SPAWN_PERMISSION)) {
            messages.send(player, "no-permission");
            return;
        }
        spawnManager.setSpawn(player.getLocation());
        messages.send(player, "spawn-set");
    }

    private void deleteSpawn(CommandSender sender) {
        if (!sender.hasPermission(DELETE_SPAWN_PERMISSION)) {
            messages.send(sender, "no-permission");
            return;
        }
        messages.send(sender, spawnManager.clearSpawn() ? "spawn-deleted" : "no-spawn-to-delete");
    }

    private void spawn(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return;
        }
        if (!player.hasPermission(SPAWN_PERMISSION)) {
            messages.send(player, "no-permission");
            return;
        }
        spawnTeleportManager.requestTeleport(player);
    }
}
