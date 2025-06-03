package de.Chaos.magicWands.Enums;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public enum Spell {
    FIREBALL("Feuerball", 20, 5, ElementDamage.FIRE) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 30, 0.2, 0.2, 0.2, 0.01);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);

            player.getNearbyEntities(3, 3, 3).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.setFireTicks(60); // 3 Sekunden Feuer
                    target.damage(6.0, player);
                }
            });
        }
    },
    ICE_SHARD("Eissplitter", 15, 4, ElementDamage.WATER) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, player.getEyeLocation(), 30, 0.3, 0.3, 0.3, 0.01);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1.2f);

            player.getNearbyEntities(3, 3, 3).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.damage(4.0, player);
                    target.setFreezeTicks(100); // 5 Sekunden einfrieren
                }
            });
        }
    },
    STONE_BLAST("Steinschlag", 25, 6, ElementDamage.EARTH) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, player.getEyeLocation(), 30, 0.3, 0.3, 0.3, 0.01);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1, 1f);

            player.getNearbyEntities(4, 4, 4).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.damage(8.0, player);
                }
            });
        }
    },
    WIND_SLASH("Windschnitt", 10, 2, ElementDamage.AIR) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.CLOUD, player.getEyeLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 1.2f);

            player.getNearbyEntities(3, 3, 3).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.2);
                    knockback.setY(0.5);
                    target.setVelocity(knockback);
                    target.damage(3.0, player);
                }
            });
        }
    },
    ARCANE_BURST("Arkane Explosion", 30, 8, ElementDamage.ARCANE) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 50, 1, 1, 1, 0.05);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1f);

            player.getNearbyEntities(5, 5, 5).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.damage(10.0, player);
                }
            });
        }
    },
    LIGHTNING_STRIKE("Blitzschlag", 35, 10, ElementDamage.LIGHTNING) {
        @Override
        public void cast(Player player) {
            player.getWorld().strikeLightningEffect(player.getTargetBlockExact(10).getLocation());

            player.getNearbyEntities(4, 4, 4).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.damage(12.0, player);
                }
            });
        }
    },
    VOID_ORB("Leerenkugel", 50, 12, ElementDamage.DARK) {
        @Override
        public void cast(Player player) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 40, 1, 1, 1, 0.1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0.5f);

            player.getNearbyEntities(6, 6, 6).forEach(entity -> {
                if (entity instanceof LivingEntity target) {
                    target.damage(14.0, player);
                    target.setGravity(false); // Schweben als Leereffekt
                }
            });
        }
    };
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

    public abstract void cast(Player player);
}
