package dev.infectedware.stunsmp;

import java.util.Set;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

final class BannedItemListener implements Listener {

    private static final Set<Material> BANNED_MATERIALS = Set.of(Material.ENCHANTED_GOLDEN_APPLE, Material.MACE);
    private static final Set<Material> UNCRAFTABLE_MATERIALS = Set.of(
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_PICKAXE,
            Material.NETHERITE_AXE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_HOE,
            Material.END_CRYSTAL,
            Material.RESPAWN_ANCHOR);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        event.getLoot().removeIf(BannedItemListener::isBanned);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVaultDispense(BlockDispenseLootEvent event) {
        event.getDispensedLoot().removeIf(BannedItemListener::isBanned);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        if (isBanned(result) || isUncraftable(result)) {
            inventory.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrafterCraft(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (isBanned(result) || isUncraftable(result)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (isUncraftable(event.getResult())) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSmithItem(SmithItemEvent event) {
        if (isUncraftable(event.getInventory().getResult())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (isBanned(event.getItem())) {
            event.setCancelled(true);
        }
    }

    private static boolean isBanned(ItemStack item) {
        return item != null && BANNED_MATERIALS.contains(item.getType());
    }

    private static boolean isUncraftable(ItemStack item) {
        return item != null && UNCRAFTABLE_MATERIALS.contains(item.getType());
    }
}
