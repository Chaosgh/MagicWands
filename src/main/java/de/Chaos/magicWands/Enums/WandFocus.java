package de.Chaos.magicWands.Enums;


import org.bukkit.Material;

public enum WandFocus {
    FIRE(ElementDamage.FIRE,10000, Material.PAPER),
    ICE(ElementDamage.WATER,10000, Material.PAPER),
    EARTH(ElementDamage.EARTH,10000, Material.PAPER),
    WIND(ElementDamage.AIR,10000, Material.PAPER),
    ARCANE(ElementDamage.ARCANE,10000, Material.PAPER),
    LIGHTNING(ElementDamage.LIGHTNING,10000, Material.PAPER),
    DARKNESS(ElementDamage.DARK,10000, Material.PAPER);

    private final ElementDamage elementDamage;
    private final int customModelData;
    private final Material material;

    WandFocus(ElementDamage elementDamage, int customModelData, Material material) {
        this.elementDamage = elementDamage;
        this.customModelData = customModelData;
        this.material = material;
    }


    public ElementDamage getElementDamage() {
        return elementDamage;
    }

    public double getCustomModelData() {
        return customModelData;
    }
}
