package de.Chaos.magicWands.Commands;

import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import de.Chaos.magicWands.Logic.Wand;
import de.Chaos.magicWands.Logic.WandBuilder;
import de.Chaos.magicWands.Logic.WandUtils;
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

public class MagicWandsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "wandinfo" -> {
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                Wand wand = WandUtils.getWandFromItem(heldItem);
                if (wand == null) {
                    player.sendMessage("§cDu musst einen Zauberstab in der Hand halten!");
                    return true;
                }
                showWandInfo(player, wand);
            }
            case "givewand" -> {
                if (args.length < 4) {
                    player.sendMessage("§cVerwendung: /mw givewand <core> <grip> <focus>");
                    return true;
                }
                createWand(player, args[1], args[2], args[3]);
            }
            default -> sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== MagicWands Befehle ===");
        player.sendMessage("§e/mw wandinfo §7- Zeigt Informationen über den Zauberstab in deiner Hand");
        player.sendMessage("§e/mw givewand <core> <grip> <focus> §7- Erstellt einen Zauberstab mit den angegebenen Komponenten");
    }

    private void showWandInfo(Player player, Wand wand) {
        player.sendMessage("§6=== Zauberstab-Informationen ===");
        player.sendMessage("§7Kern: §e" + wand.getWandCore().name());
        player.sendMessage("§7Griff: §e" + wand.getWandGrip().name());
        player.sendMessage("§7Fokus: §e" + wand.getWandFocus().name());
        player.sendMessage("§7Mana: §b" + wand.getWandCore().getManaCapacity());
        player.sendMessage("§7Cast-Speed: §b" + wand.getWandGrip().getCastSpeed());
        player.sendMessage("§7Crit-Chance: §b" + (int)(wand.getWandGrip().getCritChance() * 100) + "%");
        player.sendMessage("§7Element: §b" + wand.getWandFocus().getElementDamage().name());
        player.sendMessage("§7Spell-Slots: §b" + wand.getSpellSlots());
        player.sendMessage("§7Zaubersprüche: §b" + (wand.getSpells().isEmpty() ? "Keine" : 
                wand.getSpells().stream().map(spell -> spell.getDisplayName()).collect(Collectors.joining(", "))));
    }

    private void createWand(Player player, String coreStr, String gripStr, String focusStr) {
        try {
            WandCore core = WandCore.valueOf(coreStr.toUpperCase());
            WandGrip grip = WandGrip.valueOf(gripStr.toUpperCase());
            WandFocus focus = WandFocus.valueOf(focusStr.toUpperCase());

            Wand wand = WandBuilder.buildWand(core, grip, focus);
            ItemStack wandItem = WandUtils.saveWandToItem(wand);
            player.getInventory().addItem(wandItem);
            player.sendMessage("§aZauberstab erstellt und in dein Inventar gelegt!");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUngültige Komponente! Überprüfe deine Eingabe.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterStartingWith(Arrays.asList("wandinfo", "givewand"), args[0]);
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("givewand")) {
            if (args.length == 2) {
                return filterStartingWith(Arrays.stream(WandCore.values())
                        .map(Enum::name)
                        .collect(Collectors.toList()), args[1]);
            } else if (args.length == 3) {
                return filterStartingWith(Arrays.stream(WandGrip.values())
                        .map(Enum::name)
                        .collect(Collectors.toList()), args[2]);
            } else if (args.length == 4) {
                return filterStartingWith(Arrays.stream(WandFocus.values())
                        .map(Enum::name)
                        .collect(Collectors.toList()), args[3]);
            }
        }
        return new ArrayList<>();
    }

    private List<String> filterStartingWith(List<String> list, String prefix) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}