package dev.infectedware.stunsmp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

final class SpawnTeleportManager {

    private static final String BYPASS_PERMISSION = "stunsmp.spawn.bypass";
    private static final long TICKS_PER_SECOND = 20L;

    private final JavaPlugin plugin;
    private final SpawnManager spawnManager;
    private final Messages messages;
    private final int countdownSeconds;
    private final Map<UUID, BukkitTask> countdowns = new HashMap<>();

    SpawnTeleportManager(JavaPlugin plugin, SpawnManager spawnManager, Messages messages, int countdownSeconds) {
        this.plugin = plugin;
        this.spawnManager = spawnManager;
        this.messages = messages;
        this.countdownSeconds = Math.max(0, countdownSeconds);
    }

    void requestTeleport(Player player) {
        if (resolveSpawn(player) == null) {
            return;
        }
        UUID playerId = player.getUniqueId();
        if (countdowns.containsKey(playerId)) {
            messages.send(player, "already-teleporting");
            return;
        }
        if (countdownSeconds == 0 || player.hasPermission(BYPASS_PERMISSION)) {
            teleport(player);
            return;
        }
        countdowns.put(playerId, new Countdown(player).runTaskTimer(plugin, 0L, TICKS_PER_SECOND));
    }

    private Location resolveSpawn(Player player) {
        if (!spawnManager.hasSpawn()) {
            messages.send(player, "no-spawn");
            return null;
        }
        Location spawn = spawnManager.getSpawnLocation();
        if (spawn == null) {
            messages.send(player, "world-not-loaded");
        }
        return spawn;
    }

    private void teleport(Player player) {
        Location spawn = resolveSpawn(player);
        if (spawn == null) {
            return;
        }
        player.teleportAsync(spawn).thenAccept(teleported -> {
            if (!player.isOnline()) {
                return;
            }
            String key = Boolean.TRUE.equals(teleported) ? "teleported" : "teleport-failed";
            messages.send(player, key);
            messages.sendActionBar(player, key);
        });
    }

    private final class Countdown extends BukkitRunnable {

        private final Player player;
        private int remainingSeconds = countdownSeconds;

        private Countdown(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (remainingSeconds <= 0) {
                cancel();
                countdowns.remove(player.getUniqueId());
                teleport(player);
                return;
            }
            String seconds = Integer.toString(remainingSeconds);
            messages.send(player, "countdown", "{seconds}", seconds);
            messages.sendActionBar(player, "countdown", "{seconds}", seconds);
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
            remainingSeconds--;
        }
    }
}
