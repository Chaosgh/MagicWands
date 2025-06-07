package de.Chaos.magicWands.Listeners;

import de.Chaos.magicWands.Logic.Wand;
import de.Chaos.magicWands.Logic.WandUtils;
import de.Chaos.magicWands.MagicWands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaDisplayListener implements Listener {
    private static final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    private static final int UPDATE_INTERVAL = 2; // 0.1 Sekunden (2 Ticks)

    public static void startManaDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, BossBar> entry : playerBossBars.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {
                        entry.getValue().removeAll();
                        playerBossBars.remove(entry.getKey());
                        continue;
                    }

                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem.getType() != Material.BLAZE_ROD) {
                        entry.getValue().removeAll();
                        continue;
                    }

                    Wand wand = WandUtils.getWandFromItem(heldItem);
                    if (wand == null) {
                        entry.getValue().removeAll();
                        continue;
                    }

                    updateManaBar(player, wand, entry.getValue());
                }
            }
        }.runTaskTimer(MagicWands.getInstance(), 0, UPDATE_INTERVAL);
    }

    public static void updateManaBar(Player player, Wand wand, BossBar bossBar) {
        double currentMana = wand.getCurrentMana();
        double maxMana = wand.getMaxMana();
        double percentage = currentMana / maxMana;

        bossBar.setTitle(String.format("§f🔷 §7Mana: §b%.1f/%.1f", currentMana, maxMana));
        bossBar.setProgress(percentage);
        bossBar.addPlayer(player);
    }

    public static BossBar getPlayerBossBar(Player player) {
        return playerBossBars.get(player.getUniqueId());
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (newItem != null && newItem.getType() == Material.BLAZE_ROD) {
            Wand wand = WandUtils.getWandFromItem(newItem);
            if (wand != null) {
                BossBar bossBar = playerBossBars.computeIfAbsent(player.getUniqueId(), uuid -> {
                    BossBar bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
                    bar.addPlayer(player);
                    return bar;
                });
                updateManaBar(player, wand, bossBar);
                bossBar.setVisible(true);
            }
        } else {
            BossBar bossBar = playerBossBars.get(player.getUniqueId());
            if (bossBar != null) {
                bossBar.removeAll();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossBar bossBar = playerBossBars.remove(event.getPlayer().getUniqueId());
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }
} 