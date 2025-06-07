package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import de.Chaos.magicWands.MagicWands;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.Map;

public class WandUtils {
    private static final NamespacedKey WAND_ID_KEY = new NamespacedKey(MagicWands.getInstance(), "wand_id");
    private static final NamespacedKey WAND_CORE_KEY = new NamespacedKey(MagicWands.getInstance(), "wand_core");
    private static final NamespacedKey WAND_GRIP_KEY = new NamespacedKey(MagicWands.getInstance(), "wand_grip");
    private static final NamespacedKey WAND_FOCUS_KEY = new NamespacedKey(MagicWands.getInstance(), "wand_focus");
    private static final NamespacedKey WAND_SPELLS_KEY = new NamespacedKey(MagicWands.getInstance(), "wand_spells");


    public static Wand getWandFromItem(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        String wandIdStr = data.get(WAND_ID_KEY, PersistentDataType.STRING);
        String coreStr = data.get(WAND_CORE_KEY, PersistentDataType.STRING);
        String focusStr = data.get(WAND_FOCUS_KEY, PersistentDataType.STRING);
        String gripStr = data.get(WAND_GRIP_KEY, PersistentDataType.STRING);

        if (wandIdStr == null || coreStr == null || focusStr == null || gripStr == null) {
            return null;
        }

        UUID wandId = UUID.fromString(wandIdStr);

        try {
            WandCore core = WandCore.valueOf(coreStr);
            WandFocus focus = WandFocus.valueOf(focusStr);
            WandGrip grip = WandGrip.valueOf(gripStr);
            
            Wand wand = new Wand(core, grip, focus);
            
            // Setze die Wand-ID
            try {
                Field wandIdField = Wand.class.getDeclaredField("wandId");
                wandIdField.setAccessible(true);
                wandIdField.set(wand, wandId);
            } catch (Exception e) {
                return null;
            }

            // Setze die Mana-Werte
            double maxMana = core.getManaCapacity();
            double regenRate = core.getManaRegenRate();
            
            try {
                Field playerManaMapField = Wand.class.getDeclaredField("playerManaMap");
                Field wandMaxManaMapField = Wand.class.getDeclaredField("wandMaxManaMap");
                Field wandRegenRateMapField = Wand.class.getDeclaredField("wandRegenRateMap");
                
                playerManaMapField.setAccessible(true);
                wandMaxManaMapField.setAccessible(true);
                wandRegenRateMapField.setAccessible(true);
                
                @SuppressWarnings("unchecked")
                Map<UUID, Double> playerManaMap = (Map<UUID, Double>) playerManaMapField.get(null);
                @SuppressWarnings("unchecked")
                Map<UUID, Double> wandMaxManaMap = (Map<UUID, Double>) wandMaxManaMapField.get(null);
                @SuppressWarnings("unchecked")
                Map<UUID, Double> wandRegenRateMap = (Map<UUID, Double>) wandRegenRateMapField.get(null);
                
                // Setze die Mana-Werte in den Maps nur wenn sie noch nicht existieren
                if (!playerManaMap.containsKey(wandId)) {
                    playerManaMap.put(wandId, maxMana);
                }
                wandMaxManaMap.put(wandId, maxMana);
                wandRegenRateMap.put(wandId, regenRate);
            } catch (Exception e) {
                return null;
            }

            // Lade die Zauber
            String spellsStr = data.get(WAND_SPELLS_KEY, PersistentDataType.STRING);
            if (spellsStr != null && !spellsStr.isEmpty()) {
                String[] spellNames = spellsStr.split(",");
                for (String spellName : spellNames) {
                    try {
                        Spell spell = Spell.valueOf(spellName);
                        wand.addSpell(spell);
                    } catch (IllegalArgumentException e) {
                        // Ignoriere ungültige Zauber
                    }
                }
            }
            
            return wand;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ItemStack saveWandToItem(Wand wand) {
        return wand.toItemStack(MagicWands.getInstance());
    }



    public static ItemStack createSpellRune(Spell spell, int amount) {
        ItemStack rune = SpellRuneSystem.createSpellRune(spell);
        rune.setAmount(amount);
        return rune;
    }

}