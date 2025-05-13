package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import de.Chaos.magicWands.Enums.Spell;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wand {
    private static final String DISPLAY_NAME = "§5§lMagischer Zauberstab";
    private final WandCore wandCore;
    private final WandFocus wandFocus;
    private final WandGrip wandGrip;
    private int spellSlots;
    private final List<Spell> spells;
    private int currentMana;
    private final int maxMana;

    public Wand(WandCore core, WandGrip grip, WandFocus focus) {
        this.wandCore = core;
        this.wandGrip = grip;
        this.wandFocus = focus;
        this.spellSlots = 3;
        this.spells = new ArrayList<>();
        this.maxMana = core.getManaCapacity();
        this.currentMana = this.maxMana;
    }

    public ItemStack toItemStack(Plugin plugin) {
        ItemStack wandItem = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wandItem.getItemMeta();
        if (meta == null) return wandItem;

        meta.setDisplayName(DISPLAY_NAME);
        List<String> lore = new ArrayList<>();
        lore.add("§7Kern: §e" + wandCore.name());
        lore.add("§7Griff: §e" + wandGrip.name());
        lore.add("§7Fokus: §e" + wandFocus.name());
        lore.add("");
        lore.add("§bMana: §f" + currentMana + "/" + maxMana);
        lore.add("§bCast-Speed: §f" + wandGrip.getCastSpeed());
        lore.add("§bCrit-Chance: §f" + (int) (wandGrip.getCritChance() * 100) + "%");
        lore.add("§bElement: " + wandFocus.getElementDamage().name());
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "wand_core"), PersistentDataType.STRING, wandCore.name());
        data.set(new NamespacedKey(plugin, "wand_grip"), PersistentDataType.STRING, wandGrip.name());
        data.set(new NamespacedKey(plugin, "wand_focus"), PersistentDataType.STRING, wandFocus.name());
        data.set(new NamespacedKey(plugin, "wand_spellslots"), PersistentDataType.INTEGER, spellSlots);
        data.set(new NamespacedKey(plugin, "wand_mana"), PersistentDataType.INTEGER, currentMana);
        data.set(new NamespacedKey(plugin, "wand_maxmana"), PersistentDataType.INTEGER, maxMana);

        String spellList = spells.stream().map(Spell::name).collect(Collectors.joining(","));
        data.set(new NamespacedKey(plugin, "wand_spells"), PersistentDataType.STRING, spellList);

        wandItem.setItemMeta(meta);
        return wandItem;
    }

    public void setCurrentMana(int mana) {
        this.currentMana = Math.max(0, Math.min(maxMana, mana));
    }

    public WandCore getWandCore() { return wandCore; }
    public WandFocus getWandFocus() { return wandFocus; }
    public WandGrip getWandGrip() { return wandGrip; }
    public int getSpellSlots() { return spellSlots; }
    public List<Spell> getSpells() { return spells; }

    public void addSpell(Spell spell) {
        if (!spells.contains(spell) && spells.size() < spellSlots) {
            spells.add(spell);
        }
    }

    public void removeSpell(Spell spell) {
        spells.remove(spell);
    }

    public boolean isFull() {
        return spells.size() >= spellSlots;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public boolean hasEnoughMana(int cost) {
        return currentMana >= cost;
    }

    public void consumeMana(int cost) {
        currentMana = Math.max(0, currentMana - cost);
    }

}
