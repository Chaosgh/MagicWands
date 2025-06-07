package de.Chaos.magicWands.Commands;

import de.Chaos.magicWands.Enums.Spell;
import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import de.Chaos.magicWands.Logic.Wand;
import de.Chaos.magicWands.Logic.WandBuilder;
import de.Chaos.magicWands.Logic.WandUtils;
import de.Chaos.magicWands.MagicWands;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MagicWandsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
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
            case "spells" -> {
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                Wand wand = WandUtils.getWandFromItem(heldItem);
                if (wand == null) {
                    player.sendMessage("§cDu musst einen Zauberstab in der Hand halten!");
                    return true;
                }
                showSpells(player, wand);
            }

            case "givespellrune" -> {
                if (!player.hasPermission("magicwands.admin")) {
                    player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cVerwendung: /mw givespellrune <spell> <anzahl>");
                    return true;
                }
                try {
                    Spell spell = Spell.valueOf(args[1].toUpperCase());
                    int amount = Integer.parseInt(args[2]);
                    ItemStack spellRune = WandUtils.createSpellRune(spell, amount);
                    player.getInventory().addItem(spellRune);
                    player.sendMessage("§aDu hast " + amount + " " + spell.getDisplayName() + " Runen erhalten!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cUngültiger Zauberspruch oder Anzahl!");
                }
            }
            case "givecore" -> {
                if (args.length < 2) {
                    player.sendMessage("§cVerwendung: /mw givecore <core>");
                    return true;
                }
                try {
                    WandCore core = WandCore.valueOf(args[1].toUpperCase());
                    ItemStack coreItem = createCoreItem(core);
                    player.getInventory().addItem(coreItem);
                    player.sendMessage("§aDu hast einen " + core.name() + " Kern erhalten!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cUngültiger Kern! Verfügbare Kerne: OAK, BIRCH, BLAZE_ROD, ENDER_ROD, NETHER_STAR");
                }
            }
            case "givegrip" -> {
                if (args.length < 2) {
                    player.sendMessage("§cVerwendung: /mw givegrip <grip>");
                    return true;
                }
                try {
                    WandGrip grip = WandGrip.valueOf(args[1].toUpperCase());
                    ItemStack gripItem = createGripItem(grip);
                    player.getInventory().addItem(gripItem);
                    player.sendMessage("§aDu hast einen " + grip.name() + " Griff erhalten!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cUngültiger Griff! Verfügbare Griffe: SIMPLE, FINE_LEATHER, DRAGON_HIDE, VOID_SILK");
                }
            }
            case "givefocus" -> {
                if (args.length < 2) {
                    player.sendMessage("§cVerwendung: /mw givefocus <focus>");
                    return true;
                }
                try {
                    WandFocus focus = WandFocus.valueOf(args[1].toUpperCase());
                    ItemStack focusItem = createFocusItem(focus);
                    player.getInventory().addItem(focusItem);
                    player.sendMessage("§aDu hast einen " + focus.name() + " Fokus erhalten!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cUngültiger Fokus! Verfügbare Fokusse: FIRE, ICE, EARTH, WIND, ARCANE, LIGHTNING, DARKNESS");
                }
            }
            case "reload" -> {
                if (!player.hasPermission("magicwands.admin")) {
                    player.sendMessage("§cDu hast keine Berechtigung für diesen Befehl!");
                    return true;
                }
                // TODO: Implementiere Reload-Logik
                player.sendMessage("§aPlugin wurde neu geladen!");
            }
            default -> sendHelp(player);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("wandinfo", "givewand", "spells", "giverune", "givespellrune", "givecore", "givegrip", "givefocus", "reload");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("givewand")) {
                return Arrays.stream(WandCore.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("givespellrune")) {
                return Arrays.stream(Spell.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("givecore")) {
                return Arrays.stream(WandCore.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("givegrip")) {
                return Arrays.stream(WandGrip.values()).map(Enum::name).collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("givefocus")) {
                return Arrays.stream(WandFocus.values()).map(Enum::name).collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("givewand")) {
                return Arrays.stream(WandGrip.values()).map(Enum::name).collect(Collectors.toList());
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("givewand")) {
                return Arrays.stream(WandFocus.values()).map(Enum::name).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== MagicWands Befehle ===");
        player.sendMessage("§e/mw wandinfo §7- Zeigt Informationen über den gehaltenen Zauberstab");
        player.sendMessage("§e/mw givewand <core> <grip> <focus> §7- Erstellt einen Zauberstab mit den angegebenen Komponenten");
        player.sendMessage("§e/mw spells §7- Zeigt alle Zaubersprüche des gehaltenen Zauberstabs");
        player.sendMessage("§e/mw givecore <core> §7- Gibt einen Zauberstab-Kern");
        player.sendMessage("§e/mw givegrip <grip> §7- Gibt einen Zauberstab-Griff");
        player.sendMessage("§e/mw givefocus <focus> §7- Gibt einen Zauberstab-Fokus");
        player.sendMessage("§e/mw givespellrune <spell> <anzahl> §7- Gibt eine Zauber-Rune (Admin)");
        player.sendMessage("§e/mw reload §7- Lädt das Plugin neu (Admin)");
        player.sendMessage("");
        player.sendMessage("§6Crafting: §7Verwende /wandbuilder um Zauberstäbe zu craften!");
    }

    private void showWandInfo(Player player, Wand wand) {
        player.sendMessage("§6=== Zauberstab-Informationen ===");
        player.sendMessage("§7Kern: §e" + wand.getWandCore().name());
        player.sendMessage("§7Griff: §e" + wand.getWandGrip().name());
        player.sendMessage("§7Fokus: §e" + wand.getWandFocus().name());
        player.sendMessage("§7Mana: §b" + wand.getCurrentMana() + "/" + wand.getMaxMana());
        player.sendMessage("§7Mana-Regeneration: §b" + wand.getWandCore().getManaRegenRate() + " pro Sekunde");
        player.sendMessage("§7Cast-Speed: §b" + wand.getWandGrip().getCastSpeed());
        player.sendMessage("§7Crit-Chance: §b" + (int)(wand.getWandGrip().getCritChance() * 100) + "%");
        player.sendMessage("§7Element: §b" + wand.getWandFocus().getElementDamage().name());
        player.sendMessage("§7Spell-Slots: §b" + wand.getSpellSlots());
        player.sendMessage("§7Zaubersprüche: §b" + (wand.getSpells().isEmpty() ? "Keine" : 
                wand.getSpells().stream().map(Spell::getDisplayName).collect(Collectors.joining(", "))));
    }

    private void showSpells(Player player, Wand wand) {
        player.sendMessage("§6=== Verfügbare Zaubersprüche ===");
        if (wand.getSpells().isEmpty()) {
            player.sendMessage("§cDieser Zauberstab hat keine Zaubersprüche!");
            return;
        }

        for (Spell spell : wand.getSpells()) {
            String elementColor = getElementColor(spell);
            player.sendMessage(elementColor + "• " + spell.getDisplayName());
            player.sendMessage("  §7Mana-Kosten: §b" + spell.getManaCost());
            player.sendMessage("  §7Cooldown: §e" + spell.getCooldownSeconds() + " Sekunden");
            player.sendMessage("  §7Element: " + elementColor + spell.getElement().name());
            player.sendMessage("");
        }
    }

    private String getElementColor(Spell spell) {
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

    private ItemStack createCoreItem(WandCore core) {
        ItemStack item = new ItemStack(core.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Zauberstab-Kern: " + core.name());
            List<String> lore = new ArrayList<>();
            lore.add("§7Mana-Kapazität: §b" + core.getManaCapacity());
            lore.add("§7Mana-Regeneration: §b" + core.getManaRegenRate() + "/s");
            lore.add("");
            lore.add("§eVerwendung: Platziere diesen Kern");
            lore.add("§ein das Crafting-GUI (/wandbuilder)");
            meta.setLore(lore);
            
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(MagicWands.getInstance(), "wand_core"), PersistentDataType.STRING, core.name());
            
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createGripItem(WandGrip grip) {
        ItemStack item = new ItemStack(grip.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Zauberstab-Griff: " + grip.name());
            List<String> lore = new ArrayList<>();
            lore.add("§7Zauber-Geschwindigkeit: §b" + (grip.getCastSpeed() * 100) + "%");
            lore.add("§7Kritische Treffer: §b" + (grip.getCritChance() * 100) + "%");
            lore.add("");
            lore.add("§eVerwendung: Platziere diesen Griff");
            lore.add("§ein das Crafting-GUI (/wandbuilder)");
            meta.setLore(lore);
            
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(MagicWands.getInstance(), "wand_grip"), PersistentDataType.STRING, grip.name());
            
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createFocusItem(WandFocus focus) {
        ItemStack item = new ItemStack(focus.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Zauberstab-Fokus: " + focus.name());
            List<String> lore = new ArrayList<>();
            lore.add("§7Element: §b" + focus.getElementDamage().name());
            lore.add("");
            lore.add("§eVerwendung: Platziere diesen Fokus");
            lore.add("§ein das Crafting-GUI (/wandbuilder)");
            meta.setLore(lore);
            
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(MagicWands.getInstance(), "wand_focus"), PersistentDataType.STRING, focus.name());
            
            item.setItemMeta(meta);
        }
        return item;
    }
}