package dev.infectedware.stunsmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

final class SpawnManager {

    private static final String WORLD_KEY = "world";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String Z_KEY = "z";
    private static final String YAW_KEY = "yaw";
    private static final String PITCH_KEY = "pitch";

    private final Path spawnFile;
    private final Logger logger;
    private final ExecutorService fileWriter = Executors.newSingleThreadExecutor(
            runnable -> new Thread(runnable, "StunSMP-SpawnWriter"));
    private SpawnPoint spawnPoint;

    SpawnManager(Path dataFolder, Logger logger) {
        this.spawnFile = dataFolder.resolve("spawn.yml");
        this.logger = logger;
    }

    void load() {
        if (!Files.isRegularFile(spawnFile)) {
            return;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(spawnFile.toFile());
        String worldName = configuration.getString(WORLD_KEY);
        if (worldName == null || worldName.isBlank()) {
            logger.warning("Ignoring " + spawnFile + " because it has no world");
            return;
        }
        spawnPoint = new SpawnPoint(worldName,
                configuration.getDouble(X_KEY),
                configuration.getDouble(Y_KEY),
                configuration.getDouble(Z_KEY),
                (float) configuration.getDouble(YAW_KEY),
                (float) configuration.getDouble(PITCH_KEY));
    }

    void shutdown() {
        fileWriter.shutdown();
        try {
            if (!fileWriter.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warning("Timed out while saving " + spawnFile);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

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
        saveAsync(spawnPoint);
    }

    boolean clearSpawn() {
        if (spawnPoint == null) {
            return false;
        }
        spawnPoint = null;
        saveAsync(null);
        return true;
    }

    private void saveAsync(SpawnPoint snapshot) {
        String contents = snapshot == null ? null : serialize(snapshot);
        fileWriter.execute(() -> write(contents));
    }

    private static String serialize(SpawnPoint snapshot) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(WORLD_KEY, snapshot.worldName());
        configuration.set(X_KEY, snapshot.x());
        configuration.set(Y_KEY, snapshot.y());
        configuration.set(Z_KEY, snapshot.z());
        configuration.set(YAW_KEY, snapshot.yaw());
        configuration.set(PITCH_KEY, snapshot.pitch());
        return configuration.saveToString();
    }

    private void write(String contents) {
        try {
            if (contents == null) {
                Files.deleteIfExists(spawnFile);
                return;
            }
            Files.createDirectories(spawnFile.getParent());
            Path temporaryFile = spawnFile.resolveSibling(spawnFile.getFileName() + ".tmp");
            Files.writeString(temporaryFile, contents, StandardCharsets.UTF_8);
            Files.move(temporaryFile, spawnFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Could not save " + spawnFile, exception);
        }
    }

    private record SpawnPoint(String worldName, double x, double y, double z, float yaw, float pitch) {

        Location toLocation() {
            World world = Bukkit.getWorld(worldName);
            return world == null ? null : new Location(world, x, y, z, yaw, pitch);
        }
    }
}
