package de.Chaos.magicWands.Listeners;

import de.Chaos.magicWands.Logic.SpellRuneSystem;
import de.Chaos.magicWands.MagicWands;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpellRuneListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        
        if (isSpellRune(heldItem) && offhandItem.getType() == Material.BLAZE_ROD) {
            event.setCancelled(true);
            SpellRuneSystem.applySpellRune(player, offhandItem, heldItem);
        }
        else if (heldItem.getType() == Material.BLAZE_ROD && isSpellRune(offhandItem)) {
            event.setCancelled(true);
            SpellRuneSystem.applySpellRune(player, heldItem, offhandItem);
        }
    }

    private boolean isSpellRune(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        return data.has(new NamespacedKey(MagicWands.getInstance(), "spell_rune"), PersistentDataType.BOOLEAN) &&
                Boolean.TRUE.equals(data.get(new NamespacedKey(MagicWands.getInstance(), "spell_rune"), PersistentDataType.BOOLEAN));
    }
}