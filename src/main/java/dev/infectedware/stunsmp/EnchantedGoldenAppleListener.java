package dev.infectedware.stunsmp;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

final class EnchantedGoldenAppleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        event.getLoot().removeIf(EnchantedGoldenAppleListener::isEnchantedGoldenApple);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        if (isEnchantedGoldenApple(inventory.getResult())) {
            inventory.setResult(null);
        }
    }

    private static boolean isEnchantedGoldenApple(ItemStack item) {
        return item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE;
    }
}
