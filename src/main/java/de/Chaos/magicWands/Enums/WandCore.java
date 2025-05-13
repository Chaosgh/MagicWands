package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandCore {
    OAK(100,10000, Material.PAPER),
    BIRCH(120,10000, Material.PAPER),
    BLAZE_ROD(200, 10000, Material.PAPER),
    ENDER_ROD(250, 10000, Material.PAPER),
    NETHER_STAR(500, 10000, Material.PAPER);

    private final int manaCapacity;
    private final int customModelData;
    private final Material material;

    WandCore(int manaCapacity, int customModelData, Material material) {
        this.manaCapacity = manaCapacity;
        this.customModelData = customModelData;
        this.material = material;
    }

    public int getManaCapacity() {
        return manaCapacity;
    }
    public double getCustomModelData(){
        return customModelData;
    }
}
