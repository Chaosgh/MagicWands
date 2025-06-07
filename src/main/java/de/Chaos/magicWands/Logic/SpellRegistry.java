package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;

import de.Chaos.magicWands.Listeners.ManaDisplayListener;
import org.bukkit.entity.Player;
import org.bukkit.boss.BossBar;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet die Registrierung und Ausführung von Zaubersprüchen.
 */
public class SpellRegistry {
    private static final Map<String, Long> playerCooldowns = new HashMap<>();
    private static final Map<UUID, Spell> activeSpells = new HashMap<>();
    private static Plugin pluginInstance;
    private static final Map<UUID, List<Spell>> playerSpells = new HashMap<>();
    private static final Map<UUID, Integer> playerSpellSlots = new HashMap<>();

    /**
     * Initialisiert das SpellRegistry mit der Plugin-Instanz.
     * MUSS in der onEnable() Methode des Hauptplugins aufgerufen werden sonst alles kapusch
     *
     * @param plugin Die Plugin-Instanz
     */
    public static void initialize(Plugin plugin) {
        pluginInstance = plugin;
        plugin.getLogger().info("SpellRegistry initialized with epic animations!");
    }

    /**
     * Setzt den aktiven Zauberspruch für einen Spieler. #todo für per wand nicht per spieler
     *
     * @param player Der Spieler
     * @param spell Der zu aktivierende Zauberspruch
     */
    public static void setActiveSpell(Player player, Spell spell) {


        activeSpells.put(player.getUniqueId(), spell);

        String elementColor = getElementColor(spell);
        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬ §6✦ §7▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§f⚡ §aZauber gewählt: " + elementColor + spell.getDisplayName());
        player.sendMessage("§f📊 §7Mana-Kosten: §b" + spell.getManaCost() + " §7| Cooldown: §e" + spell.getCooldownSeconds() + "s");
        player.sendMessage("§f🌟 §7Element: " + elementColor + spell.getElement().name());
        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬ §6✦ §7▬▬▬▬▬▬▬▬▬▬▬");
    }

    /**
     * Führt den aktiven Zauberspruch eines Spielers aus, wenn möglich.
     *
     * @param player Der Spieler
     * @param wand   Der verwendete Zauberstab
     */
    public static void castSpell(Player player, Wand wand) {
        if (pluginInstance == null) {
            player.sendMessage("§4✖ §cFehler: SpellRegistry nicht initialisiert!");
            return;
        }

        UUID playerId = player.getUniqueId();
        Spell spell = activeSpells.get(playerId);

        if (spell == null) {
            player.sendMessage("§4⚠ §cDu hast keinen Zauberspruch ausgewählt!");
            player.sendMessage("§7Verwende §f/spells §7um einen Zauber zu wählen.");
            return;
        }

        // Cooldown prüfen
        if (isOnCooldown(playerId, spell)) {
            long remainingCooldown = getRemainingCooldown(playerId, spell);
            player.sendMessage("§4⏰ §cZauber noch im Cooldown!");
            player.sendMessage("§7Verbleibende Zeit: §e" + (remainingCooldown / 1000) + " §7Sekunden");

            // Cooldown progress bar
            int totalCooldown = spell.getCooldownSeconds();
            int remainingSeconds = (int) (remainingCooldown / 1000);
            String progressBar = createProgressBar(remainingSeconds, totalCooldown);
            player.sendMessage("§7" + progressBar);
            return;
        }

        // Mana prüfen
        double currentMana = wand.getCurrentMana();
        double manaCost = spell.getManaCost();

        if (currentMana < manaCost) {
            player.sendMessage("§4🔷 §cNicht genug Mana!");
            player.sendMessage("§7Benötigt: §b" + manaCost + " §7| Verfügbar: §b" + currentMana);

            return;
        }

        String elementColor = getElementColor(spell);


        // Mana abziehen
        wand.setCurrentMana(currentMana - manaCost);



        // Aktualisiere die Manabar
        BossBar bossBar = ManaDisplayListener.getPlayerBossBar(player);
        if (bossBar != null) {
            ManaDisplayListener.updateManaBar(player, wand, bossBar);
        }

        try {
            spell.cast(player, pluginInstance);

            pluginInstance.getServer().getScheduler().runTaskLater(pluginInstance, () -> player.sendMessage(elementColor + "✨ " + spell.getDisplayName() + " §awurde erfolgreich gewirkt!"), 10L);

        } catch (Exception e) {
            player.sendMessage("§4✖ §cFehler beim Wirken des Zaubers!");
            pluginInstance.getLogger().severe("Error casting spell " + spell.name() + " for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();

            return;
        }

        // Cooldown setzen
        setCooldown(playerId, spell);

        wand.setManaRegenCooldown();

        player.sendMessage("§7⏰ Cooldown: §e" + spell.getCooldownSeconds() + " Sekunden");
    }

    private static boolean isOnCooldown(UUID playerId, Spell spell) {
        String key = playerId.toString() + ":" + spell.name();
        return playerCooldowns.containsKey(key) &&
                System.currentTimeMillis() < playerCooldowns.get(key);
    }

    private static long getRemainingCooldown(UUID playerId, Spell spell) {
        String key = playerId.toString() + ":" + spell.name();
        if (!playerCooldowns.containsKey(key)) return 0;

        long cooldownEnd = playerCooldowns.get(key);
        long now = System.currentTimeMillis();
        return Math.max(0, cooldownEnd - now);
    }

    private static void setCooldown(UUID playerId, Spell spell) {
        String key = playerId.toString() + ":" + spell.name();
        long cooldownEnd = System.currentTimeMillis() + (spell.getCooldownSeconds() * 1000L);
        playerCooldowns.put(key, cooldownEnd);
    }


    private static String createProgressBar(double current, double max) {
        double percentage = current / max;
        int filledLength = (int) (20 * percentage);
        int emptyLength = 20 - filledLength;


        return "§a" + // Grüne Farbe für gefüllten Teil
                "█".repeat(filledLength) +
                "§7" + // Graue Farbe für leeren Teil
                "█".repeat(emptyLength);
    }

    /**
     * Gibt die Farbe basierend auf dem Element zurück.
     */
    private static String getElementColor(Spell spell) {
        return switch (spell.getElement()) {
            case FIRE -> "§c";
            case WATER -> "§b";
            case EARTH -> "§6";
            case AIR -> "§f";
            case LIGHTNING -> "§e";
            case ARCANE -> "§d";
            case DARK -> "§8";
        };
    }

    /**
     * Cleanup-Methode für Plugin-Shutdown.
     */
    public static void cleanup() {
        playerCooldowns.clear();
        activeSpells.clear();
        playerSpells.clear();
        playerSpellSlots.clear();
        pluginInstance = null;
    }
}