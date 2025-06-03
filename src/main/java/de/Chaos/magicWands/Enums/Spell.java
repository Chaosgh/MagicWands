package de.Chaos.magicWands.Enums;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Spell {
    FIREBALL("Feuerball", 20, 5, ElementDamage.FIRE) {
        @Override
        public void cast(Player player, Plugin plugin) {
            // Casting Animation
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 20) {
                        // Explosive Fireball Launch
                        launchFireball(player, plugin);
                        cancel();
                        return;
                    }

                    // Charging Animation
                    double angle = ticks * 0.5;
                    Location handLoc = player.getEyeLocation().add(
                            Math.cos(angle) * 0.5,
                            -0.3 + Math.sin(ticks * 0.3) * 0.1,
                            Math.sin(angle) * 0.5
                    );

                    player.getWorld().spawnParticle(Particle.FLAME, handLoc, 3, 0.1, 0.1, 0.1, 0.01);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, handLoc, 1, 0.05, 0.05, 0.05, 0.01);

                    if (ticks % 5 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.5f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void launchFireball(Player player, Plugin plugin) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5f, 0.8f);

            // Create projectile trail
            Vector direction = player.getEyeLocation().getDirection().normalize();
            Location startLoc = player.getEyeLocation().add(direction);

            new BukkitRunnable() {
                int distance = 0;
                Location currentLoc = startLoc.clone();

                @Override
                public void run() {
                    if (distance >= 20) {
                        explodeFireball(currentLoc, player, plugin);
                        cancel();
                        return;
                    }

                    // Check for collision
                    if (currentLoc.getBlock().getType().isSolid() ||
                            !currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1).isEmpty()) {
                        explodeFireball(currentLoc, player, plugin);
                        cancel();
                        return;
                    }

                    // Projectile effects
                    currentLoc.getWorld().spawnParticle(Particle.FLAME, currentLoc, 8, 0.2, 0.2, 0.2, 0.05);
                    currentLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, currentLoc, 3, 0.1, 0.1, 0.1, 0.02);
                    currentLoc.getWorld().spawnParticle(Particle.DRIP_LAVA, currentLoc, 2, 0.1, 0.1, 0.1, 0.01);

                    currentLoc.add(direction);
                    distance++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void explodeFireball(Location loc, Player player, Plugin plugin) {
            // Massive explosion effect
            loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 5, 1, 1, 1, 0.1);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 2, 2, 2, 0.1);
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 1.5, 1.5, 1.5, 0.05);
            loc.getWorld().spawnParticle(Particle.LAVA, loc, 20, 1, 1, 1, 0.02);

            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.8f);
            loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 0.5f);

            // Damage and burn enemies
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
            // Ice formation animation
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 15) {
                        launchIceShards(player, plugin);
                        cancel();
                        return;
                    }

                    // Frost gathering effect
                    Location handLoc = player.getEyeLocation().add(0, -0.3, 0);
                    player.getWorld().spawnParticle(Particle.SNOWBALL, handLoc, 5, 0.3, 0.3, 0.3, 0.01);
                    player.getWorld().spawnParticle(Particle.ITEM_CRACK, handLoc, 3, 0.2, 0.2, 0.2, 0.01,
                            new ItemStack(Material.ICE));

                    if (ticks % 3 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 0.7f, 1.8f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void launchIceShards(Player player, Plugin plugin) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.5f, 0.8f);

            // Launch multiple ice shards
            for (int i = 0; i < 5; i++) {
                Vector baseDirection = player.getEyeLocation().getDirection().normalize();
                Vector shardDirection = baseDirection.clone().add(new Vector(
                        (Math.random() - 0.5) * 0.5,
                        (Math.random() - 0.5) * 0.3,
                        (Math.random() - 0.5) * 0.5
                )).normalize();

                Location startLoc = player.getEyeLocation().add(shardDirection);
                final int shardIndex = i;

                new BukkitRunnable() {
                    int distance = 0;
                    Location currentLoc = startLoc.clone();

                    @Override
                    public void run() {
                        if (distance >= 15) {
                            iceShardImpact(currentLoc, player);
                            cancel();
                            return;
                        }

                        // Check collision
                        if (currentLoc.getBlock().getType().isSolid()) {
                            iceShardImpact(currentLoc, player);
                            cancel();
                            return;
                        }

                        // Shard trail effects
                        currentLoc.getWorld().spawnParticle(Particle.ITEM_CRACK, currentLoc, 3, 0.1, 0.1, 0.1, 0.01,
                                new ItemStack(Material.ICE));
                        currentLoc.getWorld().spawnParticle(Particle.SNOWBALL, currentLoc, 2, 0.05, 0.05, 0.05, 0.01);

                        currentLoc.add(shardDirection);
                        distance++;
                    }
                }.runTaskTimer(plugin, shardIndex * 2, 1);
            }
        }

        private void iceShardImpact(Location loc, Player player) {
            loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc, 15, 0.5, 0.5, 0.5, 0.1,
                    new ItemStack(Material.ICE));
            loc.getWorld().spawnParticle(Particle.SNOWBALL, loc, 10, 0.3, 0.3, 0.3, 0.05);

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    target.damage(5.0, player);
                    target.setFreezeTicks(120);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
                }
            }
        }
    },

    LIGHTNING_STRIKE("Blitzschlag", 35, 10, ElementDamage.LIGHTNING) {
        @Override
        public void cast(Player player, Plugin plugin) {
            Location target = player.getTargetBlockExact(15) != null ?
                    player.getTargetBlockExact(15).getLocation() :
                    player.getLocation().add(player.getEyeLocation().getDirection().multiply(10));

            // Storm buildup
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 30) {
                        strikeWithLightning(target, player, plugin);
                        cancel();
                        return;
                    }

                    // Storm clouds gathering
                    Location skyLoc = target.clone().add(0, 20, 0);
                    skyLoc.getWorld().spawnParticle(Particle.CLOUD, skyLoc, 10, 3, 1, 3, 0.02);
                    skyLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, skyLoc, 5, 2, 1, 2, 0.01);

                    if (ticks % 10 == 0) {
                        skyLoc.getWorld().playSound(skyLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 0.5f);
                    }

                    // Electric charge building up
                    target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.clone().add(0, 1, 0),
                            3, 0.5, 2, 0.5, 0.01);

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void strikeWithLightning(Location target, Player player, Plugin plugin) {
            // Epic lightning strike
            target.getWorld().strikeLightningEffect(target);
            target.getWorld().playSound(target, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2f, 1f);

            // Chain lightning effect
            new BukkitRunnable() {
                int chains = 0;
                Location currentLoc = target.clone();

                @Override
                public void run() {
                    if (chains >= 3) {
                        cancel();
                        return;
                    }

                    // Find nearest enemy for chain
                    LivingEntity nearestEnemy = null;
                    double nearestDistance = Double.MAX_VALUE;

                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 8, 8, 8)) {
                        if (entity instanceof LivingEntity target && !entity.equals(player)) {
                            double distance = entity.getLocation().distance(currentLoc);
                            if (distance < nearestDistance) {
                                nearestDistance = distance;
                                nearestEnemy = target;
                            }
                        }
                    }

                    if (nearestEnemy != null) {
                        // Lightning chain effect
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
            // Void energy gathering
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 40) {
                        createVoidOrb(player, plugin);
                        cancel();
                        return;
                    }

                    // Dark energy swirling
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
                        spiralLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, spiralLoc, 1, 0.02, 0.02, 0.02, 0.01);
                    }

                    if (ticks % 8 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 0.5f, 0.5f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void createVoidOrb(Player player, Plugin plugin) {
            Location orbLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(3));

            player.getWorld().playSound(orbLoc, Sound.ENTITY_ENDERMAN_SCREAM, 1.5f, 0.3f);

            // Void orb existence and effects
            new BukkitRunnable() {
                int lifetime = 0;

                @Override
                public void run() {
                    if (lifetime >= 100) {
                        voidOrbExplode(orbLoc, player, plugin);
                        cancel();
                        return;
                    }

                    // Void orb visual
                    orbLoc.getWorld().spawnParticle(Particle.PORTAL, orbLoc, 15, 0.3, 0.3, 0.3, 0.1);
                    orbLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, orbLoc, 5, 0.2, 0.2, 0.2, 0.02);
                    orbLoc.getWorld().spawnParticle(Particle.END_ROD, orbLoc, 3, 0.1, 0.1, 0.1, 0.01);

                    // Gravity well effect
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

        private void voidOrbExplode(Location loc, Player player, Plugin plugin) {
            // Void explosion
            loc.getWorld().spawnParticle(Particle.PORTAL, loc, 100, 3, 3, 3, 0.2);
            loc.getWorld().spawnParticle(Particle.END_ROD, loc, 50, 2, 2, 2, 0.1);
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 2, 2, 2, 0.05);

            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.5f, 0.5f);

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 8, 8, 8)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    double distance = entity.getLocation().distance(loc);
                    double damage = 20.0 * (1.0 - distance / 8.0);

                    target.damage(damage, player);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

                    // Knockback
                    Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2);
                    entity.setVelocity(knockback);
                }
            }
        }
    },

    ARCANE_BURST("Arkane Explosion", 30, 8, ElementDamage.ARCANE) {
        @Override
        public void cast(Player player, Plugin plugin) {
            // Arcane energy buildup
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 25) {
                        arcaneExplosion(player, plugin);
                        cancel();
                        return;
                    }

                    // Magical energy rings
                    Location center = player.getLocation().add(0, 1, 0);
                    double radius = ticks * 0.1;

                    for (int i = 0; i < 16; i++) {
                        double angle = (Math.PI * 2 * i) / 16;
                        Location ringLoc = center.clone().add(
                                Math.cos(angle) * radius,
                                Math.sin(ticks * 0.2) * 0.5,
                                Math.sin(angle) * radius
                        );

                        ringLoc.getWorld().spawnParticle(Particle.END_ROD, ringLoc, 1, 0.02, 0.02, 0.02, 0.01);
                        ringLoc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, ringLoc, 2, 0.05, 0.05, 0.05, 0.01);
                    }

                    if (ticks % 5 == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.2f);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        private void arcaneExplosion(Player player, Plugin plugin) {
            Location center = player.getLocation().add(0, 1, 0);

            // Massive arcane explosion
            new BukkitRunnable() {
                double radius = 0;

                @Override
                public void run() {
                    if (radius >= 8) {
                        cancel();
                        return;
                    }

                    // Expanding wave
                    for (int i = 0; i < 32; i++) {
                        double angle = (Math.PI * 2 * i) / 32;
                        Location waveLoc = center.clone().add(
                                Math.cos(angle) * radius,
                                0,
                                Math.sin(angle) * radius
                        );

                        waveLoc.getWorld().spawnParticle(Particle.END_ROD, waveLoc, 3, 0.1, 0.1, 0.1, 0.05);
                        waveLoc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, waveLoc, 5, 0.2, 0.2, 0.2, 0.1);
                    }

                    // Damage entities in current ring
                    for (Entity entity : center.getWorld().getNearbyEntities(center, radius + 1, 3, radius + 1)) {
                        if (entity instanceof LivingEntity target && !entity.equals(player)) {
                            double distance = entity.getLocation().distance(center);
                            if (Math.abs(distance - radius) < 1.5) {
                                target.damage(12.0, player);
                                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
                            }
                        }
                    }

                    player.getWorld().playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.5f, 0.8f);
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
}