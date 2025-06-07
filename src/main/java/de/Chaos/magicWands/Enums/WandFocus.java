package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandFocus {
    FIRE(ElementDamage.FIRE, Material.BLAZE_POWDER),
    ICE(ElementDamage.WATER, Material.ICE),
    EARTH(ElementDamage.EARTH, Material.DIRT),
    WIND(ElementDamage.AIR, Material.FEATHER),
    ARCANE(ElementDamage.ARCANE, Material.ENDER_EYE),
    LIGHTNING(ElementDamage.LIGHTNING, Material.YELLOW_DYE),
    DARKNESS(ElementDamage.DARK, Material.BLACK_DYE);

    private final ElementDamage elementDamage;
    private final Material material;

    WandFocus(ElementDamage elementDamage, Material material) {
        this.elementDamage = elementDamage;
        this.material = material;
    }

    public ElementDamage getElementDamage() {
        return elementDamage;
    }

    public Material getMaterial() {
        return material;
    }

}
