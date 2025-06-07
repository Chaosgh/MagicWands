package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.MagicWands;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * System für das Hinzufügen von Zaubersprüchen zu Zauberstäben mit Spell-Runen.
 */
public class SpellRuneSystem {
    private static Plugin plugin;
    
    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }
    
    /**
     * Erstellt eine Spell-Rune für einen bestimmten Zauberspruch.
     *
     * @param spell Der Zauberspruch, den die Rune enthält
     * @return Die erstellte Spell-Rune als ItemStack
     */
    public static ItemStack createSpellRune(Spell spell) {
        ItemStack rune = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = rune.getItemMeta();
        if (meta == null) return rune;
        
        String displayName = ChatColor.LIGHT_PURPLE + "Zauber-Rune: " + spell.getDisplayName();
        meta.setDisplayName(displayName);

        List<String> lore = getStrings(spell);
        meta.setLore(lore);
        
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "spell_rune"), PersistentDataType.BOOLEAN, true);
        data.set(new NamespacedKey(plugin, "spell_type"), PersistentDataType.STRING, spell.name());
        
        rune.setItemMeta(meta);
        return rune;
    }

    private static @NotNull List<String> getStrings(Spell spell) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Fügt einen Zauberspruch zu einem Zauberstab hinzu");
        lore.add(ChatColor.AQUA + "Zauber: " + spell.getDisplayName());
        lore.add(ChatColor.AQUA + "Element: " + spell.getElement().name());
        lore.add(ChatColor.AQUA + "Mana-Kosten: " + spell.getManaCost());
        lore.add(ChatColor.AQUA + "Cooldown: " + spell.getCooldownSeconds() + " Sekunden");
        lore.add(ChatColor.YELLOW + "Rechtsklick auf einen Zauberstab zum Anwenden");
        return lore;
    }

    /**
     * Wendet eine Spell-Rune auf einen Zauberstab an.
     *
     * @param player   Der Spieler, der die Rune anwendet
     * @param wandItem Der Zauberstab-ItemStack
     * @param runeItem Die Spell-Rune
     */
    public static void applySpellRune(Player player, ItemStack wandItem, ItemStack runeItem) {
        // Prüfen, ob es sich um einen Zauberstab handelt
        Wand wand = WandUtils.getWandFromItem(wandItem);
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Dies ist kein gültiger Zauberstab!");
            return;
        }
        
        // Prüfen, ob es sich um eine Spell-Rune handelt
        if (!isSpellRune(runeItem)) {
            player.sendMessage(ChatColor.RED + "Dies ist keine gültige Zauber-Rune!");
            return;
        }
        
        // Spell aus der Rune auslesen
        ItemMeta runeMeta = runeItem.getItemMeta();
        PersistentDataContainer runeData = runeMeta.getPersistentDataContainer();
        String spellTypeStr = runeData.get(new NamespacedKey(MagicWands.getInstance(), "spell_type"), PersistentDataType.STRING);
        
        if (spellTypeStr == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Zauber-Rune!");
            return;
        }
        
        try {
            Spell spell = Spell.valueOf(spellTypeStr);
            
            if (wand.isFull()) {
                player.sendMessage(ChatColor.RED + "Dieser Zauberstab hat bereits die maximale Anzahl an Zaubersprüchen!");
                return;
            }
            
            // Prüfen, ob der Zauberstab den Zauberspruch bereits hat
            if (wand.getSpells().contains(spell)) {
                player.sendMessage(ChatColor.RED + "Dieser Zauberstab hat diesen Zauberspruch bereits!");
                return;
            }

            wand.addSpell(spell);
            
            // Zauberstab aktualisieren
            ItemStack updatedWand = WandUtils.saveWandToItem(wand);
            player.getInventory().setItemInMainHand(updatedWand);
            
            // Rune verbrauchen
            runeItem.setAmount(runeItem.getAmount() - 1);
            
            player.sendMessage(ChatColor.GREEN + "Du hast deinem Zauberstab den Zauberspruch " + 
                    ChatColor.GOLD + spell.getDisplayName() + ChatColor.GREEN + " hinzugefügt!");

        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Ungültiger Zauberspruch in der Rune!");
        }
    }

    private static boolean isSpellRune(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        return data.has(new NamespacedKey(MagicWands.getInstance(), "spell_rune"), PersistentDataType.BOOLEAN) &&
                Boolean.TRUE.equals(data.get(new NamespacedKey(MagicWands.getInstance(), "spell_rune"), PersistentDataType.BOOLEAN));
    }
}