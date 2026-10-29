package dev.infectedware.stunsmp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

final class SpawnListener implements Listener {

    private final SpawnTeleportManager spawnTeleportManager;

    SpawnListener(SpawnTeleportManager spawnTeleportManager) {
        this.spawnTeleportManager = spawnTeleportManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            spawnTeleportManager.cancelCountdown(event.getPlayer(), "cancelled-move");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            spawnTeleportManager.cancelCountdown(player, "cancelled-damage");
        }
    }
}
