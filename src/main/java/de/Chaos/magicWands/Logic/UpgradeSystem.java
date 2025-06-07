package de.Chaos.magicWands.Logic;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * System für das Upgraden von Zauberstäben mit Runen.
 */
public class UpgradeSystem {
    private static Plugin plugin;

    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }

    //  Keys für PersistentData
    private static NamespacedKey KEY_TYPE() {
        return new NamespacedKey(plugin, "rune_type");
    }

    private static NamespacedKey KEY_VALUE() {
        return new NamespacedKey(plugin, "rune_value");
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

        if (typeStr == null || value == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Rune-Daten!");
            return;
        }

        RuneType type = RuneType.valueOf(typeStr);

        // Neuen verbesserten Zauberstab erzeugen
        Wand upgradedWand = upgradeWand(wand, type, value);
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
    private static Wand upgradeWand(Wand wand, RuneType type, int value) {
        // Beispielhafte Erweiterung der Upgrade-Logik
        return new Wand(
                wand.getWandCore().upgrade(type, value),
                wand.getWandGrip(),
                wand.getWandFocus()
        );
    }

    /**
     * Enum für die verschiedenen Typen von Upgrade-Runen.
     * #todo muss noch impementiert werden
     */
    public enum RuneType {
        MANA(),
        ELEMENT(),
        SPEED(),
        CRIT();

        RuneType() {
        }


    }
}
