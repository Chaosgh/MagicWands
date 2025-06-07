package de.Chaos.magicWands.Enums;

import org.bukkit.Material;
import de.Chaos.magicWands.Logic.UpgradeSystem.RuneType;

public enum WandCore {
    OAK(100.0, Material.STICK, 0.5),
    BIRCH(120.0, Material.BIRCH_WOOD, 1.0),
    BLAZE_ROD(200.0, Material.BLAZE_ROD, 1.5),
    ENDER_ROD(250.0, Material.END_ROD, 2.0),
    NETHER_STAR(500.0, Material.NETHER_STAR, 2.5);

    private final double manaCapacity;
    private final Material material;
    private final double manaRegenRate;

    WandCore(double manaCapacity, Material material, double manaRegenRate) {
        this.manaCapacity = manaCapacity;
        this.material = material;
        this.manaRegenRate = manaRegenRate;
    }

    public double getManaCapacity() {
        return manaCapacity;
    }

    public Material getMaterial() {
        return material;
    }

    public double getManaRegenRate() {
        return manaRegenRate;
    }

    public WandCore upgrade(RuneType runeType, int level) {
        switch (this) {
            case OAK:
                if (runeType == RuneType.MANA && level >= 2) return BIRCH;
                break;
            case BIRCH:
                if (runeType == RuneType.MANA && level >= 3) return BLAZE_ROD;
                break;
            case BLAZE_ROD:
                if (runeType == RuneType.MANA && level >= 4) return ENDER_ROD;
                break;
            case ENDER_ROD:
                if (runeType == RuneType.MANA && level >= 5) return NETHER_STAR;
                break;
            case NETHER_STAR:
                return this;
        }
        return this;
    }
} 