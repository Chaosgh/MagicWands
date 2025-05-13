package de.Chaos.magicWands.Enums;

public enum Spell {
    FIREBALL("Feuerball", 20, 5, ElementDamage.FIRE),
    ICE_SHARD("Eissplitter", 15, 4, ElementDamage.WATER),
    STONE_BLAST("Steinschlag", 25, 6, ElementDamage.EARTH),
    WIND_SLASH("Windschnitt", 10, 2, ElementDamage.AIR),
    ARCANE_BURST("Arkane Explosion", 30, 8, ElementDamage.ARCANE),
    LIGHTNING_STRIKE("Blitzschlag", 35, 10, ElementDamage.LIGHTNING),
    VOID_ORB("Leerenkugel", 50, 12, ElementDamage.DARK);

    private final String displayName;
    private final int manaCost;
    private final int cooldownSeconds;
    private final ElementDamage element;

    Spell(String displayName, int manaCost, int cooldownSeconds, ElementDamage element) {
        this.displayName = displayName;
        this.manaCost = manaCost;
        this.cooldownSeconds = cooldownSeconds;
        this.element = element;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public ElementDamage getElement() {
        return element;
    }
}
