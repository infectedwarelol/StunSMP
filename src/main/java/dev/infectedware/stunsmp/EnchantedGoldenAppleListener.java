package dev.infectedware.stunsmp;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

final class EnchantedGoldenAppleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        event.getLoot().removeIf(EnchantedGoldenAppleListener::isEnchantedGoldenApple);
    }

    private static boolean isEnchantedGoldenApple(ItemStack item) {
        return item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE;
    }
}
