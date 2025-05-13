package de.Chaos.magicWands.Commands;

import de.Chaos.magicWands.Enums.ElementDamage;
import de.Chaos.magicWands.Logic.UpgradeSystem;
import de.Chaos.magicWands.Logic.UpgradeSystem.RuneType;
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


public class RuneCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cVerwendung: /giverune <typ> <wert> [element]");
            return true;
        }

        try {
            RuneType runeType = RuneType.valueOf(args[0].toUpperCase());
            int value = Integer.parseInt(args[1]);
            
            ElementDamage element = null;
            if (runeType == RuneType.ELEMENT && args.length > 2) {
                try {
                    element = ElementDamage.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cUngültiges Element! Verfügbare Elemente: " + 
                            Arrays.stream(ElementDamage.values())
                                  .map(Enum::name)
                                  .collect(Collectors.joining(", ")));
                    return true;
                }
            }
            
            ItemStack rune = UpgradeSystem.createUpgradeRune(runeType, value, element);
            player.getInventory().addItem(rune);
            player.sendMessage("§aRune erstellt und in dein Inventar gelegt!");
            
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUngültiger Runentyp! Verfügbare Typen: " + 
                    Arrays.stream(RuneType.values())
                          .map(Enum::name)
                          .collect(Collectors.joining(", ")));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterStartingWith(Arrays.stream(RuneType.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()), args[0]);
        } else if (args.length == 2) {
            return List.of("5", "10", "20", "50");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("ELEMENT")) {
            return filterStartingWith(Arrays.stream(ElementDamage.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()), args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> filterStartingWith(List<String> list, String prefix) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}