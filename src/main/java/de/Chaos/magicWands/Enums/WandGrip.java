package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandGrip {
    SIMPLE(1.0, 0.05, Material.STICK),
    FINE_LEATHER(1.2, 0.10, Material.LEATHER),
    DRAGON_HIDE(1.5, 0.15, Material.DRAGON_BREATH),
    VOID_SILK(2.0, 0.25, Material.END_ROD);

    private final double castSpeed;
    private final double critChance;
    private final Material material;

    WandGrip(double castSpeed, double critChance, Material material) {
        this.castSpeed = castSpeed;
        this.critChance = critChance;
        this.material = material;
    }

    public double getCastSpeed() {
        return castSpeed;
    }

    public double getCritChance() {
        return critChance;
    }

    public Material getMaterial() {
        return material;
    }


}
