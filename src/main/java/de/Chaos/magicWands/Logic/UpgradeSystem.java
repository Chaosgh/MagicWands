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

    // Zentrale Keys für PersistentData
    private static NamespacedKey KEY_TYPE() {
        return new NamespacedKey(plugin, "rune_type");
    }

    private static NamespacedKey KEY_VALUE() {
        return new NamespacedKey(plugin, "rune_value");
    }

    private static NamespacedKey KEY_ELEMENT() {
        return new NamespacedKey(plugin, "rune_element");
    }

    /**
     * Erstellt eine Upgrade-Rune für einen bestimmten Stat.
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
        data.set(KEY_TYPE(), PersistentDataType.STRING, type.name());
        data.set(KEY_VALUE(), PersistentDataType.INTEGER, value);
        if (type == RuneType.ELEMENT && element != null) {
            data.set(KEY_ELEMENT(), PersistentDataType.STRING, element.name());
        }

        rune.setItemMeta(meta);
        return rune;
    }

    /**
     * Wendet eine Upgrade-Rune auf einen Zauberstab an.
     */
    public static void applyUpgrade(Player player, ItemStack wandItem, ItemStack runeItem) {
        // Prüfen, ob es sich um einen Zauberstab handelt
        Wand wand = WandUtils.getWandFromItem(wandItem);
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Dies ist kein gültiger Zauberstab!");
            return;
        }

        // Prüfen, ob es sich um eine Rune handelt
        if (!isUpgradeRune(runeItem)) {
            player.sendMessage(ChatColor.RED + "Dies ist keine gültige Upgrade-Rune!");
            return;
        }

        ItemMeta runeMeta = runeItem.getItemMeta();
        if (runeMeta == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Rune (keine Metadaten)!");
            return;
        }

        PersistentDataContainer data = runeMeta.getPersistentDataContainer();

        String typeStr = data.get(KEY_TYPE(), PersistentDataType.STRING);
        Integer value = data.get(KEY_VALUE(), PersistentDataType.INTEGER);
        String elementStr = data.get(KEY_ELEMENT(), PersistentDataType.STRING);

        if (typeStr == null || value == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Rune-Daten!");
            return;
        }

        RuneType type = RuneType.valueOf(typeStr);
        ElementDamage element = (elementStr != null) ? ElementDamage.valueOf(elementStr) : null;

        // Neuen verbesserten Zauberstab erzeugen
        Wand upgradedWand = upgradeWand(wand, type, value, element);
        ItemStack upgradedItem = WandUtils.saveWandToItem(upgradedWand);

        // Item im Inventar ersetzen
        int slot = player.getInventory().first(wandItem);
        if (slot != -1) {
            player.getInventory().setItem(slot, upgradedItem);
        } else {
            player.sendMessage(ChatColor.RED + "Zauberstab konnte nicht ersetzt werden.");
            return;
        }

        // Rune verbrauchen
        if (runeItem.getAmount() > 1) {
            runeItem.setAmount(runeItem.getAmount() - 1);
        } else {
            player.getInventory().removeItem(runeItem);
        }

        player.sendMessage(ChatColor.GREEN + "Dein Zauberstab wurde erfolgreich verbessert!");
    }

    /**
     * Prüft, ob ein ItemStack eine Upgrade-Rune ist.
     */
    public static boolean isUpgradeRune(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(KEY_TYPE(), PersistentDataType.STRING);
    }

    /**
     * Erstellt einen verbesserten Zauberstab basierend auf dem Runentyp.
     */
    private static Wand upgradeWand(Wand wand, RuneType type, int value, ElementDamage element) {
        // Beispielhafte Erweiterung der Upgrade-Logik
        Wand upgraded = new Wand(
                wand.getWandCore().upgrade(type, value, element),
                wand.getWandGrip(),
                wand.getWandFocus()
        );
        return upgraded;
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
