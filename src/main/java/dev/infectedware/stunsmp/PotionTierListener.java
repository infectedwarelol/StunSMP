package dev.infectedware.stunsmp;

import java.util.List;
import java.util.Set;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

final class PotionTierListener implements Listener {

    private static final Set<EntityPotionEffectEvent.Cause> CAPPED_CAUSES = Set.of(
            EntityPotionEffectEvent.Cause.POTION_DRINK,
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD,
            EntityPotionEffectEvent.Cause.ARROW);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        BrewerInventory contents = event.getContents();
        List<ItemStack> results = event.getResults();
        for (int slot = 0; slot < results.size(); slot++) {
            if (hasAmplifiedEffect(results.get(slot))) {
                results.set(slot, contents.getItem(slot));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        if (hasAmplifiedEffect(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (CAPPED_CAUSES.contains(event.getCause()) && isAmplified(event.getNewEffect())) {
            event.setCancelled(true);
        }
    }

    private static boolean hasAmplifiedEffect(ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof PotionMeta meta)) {
            return false;
        }
        if (meta.hasBasePotionType() && isAnyAmplified(meta.getBasePotionType().getPotionEffects())) {
            return true;
        }
        return isAnyAmplified(meta.getCustomEffects());
    }

    private static boolean isAnyAmplified(List<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            if (isAmplified(effect)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAmplified(PotionEffect effect) {
        return effect != null && effect.getAmplifier() >= 1;
    }
}
