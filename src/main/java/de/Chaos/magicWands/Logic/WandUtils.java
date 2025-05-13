package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class WandUtils {
    private static Plugin plugin;

    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }

    /**
     * Liest einen Zauberstab aus einem ItemStack aus.
     *
     * @param item Das Item, das ein Zauberstab sein könnte
     * @return Ein Wand-Objekt oder null, wenn das Item kein Zauberstab ist
     */
    public static Wand getWandFromItem(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer data = meta.getPersistentDataContainer();

        if (!hasWandTags(data)) return null;

        WandCore core = getEnumFromNBT(data, "wand_core", WandCore.class);
        WandGrip grip = getEnumFromNBT(data, "wand_grip", WandGrip.class);
        WandFocus focus = getEnumFromNBT(data, "wand_focus", WandFocus.class);

        if (core == null || grip == null || focus == null) return null;

        int currentMana = data.getOrDefault(new NamespacedKey(plugin, "wand_mana"), PersistentDataType.INTEGER, core.getManaCapacity());
        int maxMana = data.getOrDefault(new NamespacedKey(plugin, "wand_maxmana"), PersistentDataType.INTEGER, core.getManaCapacity());

        List<Spell> spells = new ArrayList<>();
        String spellList = data.get(new NamespacedKey(plugin, "wand_spells"), PersistentDataType.STRING);
        if (spellList != null && !spellList.isEmpty()) {
            for (String spellName : spellList.split(",")) {
                try {
                    spells.add(Spell.valueOf(spellName));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // Konstruktor mit allem
        Wand wand = new Wand(core, grip, focus);
        wand.getSpells().clear();
        wand.getSpells().addAll(spells);
        while (wand.getSpells().size() > wand.getSpellSlots()) {
            wand.getSpells().remove(wand.getSpells().size() - 1);
        }

        // Mana aktualisieren
        if (currentMana < 0) currentMana = 0;
        if (currentMana > maxMana) currentMana = maxMana;
        while (wand.getMaxMana() > maxMana) currentMana = maxMana;

        // Reflektiere geladenen Wert im Objekt
        try {
            var field = Wand.class.getDeclaredField("currentMana");
            field.setAccessible(true);
            field.setInt(wand, currentMana);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wand;
    }

    public static ItemStack saveWandToItem(Wand wand) {
        return wand.toItemStack(plugin);
    }

    private static boolean hasWandTags(PersistentDataContainer data) {
        return data.has(new NamespacedKey(plugin, "wand_core"), PersistentDataType.STRING) &&
                data.has(new NamespacedKey(plugin, "wand_grip"), PersistentDataType.STRING) &&
                data.has(new NamespacedKey(plugin, "wand_focus"), PersistentDataType.STRING);
    }

    private static <T extends Enum<T>> T getEnumFromNBT(PersistentDataContainer data, String key, Class<T> enumClass) {
        String value = data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
        if (value == null) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
