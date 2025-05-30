package de.Chaos.magicWands;

import de.Chaos.magicWands.Commands.CraftingCommand;
import de.Chaos.magicWands.Commands.MagicWandsCommand;
import de.Chaos.magicWands.Commands.RuneCommand;
import de.Chaos.magicWands.Commands.SpellRuneCommand;
import de.Chaos.magicWands.Guis.CraftingUI;
import de.Chaos.magicWands.Listeners.SpellRuneListener;
import de.Chaos.magicWands.Listeners.SpellSwitcher;
import de.Chaos.magicWands.Listeners.UpgradeListener;
import de.Chaos.magicWands.Logic.SpellRuneSystem;
import de.Chaos.magicWands.Logic.UpgradeSystem;
import de.Chaos.magicWands.Logic.WandUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MagicWands extends JavaPlugin {
    private static MagicWands instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        WandUtils.setPlugin(this);
        CraftingUI.setPlugin(this);
        UpgradeSystem.setPlugin(this);
        SpellRuneSystem.setPlugin(this);
        Objects.requireNonNull(this.getCommand("wandbuilder")).setExecutor(new CraftingCommand());
        Objects.requireNonNull(this.getCommand("mw")).setExecutor(new MagicWandsCommand());
        Objects.requireNonNull(this.getCommand("giverune")).setExecutor(new RuneCommand());
        Objects.requireNonNull(this.getCommand("givespellrune")).setExecutor(new SpellRuneCommand());
        Bukkit.getPluginManager().registerEvents(new CraftingUI(), this);
        Bukkit.getPluginManager().registerEvents(new SpellSwitcher(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpellRuneListener(), this);
        
        getLogger().info("MagicWands Plugin wurde aktiviert!");
    }
    @Override
    public void onDisable() {
        getLogger().info("MagicWands Plugin wurde deaktiviert!");
    }

}