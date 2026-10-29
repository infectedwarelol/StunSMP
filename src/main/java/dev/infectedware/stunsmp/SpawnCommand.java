package dev.infectedware.stunsmp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class SpawnCommand implements CommandExecutor {

    private static final String SET_SPAWN_PERMISSION = "stunsmp.setspawn";

    private final SpawnManager spawnManager;
    private final Messages messages;

    SpawnCommand(SpawnManager spawnManager, Messages messages) {
        this.spawnManager = spawnManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        switch (command.getName()) {
            case "setspawn" -> setSpawn(sender);
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
}
