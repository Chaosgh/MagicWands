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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;

public class Wand {
    private static final String DISPLAY_NAME = "§5§lMagischer Zauberstab";
    private static final Map<UUID, Double> playerManaMap = new HashMap<>();
    private static final Map<UUID, Double> wandMaxManaMap = new HashMap<>();
    private static final Map<UUID, Double> wandRegenRateMap = new HashMap<>();
    private static final Map<UUID, Long> manaRegenCooldowns = new HashMap<>();
    private static final int MANA_REGEN_INTERVAL = 20; // 1 Sekunde (20 Ticks)
    private static final double REGEN_RATE_MULTIPLIER = 1.0; // Regenerationsrate normalisiert
    private static final long MANA_REGEN_COOLDOWN = 2000; // 2 Sekunden Cooldown nach Zaubern
    private final UUID wandId;
    private final WandCore core;
    private final WandFocus focus;
    private final WandGrip grip;
    private final int spellSlots;
    private final List<Spell> spells;
    private final double maxMana;
    private final double regenRate;

    public Wand(WandCore core, WandGrip grip, WandFocus focus) {
        this.wandId = UUID.randomUUID();
        this.core = core;
        this.grip = grip;
        this.focus = focus;
        this.spellSlots = 3;
        this.spells = new ArrayList<>();
        this.maxMana = core.getManaCapacity();
        this.regenRate = core.getManaRegenRate();
        
        // Initialisiere Mana-Werte
        playerManaMap.put(this.wandId, this.maxMana);
        wandMaxManaMap.put(this.wandId, this.maxMana);
        wandRegenRateMap.put(this.wandId, this.regenRate);
    }

    /**
     * Initialisiert die Mana-Regeneration für alle Zauberstäbe.
     * Diese Methode muss in der onEnable() des Plugins aufgerufen werden.
     */
    public static void initializeManaRegeneration(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Double> entry : playerManaMap.entrySet()) {
                    UUID wandId = entry.getKey();
                    double currentMana = entry.getValue();
                    double regenRate = wandRegenRateMap.getOrDefault(wandId, 1.0) * REGEN_RATE_MULTIPLIER; // Beibehaltung des Multiplikators hier, falls er später für spezifische Effekte benötigt wird, aber der Standardwert ist jetzt 1.0
                    double maxMana = getMaxManaForWand(wandId);
                    
                    // Prüfe ob der Zauberstab im Mana-Regenerations-Cooldown ist
                    if (manaRegenCooldowns.containsKey(wandId) && 
                        System.currentTimeMillis() < manaRegenCooldowns.get(wandId)) {
                        continue;
                    }
                    
                    // Mana nur regenerieren, wenn es nicht voll ist
                    if (currentMana < maxMana) {
                        double newMana = Math.min(maxMana, currentMana + regenRate);
                        playerManaMap.put(wandId, newMana);
                    }
                }
            }
        }.runTaskTimer(plugin, MANA_REGEN_INTERVAL, MANA_REGEN_INTERVAL);
    }

    /**
     * Gibt das maximale Mana für eine bestimmte Wand-ID zurück.
     */
    public static double getMaxManaForWand(UUID wandId) {
        return wandMaxManaMap.getOrDefault(wandId, 100.0);
    }

    public ItemStack toItemStack(Plugin plugin) {
        ItemStack wandItem = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wandItem.getItemMeta();
        if (meta == null) return wandItem;

        meta.setDisplayName(DISPLAY_NAME);
        List<String> lore = getStrings();
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "wand_core"), PersistentDataType.STRING, core.name());
        data.set(new NamespacedKey(plugin, "wand_grip"), PersistentDataType.STRING, grip.name());
        data.set(new NamespacedKey(plugin, "wand_focus"), PersistentDataType.STRING, focus.name());
        data.set(new NamespacedKey(plugin, "wand_spellslots"), PersistentDataType.INTEGER, spellSlots);
        data.set(new NamespacedKey(plugin, "wand_maxmana"), PersistentDataType.DOUBLE, maxMana);
        data.set(new NamespacedKey(plugin, "wand_id"), PersistentDataType.STRING, wandId.toString());

        String spellList = spells.stream().map(Spell::name).collect(Collectors.joining(","));
        data.set(new NamespacedKey(plugin, "wand_spells"), PersistentDataType.STRING, spellList);

        wandItem.setItemMeta(meta);
        return wandItem;
    }

    private @NotNull List<String> getStrings() {
        List<String> lore = new ArrayList<>();
        lore.add("§7Kern: §e" + core.name());
        lore.add("§7Griff: §e" + grip.name());
        lore.add("§7Fokus: §e" + focus.name());
        lore.add("");
        lore.add("§bMana: §f" + String.format("%.1f", getCurrentMana()) + "/" + String.format("%.1f", maxMana));
        lore.add("§bMana-Regeneration: §f" + String.format("%.1f", regenRate) + " pro Sekunde");
        lore.add("§bCast-Speed: §f" + grip.getCastSpeed());
        lore.add("§bCrit-Chance: §f" + (int) (grip.getCritChance() * 100) + "%");
        lore.add("§bElement: " + focus.getElementDamage().name());
        return lore;
    }

    public void setCurrentMana(double mana) {
        double maxMana = getMaxManaForWand(wandId);
        playerManaMap.put(wandId, Math.min(maxMana, Math.max(0, mana)));
        // Der Mana-Regenerations-Cooldown wird jetzt in SpellRegistry.castSpell() gesetzt.
    }

    public WandCore getWandCore() { return core; }
    public WandFocus getWandFocus() { return focus; }
    public WandGrip getWandGrip() { return grip; }
    public int getSpellSlots() { return spellSlots; }
    public List<Spell> getSpells() { return spells; }

    public void addSpell(Spell spell) {
        if (!spells.contains(spell) && spells.size() < spellSlots) {
            spells.add(spell);
        }
    }



    public boolean isFull() {
        return spells.size() >= spellSlots;
    }

    public double getCurrentMana() {
        return playerManaMap.getOrDefault(wandId, 0.0);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setManaRegenCooldown() {
        manaRegenCooldowns.put(this.wandId, System.currentTimeMillis() + MANA_REGEN_COOLDOWN);
    }





}
