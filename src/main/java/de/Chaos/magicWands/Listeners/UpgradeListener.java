package de.Chaos.magicWands.Listeners;

import de.Chaos.magicWands.Logic.UpgradeSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Listener für das Upgrade-System, der Rechtsklicks mit Runen auf Zauberstäbe verarbeitet.
 */
public class UpgradeListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Nur Rechtsklicks verarbeiten
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        
        // Doppelte Events vermeiden (nur Haupthand verarbeiten)
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        
        // Prüfen, ob der Spieler eine Rune in der Haupthand und einen Zauberstab in der Nebenhand hält
        if (UpgradeSystem.isUpgradeRune(heldItem) && offhandItem.getType() == Material.BLAZE_ROD) {
            event.setCancelled(true);
            UpgradeSystem.applyUpgrade(player, offhandItem, heldItem);
        }
        // Oder umgekehrt: Zauberstab in der Haupthand und Rune in der Nebenhand
        else if (heldItem.getType() == Material.BLAZE_ROD && UpgradeSystem.isUpgradeRune(offhandItem)) {
            event.setCancelled(true);
            UpgradeSystem.applyUpgrade(player, heldItem, offhandItem);
        }
    }
}