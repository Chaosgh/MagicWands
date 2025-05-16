package de.Chaos.magicWands.Enums;


import org.bukkit.Material;

public enum WandFocus {
    FIRE(ElementDamage.FIRE,10000, Material.PAPER, "Feuerfokus"),
    ICE(ElementDamage.WATER,10000, Material.PAPER, "Eisfokus"),
    EARTH(ElementDamage.EARTH,10000, Material.PAPER, "Erdfokus"),
    WIND(ElementDamage.AIR,10000, Material.PAPER, "Windfokus"),
    ARCANE(ElementDamage.ARCANE,10000, Material.PAPER, "Arkanerfokus"),
    LIGHTNING(ElementDamage.LIGHTNING,10000, Material.PAPER, "Blitzfokus"),
    DARKNESS(ElementDamage.DARK,10000, Material.PAPER, "Dunkelfokus");

    private final ElementDamage elementDamage;
    private final int customModelData;
    private final Material material;
    private final String displayName;


    WandFocus(ElementDamage elementDamage, int customModelData, Material material, String displayName) {
        this.elementDamage = elementDamage;
        this.customModelData = customModelData;
        this.material = material;
        this.displayName = displayName;

    }


    public ElementDamage getElementDamage() {
        return elementDamage;
    }

    public double getCustomModelData() {
        return customModelData;
    }
}
