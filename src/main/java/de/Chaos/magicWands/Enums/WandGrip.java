package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandGrip {
    SIMPLE(1.0, 0.05,10,10000, Material.PAPER),
    FINE_LEATHER(1.2, 0.10,20,10000, Material.PAPER),
    DRAGON_HIDE(1.5, 0.15,25,10000, Material.PAPER),
    VOID_SILK(2.0, 0.25,40, 10000, Material.PAPER);

    private final double castSpeed;
    private final double critChance;
    private final double critDamagePercent;
    private final int customModelData;
    private final Material material;


    WandGrip(double castSpeed, double critChance, double critDamagePercent, int customModelData, Material material) {
        this.castSpeed = castSpeed;
        this.critChance = critChance;
        this.critDamagePercent = critDamagePercent;
        this.customModelData = customModelData;
        this.material = material;
    }

    public double getCustomModelData(){
        return customModelData;
    }

    public double getCastSpeed() {
        return castSpeed;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritDamagePercent() {

        return critDamagePercent;
    }
}
