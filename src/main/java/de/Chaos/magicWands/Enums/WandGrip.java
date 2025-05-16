package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandGrip {
    SIMPLE(1.0, 0.05, 10, 30000, Material.STICK, "Einfacher Griff"),
    FINE_LEATHER(1.2, 0.10, 20, 30001, Material.LEATHER, "Feiner Ledergriff"),
    DRAGON_HIDE(1.5, 0.15, 25, 30002, Material.DRAGON_BREATH, "Drachenhaut-Griff"),
    VOID_SILK(2.0, 0.25, 40, 30003, Material.END_ROD, "Leerenseiden-Griff");

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

    public double getCastSpeed() {
        return castSpeed;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritDamagePercent() {
        return critDamagePercent;
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
