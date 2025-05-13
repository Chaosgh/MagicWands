package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.ElementDamage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * System für das Upgraden von Zauberstäben mit Runen.
 */
public class UpgradeSystem {
    private static Plugin plugin;
    
    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }
    
    /**
     * Erstellt eine Upgrade-Rune für einen bestimmten Stat.
     *
     * @param type Der Typ der Rune (mana, element, speed, etc.)
     * @param value Der Upgrade-Wert
     * @param element Das Element (nur für Element-Runen)
     * @return Die erstellte Rune als ItemStack
     */
    public static ItemStack createUpgradeRune(RuneType type, int value, ElementDamage element) {
        ItemStack rune = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = rune.getItemMeta();
        if (meta == null) return rune;
        
        String displayName = ChatColor.LIGHT_PURPLE + "Upgrade-Rune: " + type.getDisplayName();
        if (type == RuneType.ELEMENT && element != null) {
            displayName += " " + element.name();
        }
        meta.setDisplayName(displayName);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Verbessert einen Zauberstab");
        lore.add(ChatColor.AQUA + "Bonus: +" + value + " " + type.getDisplayName());
        lore.add(ChatColor.YELLOW + "Rechtsklick auf einen Zauberstab zum Anwenden");
        meta.setLore(lore);
        
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "rune_type"), PersistentDataType.STRING, type.name());
        data.set(new NamespacedKey(plugin, "rune_value"), PersistentDataType.INTEGER, value);
        if (type == RuneType.ELEMENT && element != null) {
            data.set(new NamespacedKey(plugin, "rune_element"), PersistentDataType.STRING, element.name());
        }
        
        rune.setItemMeta(meta);
        return rune;
    }
    
    /**
     * Wendet eine Upgrade-Rune auf einen Zauberstab an.
     *
     * @param player Der Spieler, der das Upgrade durchführt
     * @param wandItem Der Zauberstab-ItemStack
     * @param runeItem Die Upgrade-Rune
     * @return true, wenn das Upgrade erfolgreich war
     */
    public static boolean applyUpgrade(Player player, ItemStack wandItem, ItemStack runeItem) {
        // Prüfen, ob es sich um einen Zauberstab handelt
        Wand wand = WandUtils.getWandFromItem(wandItem);
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Dies ist kein gültiger Zauberstab!");
            return false;
        }
        
        // Prüfen, ob es sich um eine Rune handelt
        if (!isUpgradeRune(runeItem)) {
            player.sendMessage(ChatColor.RED + "Dies ist keine gültige Upgrade-Rune!");
            return false;
        }
        
        // Rune auslesen
        ItemMeta runeMeta = runeItem.getItemMeta();
        PersistentDataContainer runeData = runeMeta.getPersistentDataContainer();
        String runeTypeStr = runeData.get(new NamespacedKey(plugin, "rune_type"), PersistentDataType.STRING);
        int runeValue = runeData.get(new NamespacedKey(plugin, "rune_value"), PersistentDataType.INTEGER);
        
        if (runeTypeStr == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Rune!");
            return false;
        }
        
        RuneType runeType = RuneType.valueOf(runeTypeStr);
        
        // Neuen Zauberstab mit verbesserten Stats erstellen
        Wand upgradedWand = upgradeWand(wand, runeType, runeValue, runeData);
        if (upgradedWand == null) {
            player.sendMessage(ChatColor.RED + "Upgrade fehlgeschlagen!");
            return false;
        }
        
        // Alten Zauberstab durch neuen ersetzen
        ItemStack upgradedItem = WandUtils.saveWandToItem(upgradedWand);
        wandItem.setItemMeta(upgradedItem.getItemMeta());
        
        // Rune verbrauchen (Menge um 1 reduzieren)
        if (runeItem.getAmount() > 1) {
            runeItem.setAmount(runeItem.getAmount() - 1);
        } else {
            player.getInventory().remove(runeItem);
        }
        
        player.sendMessage(ChatColor.GREEN + "Dein Zauberstab wurde erfolgreich verbessert!");
        return true;
    }
    
    /**
     * Prüft, ob ein ItemStack eine Upgrade-Rune ist.
     *
     * @param item Das zu prüfende Item
     * @return true, wenn es eine Rune ist
     */
    public static boolean isUpgradeRune(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        return data.has(new NamespacedKey(plugin, "rune_type"), PersistentDataType.STRING);
    }
    
    /**
     * Erstellt einen verbesserten Zauberstab basierend auf dem Runentyp.
     *
     * @param wand Der ursprüngliche Zauberstab
     * @param runeType Der Typ der Rune
     * @param runeValue Der Verbesserungswert
     * @param runeData Die Rune-Daten für zusätzliche Informationen
     * @return Der verbesserte Zauberstab
     */
    private static Wand upgradeWand(Wand wand, RuneType runeType, int runeValue, PersistentDataContainer runeData) {
        // Hier würde man normalerweise eine Kopie des Zauberstabs erstellen und verbessern
        // Da unsere Wand-Klasse aber unveränderlich ist, müssen wir einen neuen erstellen
        
        // Für jetzt erstellen wir einfach einen neuen Zauberstab mit den gleichen Komponenten
        // In einer vollständigen Implementierung würde man hier die Stats verbessern
        return new Wand(wand.getWandCore(), wand.getWandGrip(), wand.getWandFocus());
    }
    
    /**
     * Enum für die verschiedenen Typen von Upgrade-Runen.
     */
    public enum RuneType {
        MANA("Mana"),
        ELEMENT("Element"),
        SPEED("Geschwindigkeit"),
        CRIT("Kritisch");
        
        private final String displayName;
        
        RuneType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}