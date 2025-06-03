package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet die Registrierung und Ausführung von Zaubersprüchen.
 */
public class SpellRegistry {
    private static final Map<String, Long> playerCooldowns = new HashMap<>();
    private static final Map<UUID, Spell> activeSpells = new HashMap<>();
    private static Plugin pluginInstance;

    /**
     * Initialisiert das SpellRegistry mit der Plugin-Instanz.
     * MUSS in der onEnable() Methode des Hauptplugins aufgerufen werden!
     *
     * @param plugin Die Plugin-Instanz
     */
    public static void initialize(Plugin plugin) {
        pluginInstance = plugin;
        plugin.getLogger().info("SpellRegistry initialized with epic animations!");
    }

    /**
     * Setzt den aktiven Zauberspruch für einen Spieler.
     *
     * @param player Der Spieler
     * @param spell Der zu aktivierende Zauberspruch
     */
    public static void setActiveSpell(Player player, Spell spell) {
        activeSpells.put(player.getUniqueId(), spell);

        // Fancy spell selection message with element color
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
            String progressBar = createProgressBar(remainingSeconds, totalCooldown, 20);
            player.sendMessage("§7" + progressBar);
            return;
        }

        // Mana prüfen
        int currentMana = wand.getCurrentMana();
        int manaCost = spell.getManaCost();

        if (currentMana < manaCost) {
            player.sendMessage("§4🔷 §cNicht genug Mana!");
            player.sendMessage("§7Benötigt: §b" + manaCost + " §7| Verfügbar: §b" + currentMana);

            // Mana progress bar
            String manaBar = createProgressBar(currentMana, wand.getMaxMana(), 20);
            player.sendMessage("§7Mana: §b" + manaBar + " §f(" + currentMana + "/" + wand.getMaxMana() + ")");
            return;
        }

        // Pre-cast effects
        String elementColor = getElementColor(spell);
        player.sendMessage("§7═══════════════════════════");
        player.sendMessage("§f🔮 §aZauber wird gewirkt...");
        player.sendMessage("§f⚡ " + elementColor + spell.getDisplayName() + " §7wird entfesselt!");

        // Mana abziehen
        wand.setCurrentMana(currentMana - manaCost);

        // Mana usage display
        String newManaBar = createProgressBar(wand.getCurrentMana(), wand.getMaxMana(), 20);
        player.sendMessage("§f🔷 §7Mana: §b" + newManaBar + " §f(" + wand.getCurrentMana() + "/" + wand.getMaxMana() + ")");
        player.sendMessage("§7═══════════════════════════");

        try {
            // ZAUBER AUSFÜHREN - JETZT MIT PLUGIN!
            spell.cast(player, pluginInstance);

            // Success message after a short delay
            pluginInstance.getServer().getScheduler().runTaskLater(pluginInstance, () -> {
                player.sendMessage(elementColor + "✨ " + spell.getDisplayName() + " §awurde erfolgreich gewirkt!");
            }, 10L);

        } catch (Exception e) {
            player.sendMessage("§4✖ §cFehler beim Wirken des Zaubers!");
            pluginInstance.getLogger().severe("Error casting spell " + spell.name() + " for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();

            // Mana zurückgeben bei Fehler
            wand.setCurrentMana(currentMana);
            return;
        }

        // Cooldown setzen
        setCooldown(playerId, spell);

        // Cooldown notification
        player.sendMessage("§7⏰ Cooldown: §e" + spell.getCooldownSeconds() + " Sekunden");
    }

    /**
     * Gibt den aktiven Zauberspruch eines Spielers zurück.
     *
     * @param player Der Spieler
     * @return Der aktive Zauberspruch oder null
     */
    public static Spell getActiveSpell(Player player) {
        return activeSpells.get(player.getUniqueId());
    }

    /**
     * Entfernt den aktiven Zauberspruch eines Spielers.
     *
     * @param player Der Spieler
     */
    public static void clearActiveSpell(Player player) {
        Spell previousSpell = activeSpells.remove(player.getUniqueId());
        if (previousSpell != null) {
            player.sendMessage("§7🚫 Zauberspruch-Auswahl entfernt: §f" + previousSpell.getDisplayName());
        } else {
            player.sendMessage("§7Du hattest keinen Zauberspruch ausgewählt.");
        }
    }

    /**
     * Zeigt detaillierte Cooldown-Informationen für einen Spieler.
     *
     * @param player Der Spieler
     */
    public static void showCooldowns(Player player) {
        UUID playerId = player.getUniqueId();
        boolean hasCooldowns = false;

        player.sendMessage("§7▬▬▬▬▬▬▬ §6⏰ Cooldowns §7▬▬▬▬▬▬▬");

        for (Spell spell : Spell.values()) {
            if (isOnCooldown(playerId, spell)) {
                hasCooldowns = true;
                long remaining = getRemainingCooldown(playerId, spell);
                int remainingSeconds = (int) (remaining / 1000);
                String elementColor = getElementColor(spell);

                String progressBar = createProgressBar(
                        spell.getCooldownSeconds() - remainingSeconds,
                        spell.getCooldownSeconds(),
                        10
                );

                player.sendMessage(elementColor + spell.getDisplayName() + " §7: §e" +
                        remainingSeconds + "s §7" + progressBar);
            }
        }

        if (!hasCooldowns) {
            player.sendMessage("§a✓ Alle Zaubersprüche bereit!");
        }

        player.sendMessage("§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
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

    /**
     * Erstellt eine ASCII Progress Bar.
     */
    private static String createProgressBar(int current, int max, int length) {
        if (max <= 0) return "§7[" + "█".repeat(length) + "]";

        double percentage = (double) current / max;
        int filled = (int) (percentage * length);
        int empty = length - filled;

        String filledBar = "§a" + "█".repeat(Math.max(0, filled));
        String emptyBar = "§7" + "█".repeat(Math.max(0, empty));

        return "§7[" + filledBar + emptyBar + "§7]";
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
            default -> "§7";
        };
    }

    /**
     * Cleanup-Methode für Plugin-Shutdown.
     */
    public static void cleanup() {
        playerCooldowns.clear();
        activeSpells.clear();
        pluginInstance = null;
    }
}