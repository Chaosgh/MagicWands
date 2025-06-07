package de.Chaos.magicWands;

import de.Chaos.magicWands.Commands.CraftingCommand;
import de.Chaos.magicWands.Commands.MagicWandsCommand;
import de.Chaos.magicWands.Guis.CraftingUI;
import de.Chaos.magicWands.Listeners.SpellRuneListener;
import de.Chaos.magicWands.Listeners.SpellSwitcher;
import de.Chaos.magicWands.Listeners.UpgradeListener;
import de.Chaos.magicWands.Listeners.ManaDisplayListener;
import de.Chaos.magicWands.Logic.SpellRegistry;
import de.Chaos.magicWands.Logic.SpellRuneSystem;
import de.Chaos.magicWands.Logic.UpgradeSystem;
import de.Chaos.magicWands.Logic.Wand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MagicWands extends JavaPlugin {
    private static MagicWands instance;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize all systems with plugin instance
        CraftingUI.setPlugin(this);
        UpgradeSystem.setPlugin(this);
        SpellRuneSystem.setPlugin(this);

        SpellRegistry.initialize(this);
        Wand.initializeManaRegeneration(this);

        // Register commands
        Objects.requireNonNull(this.getCommand("wandbuilder")).setExecutor(new CraftingCommand());
        Objects.requireNonNull(this.getCommand("mw")).setExecutor(new MagicWandsCommand());

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new CraftingUI(), this);
        Bukkit.getPluginManager().registerEvents(new SpellSwitcher(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpellRuneListener(), this);
        Bukkit.getPluginManager().registerEvents(new ManaDisplayListener(), this);

        // Starte Mana-Display
        ManaDisplayListener.startManaDisplayTask();

        getLogger().info("MagicWands Plugin wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        // Clean up SpellRegistry on plugin shutdown
        SpellRegistry.cleanup();
        getLogger().info("MagicWands Plugin wurde deaktiviert!");
    }

    // Getter method for other classes to access the plugin instance
    public static MagicWands getInstance() {
        return instance;
    }
}