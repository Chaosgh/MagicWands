package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.Spell;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet die Registrierung und Ausführung von Zaubersprüchen.
 */
public class SpellRegistry {
    private static final Map<String, Long> playerCooldowns = new HashMap<>();
    private static final Map<UUID, Spell> activeSpells = new HashMap<>();

    /**
     * Setzt den aktiven Zauberspruch für einen Spieler.
     *
     * @param player Der Spieler
     * @param spell Der zu aktivierende Zauberspruch
     */
    public static void setActiveSpell(Player player, Spell spell) {
        activeSpells.put(player.getUniqueId(), spell);
        player.sendMessage("§aDu hast " + spell.getDisplayName() + " §aausgewählt.");
    }

    /**
     * Führt den aktiven Zauberspruch eines Spielers aus, wenn möglich.
     *
     * @param player Der Spieler
     * @param wand   Der verwendete Zauberstab
     */
    public static void castSpell(Player player, Wand wand) {
        UUID playerId = player.getUniqueId();
        Spell spell = activeSpells.get(playerId);

        if (spell == null) {
            player.sendMessage("§cDu hast keinen Zauberspruch ausgewählt!");
            return;
        }

        // Cooldown prüfen
        if (isOnCooldown(playerId, spell)) {
            long remainingCooldown = getRemainingCooldown(playerId, spell);
            player.sendMessage("§cDu musst noch " + remainingCooldown / 1000 + " Sekunden warten!");
            return;
        }

        // Mana prüfen
        int currentMana = wand.getCurrentMana();
        int manaCost = spell.getManaCost();

        if (currentMana < manaCost) {
            player.sendMessage("§cNicht genug Mana! (" + currentMana + "/" + manaCost + ")");
            return;
        }

        // Mana abziehen
        wand.setCurrentMana(currentMana - manaCost);
        player.sendMessage("§bMana verwendet: " + manaCost + " §7(Verbleibend: " + wand.getCurrentMana() + ")");
        // Zauber ausführen - HIER WAR DER FEHLER!
        player.sendMessage("§aDu wirkst " + spell.getDisplayName() + "!");
        spell.cast(player);

        // Cooldown setzen
        setCooldown(playerId, spell);
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
        activeSpells.remove(player.getUniqueId());
        player.sendMessage("§7Zauberspruch-Auswahl entfernt.");
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
}