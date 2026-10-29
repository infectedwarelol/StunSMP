package dev.infectedware.stunsmp;

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

final class NetherListener implements Listener {

    private static final Set<PortalCreateEvent.CreateReason> NETHER_PORTAL_REASONS = Set.of(
            PortalCreateEvent.CreateReason.FIRE,
            PortalCreateEvent.CreateReason.NETHER_PAIR);

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalCreate(PortalCreateEvent event) {
        if (NETHER_PORTAL_REASONS.contains(event.getReason())) {
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
