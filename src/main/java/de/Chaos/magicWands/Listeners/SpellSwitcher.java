package de.Chaos.magicWands.Listeners;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.Logic.SpellRegistry;
import de.Chaos.magicWands.Logic.Wand;
import de.Chaos.magicWands.Logic.WandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SpellSwitcher implements Listener {
    private final Map<UUID, Integer> selectedSpellIndex = new HashMap<>();
    
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!event.isSneaking()) return;
        
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() != Material.BLAZE_ROD) return;
        
        Wand wand = WandUtils.getWandFromItem(heldItem);
        if (wand == null) return;
        
        List<Spell> spells = wand.getSpells();
        if (spells.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Dieser Zauberstab hat keine Zaubersprüche!");
            return;
        }
        
        UUID playerId = player.getUniqueId();
        int currentIndex = selectedSpellIndex.getOrDefault(playerId, 0);
        currentIndex = (currentIndex + 1) % spells.size();
        selectedSpellIndex.put(playerId, currentIndex);
        
        Spell selectedSpell = spells.get(currentIndex);
        SpellRegistry.setActiveSpell(player, selectedSpell);
        
        showSpellSelectionTitle(player, spells, currentIndex);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType() != Material.BLAZE_ROD) return;
        
        Wand wand = WandUtils.getWandFromItem(heldItem);
        if (wand == null) return;
        
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            event.setCancelled(true);
            SpellRegistry.castSpell(player, wand);
        }
    }
    
    private void showSpellSelectionTitle(Player player, List<Spell> spells, int selectedIndex) {
        StringBuilder title = new StringBuilder();
        
        for (int i = 0; i < spells.size(); i++) {
            if (i == selectedIndex) {
                title.append(ChatColor.GREEN).append("[")
                     .append(spells.get(i).getDisplayName())
                     .append("]").append(ChatColor.RESET);
            } else {
                title.append(ChatColor.GRAY)
                     .append(spells.get(i).getDisplayName())
                     .append(ChatColor.RESET);
            }
            
            if (i < spells.size() - 1) {
                title.append(" > ");
            }
        }
        
        player.sendTitle(ChatColor.GOLD + "Zaubersprüche", title.toString(), 10, 40, 10);
    }
}