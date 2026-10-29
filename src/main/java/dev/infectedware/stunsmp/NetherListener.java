package dev.infectedware.stunsmp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

final class NetherListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (isNetherDestination(event.getTo())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        if (isNetherDestination(event.getTo())) {
            event.setCancelled(true);
        }
    }

    private static boolean isNetherDestination(Location destination) {
        if (destination == null) {
            return false;
        }
        World world = destination.getWorld();
        return world != null && world.getEnvironment() == World.Environment.NETHER;
    }
}
