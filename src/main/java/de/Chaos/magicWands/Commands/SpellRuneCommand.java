package de.Chaos.magicWands.Commands;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.Logic.SpellRuneSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command zum Erstellen von Spell-Runen für Administratoren.
 */
public class SpellRuneCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cVerwendung: /givespellrune <zauber>");
            return true;
        }

        try {
            Spell spell = Spell.valueOf(args[0].toUpperCase());
            ItemStack spellRune = SpellRuneSystem.createSpellRune(spell);
            player.getInventory().addItem(spellRune);
            player.sendMessage("§aDu hast eine Zauber-Rune für " + spell.getDisplayName() + " §aerhalten!");
            return true;
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUngültiger Zauberspruch! Verfügbare Zaubersprüche: " + 
                    Arrays.stream(Spell.values())
                          .map(Enum::name)
                          .collect(Collectors.joining(", ")));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Arrays.stream(Spell.values())
                    .map(Enum::name)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}