package de.Chaos.magicWands.Enums;

import org.bukkit.Material;

public enum WandFocus {
    FIRE(ElementDamage.FIRE, 20000, Material.BLAZE_POWDER, "Feuerfokus"),
    ICE(ElementDamage.WATER, 20001, Material.ICE, "Eisfokus"),
    EARTH(ElementDamage.EARTH, 20002, Material.DIRT, "Erdfokus"),
    WIND(ElementDamage.AIR, 20003, Material.FEATHER, "Windfokus"),
    ARCANE(ElementDamage.ARCANE, 20004, Material.ENDER_EYE, "Arkanerfokus"),
    LIGHTNING(ElementDamage.LIGHTNING, 20005, Material.YELLOW_DYE, "Blitzfokus"),
    DARKNESS(ElementDamage.DARK, 20006, Material.BLACK_DYE, "Dunkelfokus");

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
