package de.Chaos.magicWands.Guis;

import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;
import de.Chaos.magicWands.Logic.Wand;
import de.Chaos.magicWands.Logic.WandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class CraftingUI implements Listener {
    private static final String TITLE = "§9Zauberstab-Hersteller";
    private static Plugin plugin;

    public static void setPlugin(Plugin pl) {
        plugin = pl;
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, TITLE);
        inv.setItem(8, createButton("§a§lErstellen"));
        player.openInventory(inv);
    }

    private static ItemStack createButton(String name) {
        ItemStack item = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;

        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        if (e.getSlot() == 8) {
            ItemStack coreItem = inv.getItem(3);
            ItemStack gripItem = inv.getItem(4);
            ItemStack focusItem = inv.getItem(5);

            if (!isValid(coreItem) || !isValid(gripItem) || !isValid(focusItem)) {
                player.sendMessage("§cBitte alle drei Komponenten einlegen.");
                return;
            }

            WandCore core = getEnumFromNBT(coreItem, "wand_core", WandCore.class);
            WandGrip grip = getEnumFromNBT(gripItem, "wand_grip", WandGrip.class);
            WandFocus focus = getEnumFromNBT(focusItem, "wand_focus", WandFocus.class);

            if (core == null || grip == null || focus == null) {
                player.sendMessage("§cUngültige Komponenten!");
                return;
            }

            Wand wand = WandBuilder.buildWand(core, grip, focus);
            ItemStack wandItem = wand.toItemStack(plugin);
            player.getInventory().addItem(wandItem);
            player.sendMessage("§aDu hast erfolgreich einen Zauberstab erstellt!");
            player.closeInventory();
        } else if (e.getSlot() == 3 || e.getSlot() == 4 || e.getSlot() == 5) {
            e.setCancelled(false); // Spieler darf Komponenten einlegen
        }
    }

    private boolean isValid(ItemStack item) {
        return item != null && item.hasItemMeta();
    }

    private <T extends Enum<T>> T getEnumFromNBT(ItemStack item, String key, Class<T> enumClass) {
        if (!item.hasItemMeta()) return null;
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        String value = data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
        if (value == null) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
