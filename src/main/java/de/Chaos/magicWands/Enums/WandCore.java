package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandCore {
    OAK(100, 10000, Material.STICK, "Eichenkern"),
    BIRCH(120, 10001, Material.BIRCH_WOOD, "Birkenkern"),
    BLAZE_ROD(200, 10002, Material.BLAZE_ROD, "Blazekern"),
    ENDER_ROD(250, 10003, Material.END_ROD, "Endkern"),
    NETHER_STAR(500, 10004, Material.NETHER_STAR, "Netherkern");

    private final int manaCapacity;
    private final int customModelData;
    private final Material material;
    private final String displayName;

    WandCore(int manaCapacity, int customModelData, Material material, String displayName) {
        this.manaCapacity = manaCapacity;
        this.customModelData = customModelData;
        this.material = material;
        this.displayName = displayName;
    }

    public int getManaCapacity() {
        return manaCapacity;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }
}
