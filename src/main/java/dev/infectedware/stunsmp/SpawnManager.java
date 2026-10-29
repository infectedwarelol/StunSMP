package dev.infectedware.stunsmp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

final class SpawnManager {

    private SpawnPoint spawnPoint;

    boolean hasSpawn() {
        return spawnPoint != null;
    }

    Location getSpawnLocation() {
        return spawnPoint == null ? null : spawnPoint.toLocation();
    }

    void setSpawn(Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Spawn location has no world");
        }
        spawnPoint = new SpawnPoint(world.getName(), location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    boolean clearSpawn() {
        if (spawnPoint == null) {
            return false;
        }
        spawnPoint = null;
        return true;
    }

    private record SpawnPoint(String worldName, double x, double y, double z, float yaw, float pitch) {

        Location toLocation() {
            World world = Bukkit.getWorld(worldName);
            return world == null ? null : new Location(world, x, y, z, yaw, pitch);
        }
    }
}
