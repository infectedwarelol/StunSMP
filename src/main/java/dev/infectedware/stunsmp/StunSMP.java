package dev.infectedware.stunsmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class StunSMP extends JavaPlugin {

    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        spawnManager = new SpawnManager(getDataFolder().toPath(), getLogger());
        spawnManager.load();
        Messages messages = new Messages(getConfig());
        SpawnTeleportManager spawnTeleportManager = new SpawnTeleportManager(this, spawnManager, messages,
                getConfig().getInt("countdown-seconds", 5));
        SpawnCommand spawnCommand = new SpawnCommand(spawnManager, spawnTeleportManager, messages);
        bindCommand("setspawn", spawnCommand);
        bindCommand("delspawn", spawnCommand);
        bindCommand("spawn", spawnCommand);
        registerShapedRecipe("golden_apple", Material.GOLDEN_APPLE,
                Map.of('G', Material.GOLD_INGOT, 'A', Material.APPLE),
                " G ", "GAG", " G ");
        registerShapedRecipe("cobweb", Material.COBWEB,
                Map.of('S', Material.STRING),
                "SSS", "SSS", "SSS");
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BannedItemListener(), this);
        pluginManager.registerEvents(new PotionTierListener(), this);
        pluginManager.registerEvents(new NetherListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        registeredRecipes.forEach(key -> Bukkit.removeRecipe(key, true));
        registeredRecipes.clear();
        if (spawnManager != null) {
            spawnManager.shutdown();
            spawnManager = null;
        }
    }

    private void bindCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            throw new IllegalStateException("Command /" + name + " is missing from plugin.yml");
        }
        command.setExecutor(executor);
    }

    private void registerShapedRecipe(String name, Material result, Map<Character, Material> ingredients, String... shape) {
        NamespacedKey key = new NamespacedKey(this, name);
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(result));
        recipe.shape(shape);
        ingredients.forEach(recipe::setIngredient);
        if (Bukkit.addRecipe(recipe, true)) {
            registeredRecipes.add(key);
        }
    }
}
