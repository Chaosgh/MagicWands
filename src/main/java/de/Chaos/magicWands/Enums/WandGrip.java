package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandGrip {
    SIMPLE(1.0, 0.05,10,10000, Material.PAPER, "Einfacher Griff"),
    FINE_LEATHER(1.2, 0.10,20,10000, Material.PAPER, "Feiner Ledergriff"),
    DRAGON_HIDE(1.5, 0.15,25,10000, Material.PAPER, "Drachenhaut Griff"),
    VOID_SILK(2.0, 0.25,40, 10000, Material.PAPER, "Leehrenseiden Griff");

    private final double castSpeed;
    private final double critChance;
    private final double critDamagePercent;
    private final int customModelData;
    private final Material material;
    private final String displayName;



    WandGrip(double castSpeed, double critChance, double critDamagePercent, int customModelData, Material material, String displayName) {
        this.castSpeed = castSpeed;
        this.critChance = critChance;
        this.critDamagePercent = critDamagePercent;
        this.customModelData = customModelData;
        this.material = material;
        this.displayName = displayName;

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
