package de.Chaos.magicWands.Enums;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public enum Spell {
    FIREBALL("Feuerball", 20, 5, ElementDamage.FIRE) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location targetLoc = getTargetLocation(player, 30);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 20) {
                        launchFireball(player, targetLoc, plugin);
                        cancel();
                        return;
                    }

                    double angle = ticks * 0.5;
                    Location handLoc = player.getEyeLocation().add(
                            Math.cos(angle) * 0.5,
                            -0.3 + Math.sin(ticks * 0.3) * 0.1,
                            Math.sin(angle) * 0.5
                    );

                    player.getWorld().spawnParticle(Particle.FLAME, handLoc, 3, 0.1, 0.1, 0.1, 0.01);
                    player.getWorld().spawnParticle(Particle.SMOKE, handLoc, 1, 0.05, 0.05, 0.05, 0.01);

                    if (ticks % 5 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.5f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void launchFireball(Player player, Location targetLoc, Plugin plugin) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5f, 0.8f);

            Vector direction = targetLoc.toVector().subtract(player.getEyeLocation().toVector()).normalize();
            Location startLoc = player.getEyeLocation().add(direction);

            new BukkitRunnable() {
                final Location currentLoc = startLoc.clone();
                double distanceTraveled = 0;
                final double maxDistance = player.getLocation().distance(targetLoc) + 5;

                @Override
                public void run() {
                    if (distanceTraveled >= maxDistance) {
                        explodeFireball(currentLoc, player);
                        cancel();
                        return;
                    }

                    if (currentLoc.getBlock().getType().isSolid()) {
                        explodeFireball(currentLoc, player);
                        cancel();
                        return;
                    }

                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity && !entity.equals(player)) {
                            explodeFireball(currentLoc, player);
                            cancel();
                            return;
                        }
                    }

                    currentLoc.getWorld().spawnParticle(Particle.FLAME, currentLoc, 8, 0.2, 0.2, 0.2, 0.05);
                    currentLoc.getWorld().spawnParticle(Particle.SMOKE, currentLoc, 3, 0.1, 0.1, 0.1, 0.02);
                    currentLoc.getWorld().spawnParticle(Particle.LAVA, currentLoc, 2, 0.1, 0.1, 0.1, 0.01);

                    currentLoc.add(direction.clone().multiply(0.5));
                    distanceTraveled += 0.5;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void explodeFireball(Location loc, Player player) {
            loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5, 1, 1, 1, 0.1);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 2, 2, 2, 0.1);
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 30, 1.5, 1.5, 1.5, 0.05);
            loc.getWorld().spawnParticle(Particle.LAVA, loc, 20, 1, 1, 1, 0.02);

            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.8f);
            loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.5f);

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 4, 4, 4)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    target.setFireTicks(100);
                    target.damage(8.0, player);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
                }
            }
        }
    },

    ICE_SHARD("Eissplitter", 15, 4, ElementDamage.WATER) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location targetLoc = getTargetLocation(player, 25);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 15) {
                        launchIceShards(player, targetLoc, plugin);
                        cancel();
                        return;
                    }

                    Location handLoc = player.getEyeLocation().add(0, -0.3, 0);
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, handLoc, 5, 0.3, 0.3, 0.3, 0.01);
                    player.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, handLoc, 3, 0.2, 0.2, 0.2, 0.01,
                            Material.ICE.createBlockData());

                    if (ticks % 3 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 0.7f, 1.8f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void launchIceShards(Player player, Location targetLoc, Plugin plugin) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.5f, 0.8f);

            Vector baseDirection = targetLoc.toVector().subtract(player.getEyeLocation().toVector()).normalize();
            Location startLoc = player.getEyeLocation().add(baseDirection);

            for (int i = 0; i < 5; i++) {
                Vector shardDirection = baseDirection.clone().add(new Vector(
                        (Math.random() - 0.5) * 0.3,
                        (Math.random() - 0.5) * 0.2,
                        (Math.random() - 0.5) * 0.3
                )).normalize();

                new BukkitRunnable() {
                    final Location currentLoc = startLoc.clone();
                    double distanceTraveled = 0;
                    final double maxDistance = player.getLocation().distance(targetLoc) + 3;

                    @Override
                    public void run() {
                        if (distanceTraveled >= maxDistance) {
                            iceShardImpact(currentLoc, player);
                            cancel();
                            return;
                        }

                        if (currentLoc.getBlock().getType().isSolid()) {
                            iceShardImpact(currentLoc, player);
                            cancel();
                            return;
                        }

                        for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 0.5, 0.5, 0.5)) {
                            if (entity instanceof LivingEntity && !entity.equals(player)) {
                                iceShardImpact(currentLoc, player);
                                cancel();
                                return;
                            }
                        }

                        currentLoc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, currentLoc, 3, 0.1, 0.1, 0.1, 0.01,
                                Material.ICE.createBlockData());
                        currentLoc.getWorld().spawnParticle(Particle.SNOWFLAKE, currentLoc, 2, 0.05, 0.05, 0.05, 0.01);

                        currentLoc.add(shardDirection.clone().multiply(0.4));
                        distanceTraveled += 0.4;
                    }
                }.runTaskTimer(plugin, i * 2, 1);
            }
        }

        private void iceShardImpact(Location loc, Player player) {
            loc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc, 15, 0.5, 0.5, 0.5, 0.1,
                    Material.ICE.createBlockData());
            loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 10, 0.3, 0.3, 0.3, 0.05);

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    target.damage(5.0, player);
                    target.setFreezeTicks(120);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                }
            }
        }
    },

    LIGHTNING_STRIKE("Blitzschlag", 35, 10, ElementDamage.LIGHTNING) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location targetLoc = getTargetLocation(player, 25);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 30) {
                        strikeWithLightning(targetLoc, player, plugin);
                        cancel();
                        return;
                    }

                    Location skyLoc = targetLoc.clone().add(0, 20, 0);
                    skyLoc.getWorld().spawnParticle(Particle.CLOUD, skyLoc, 10, 3, 1, 3, 0.02);
                    skyLoc.getWorld().spawnParticle(Particle.SMOKE, skyLoc, 5, 2, 1, 2, 0.01);

                    if (ticks % 10 == 0) {
                        skyLoc.getWorld().playSound(skyLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 0.5f);
                    }

                    targetLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, targetLoc.clone().add(0, 1, 0),
                            3, 0.5, 2, 0.5, 0.01);

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void strikeWithLightning(Location target, Player player, Plugin plugin) {
            target.getWorld().strikeLightningEffect(target);
            target.getWorld().playSound(target, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2f, 1f);

            for (Entity entity : target.getWorld().getNearbyEntities(target, 3, 3, 3)) {
                if (entity instanceof LivingEntity livingTarget && !entity.equals(player)) {
                    livingTarget.damage(15.0, player);
                    livingTarget.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));
                }
            }

            new BukkitRunnable() {
                int chains = 0;
                Location currentLoc = target.clone();

                @Override
                public void run() {
                    if (chains >= 3) {
                        cancel();
                        return;
                    }

                    LivingEntity nearestEnemy = null;
                    double nearestDistance = Double.MAX_VALUE;

                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 8, 8, 8)) {
                        if (entity instanceof LivingEntity livingEntity && !entity.equals(player)) {
                            double distance = entity.getLocation().distance(currentLoc);
                            if (distance < nearestDistance) {
                                nearestDistance = distance;
                                nearestEnemy = livingEntity;
                            }
                        }
                    }

                    if (nearestEnemy != null) {
                        createLightningChain(currentLoc, nearestEnemy.getLocation(), plugin);
                        nearestEnemy.damage(15.0 - (chains * 3), player);
                        nearestEnemy.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));

                        currentLoc = nearestEnemy.getLocation();
                        chains++;
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5, 10);
        }

        private void createLightningChain(Location from, Location to, Plugin plugin) {
            Vector direction = to.toVector().subtract(from.toVector());
            double distance = direction.length();
            direction.normalize();

            new BukkitRunnable() {
                double traveled = 0;

                @Override
                public void run() {
                    if (traveled >= distance) {
                        cancel();
                        return;
                    }

                    Location currentLoc = from.clone().add(direction.clone().multiply(traveled));
                    currentLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, currentLoc, 5, 0.1, 0.1, 0.1, 0.1);
                    currentLoc.getWorld().spawnParticle(Particle.END_ROD, currentLoc, 2, 0.05, 0.05, 0.05, 0.01);

                    traveled += 0.5;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    },

    VOID_ORB("Leerenkugel", 50, 12, ElementDamage.DARK) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location targetLoc = getTargetLocation(player, 20);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 40) {
                        createVoidOrb(targetLoc, player, plugin);
                        cancel();
                        return;
                    }

                    Location handLoc = player.getEyeLocation().add(0, -0.3, 0);
                    double angle = ticks * 0.3;

                    for (int i = 0; i < 3; i++) {
                        double spiralAngle = angle + (i * Math.PI * 2 / 3);
                        Location spiralLoc = handLoc.clone().add(
                                Math.cos(spiralAngle) * 0.8,
                                Math.sin(ticks * 0.2) * 0.3,
                                Math.sin(spiralAngle) * 0.8
                        );

                        spiralLoc.getWorld().spawnParticle(Particle.PORTAL, spiralLoc, 2, 0.05, 0.05, 0.05, 0.01);
                        spiralLoc.getWorld().spawnParticle(Particle.SMOKE, spiralLoc, 1, 0.02, 0.02, 0.02, 0.01);
                    }

                    if (ticks % 8 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 0.5f, 0.5f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void createVoidOrb(Location orbLoc, Player player, Plugin plugin) {
            player.getWorld().playSound(orbLoc, Sound.ENTITY_ENDERMAN_SCREAM, 1.5f, 0.3f);

            new BukkitRunnable() {
                int lifetime = 0;

                @Override
                public void run() {
                    if (lifetime >= 100) {
                        voidOrbExplode(orbLoc, player);
                        cancel();
                        return;
                    }

                    orbLoc.getWorld().spawnParticle(Particle.PORTAL, orbLoc, 15, 0.3, 0.3, 0.3, 0.1);
                    orbLoc.getWorld().spawnParticle(Particle.SMOKE, orbLoc, 5, 0.2, 0.2, 0.2, 0.02);
                    orbLoc.getWorld().spawnParticle(Particle.END_ROD, orbLoc, 3, 0.1, 0.1, 0.1, 0.01);

                    for (Entity entity : orbLoc.getWorld().getNearbyEntities(orbLoc, 6, 6, 6)) {
                        if (entity instanceof LivingEntity target && !entity.equals(player)) {
                            Vector pullVector = orbLoc.toVector().subtract(entity.getLocation().toVector());
                            pullVector.normalize().multiply(0.3);
                            entity.setVelocity(entity.getVelocity().add(pullVector));

                            if (entity.getLocation().distance(orbLoc) < 2) {
                                target.damage(2.0, player);
                                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
                            }
                        }
                    }

                    if (lifetime % 20 == 0) {
                        orbLoc.getWorld().playSound(orbLoc, Sound.BLOCK_PORTAL_AMBIENT, 0.8f, 0.5f);
                    }

                    lifetime++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void voidOrbExplode(Location loc, Player player) {
            loc.getWorld().spawnParticle(Particle.PORTAL, loc, 100, 3, 3, 3, 0.2);
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 50, 2, 2, 2, 0.1);
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 30, 2, 2, 2, 0.05);

            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.5f, 0.5f);

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 8, 8, 8)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    double distance = entity.getLocation().distance(loc);
                    double damage = 20.0 * (1.0 - distance / 8.0);

                    target.damage(damage, player);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

                    Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2);
                    entity.setVelocity(knockback);
                }
            }
        }
    },

    ARCANE_BURST("Arkane Explosion", 30, 8, ElementDamage.ARCANE) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location targetLoc = getTargetLocation(player, 15);

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 25) {
                        arcaneExplosion(targetLoc, player, plugin);
                        cancel();
                        return;
                    }

                    double radius = ticks * 0.1;

                    for (int i = 0; i < 16; i++) {
                        double angle = (Math.PI * 2 * i) / 16;
                        Location ringLoc = targetLoc.clone().add(
                                Math.cos(angle) * radius,
                                Math.sin(ticks * 0.2) * 0.5,
                                Math.sin(angle) * radius
                        );

                        ringLoc.getWorld().spawnParticle(Particle.END_ROD, ringLoc, 1, 0.02, 0.02, 0.02, 0.01);
                        ringLoc.getWorld().spawnParticle(Particle.ENCHANT, ringLoc, 2, 0.05, 0.05, 0.05, 0.01);
                    }

                    if (ticks % 5 == 0) {
                        targetLoc.getWorld().playSound(targetLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.2f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void arcaneExplosion(Location center, Player player, Plugin plugin) {
            new BukkitRunnable() {
                double radius = 0;

                @Override
                public void run() {
                    if (radius >= 8) {
                        cancel();
                        return;
                    }

                    for (int i = 0; i < 32; i++) {
                        double angle = (Math.PI * 2 * i) / 32;
                        Location waveLoc = center.clone().add(
                                Math.cos(angle) * radius,
                                0,
                                Math.sin(angle) * radius
                        );

                        waveLoc.getWorld().spawnParticle(Particle.END_ROD, waveLoc, 3, 0.1, 0.1, 0.1, 0.05);
                        waveLoc.getWorld().spawnParticle(Particle.ENCHANT, waveLoc, 5, 0.2, 0.2, 0.2, 0.1);
                    }

                    for (Entity entity : center.getWorld().getNearbyEntities(center, radius + 1, 3, radius + 1)) {
                        if (entity instanceof LivingEntity target && !entity.equals(player)) {
                            double distance = entity.getLocation().distance(center);
                            if (Math.abs(distance - radius) < 1.5) {
                                target.damage(12.0, player);
                                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
                            }
                        }
                    }

                    center.getWorld().playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.5f, 0.8f);
                    radius += 0.8;
                }
            }.runTaskTimer(plugin, 0, 2);
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

    public abstract void cast(Player player, Plugin plugin);

    private static Location getTargetLocation(Player player, int maxDistance) {
        if (player.getTargetBlockExact(maxDistance) != null) {
            return Objects.requireNonNull(player.getTargetBlockExact(maxDistance)).getLocation().add(0, 1, 0);
        }

        return player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(maxDistance));
    }
}