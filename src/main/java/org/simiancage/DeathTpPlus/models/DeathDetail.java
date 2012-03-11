package org.simiancage.DeathTpPlus.models;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;

public class DeathDetail
{
    private Player player;
    private DeathEventType causeOfDeath;
    private Player killer;
    private String murderWeapon;

    public DeathDetail()
    {
    }

    public DeathDetail(EntityDeathEvent event)
    {
        player = (Player) event.getEntity();

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) damageEvent).getDamager();
            if (damager instanceof Player) {
                if (((Player) damager).getItemInHand().getType().equals(Material.AIR)) {
                    causeOfDeath = DeathEventType.PVP_FISTS;
                }
                else {
                    causeOfDeath = DeathEventType.PVP;
                }
                murderWeapon = ((Player) damager).getItemInHand().getType().toString();
                killer = (Player) damager;
            }
            else if (damager instanceof Creature) {
                if (damager instanceof Tameable) {
                    if (((Tameable) damager).isTamed()) {
                        causeOfDeath = DeathEventType.PVP_TAMED;
                        murderWeapon = damager.getType().toString();
                        killer = (Player) ((Tameable) damager).getOwner();
                    }
                }
                else {
                    try {
                        causeOfDeath = DeathEventType.valueOf(damager.getType().toString());
                    }
                    catch (IllegalArgumentException e) {
                        causeOfDeath = DeathEventType.UNKNOWN;
                    }
                }
            }
            else if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                    causeOfDeath = DeathEventType.PVP;
                    murderWeapon = ((Projectile) damager).toString().replace("Craft", "");
                    killer = (Player) ((Projectile) damager).getShooter();
                }
                else if (((Projectile) damager).getShooter() instanceof Creature) {
                    try {
                        causeOfDeath = DeathEventType.valueOf( ((Projectile) damager).getShooter().getType().toString());
                    }
                    catch (IllegalArgumentException e) {
                        causeOfDeath = DeathEventType.UNKNOWN;
                    }
                }
                else {
                    causeOfDeath = DeathEventType.DISPENSER;
                }
            }
            else if (damager instanceof TNTPrimed) {
                causeOfDeath = DeathEventType.BLOCK_EXPLOSION;
            }
            else {
                DeathTpPlus.logger.info("Unknown enitity damager" + damager);
            }
        }
        else if (damageEvent != null) {
            causeOfDeath = DeathEventType.valueOf(damageEvent.getCause().toString());
        }

        if (causeOfDeath == null) {
            causeOfDeath = DeathEventType.UNKNOWN;
            murderWeapon = "unknown";
        }
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public DeathEventType getCauseOfDeath()
    {
        return causeOfDeath;
    }

    public void setCauseOfDeath(DeathEventType causeOfDeath)
    {
        this.causeOfDeath = causeOfDeath;
    }

    public Player getKiller()
    {
        return killer;
    }

    public void setKiller(Player killer)
    {
        this.killer = killer;
    }

    public String getMurderWeapon()
    {
        return toCamelCase(murderWeapon);
    }

    public void setMurderWeapon(String murderWeapon)
    {
        this.murderWeapon = murderWeapon;
    }

    public Boolean isPVPDeath()
    {
        return causeOfDeath== DeathEventType.PVP || causeOfDeath == DeathEventType.PVP_FISTS || causeOfDeath == DeathEventType.PVP_TAMED;
    }

    private static String toCamelCase(String rawItemName)
    {
        String[] rawItemNameParts = rawItemName.split("_");
        String itemName = "";

        for (String itemNamePart : rawItemNameParts) {
            itemName = itemName + " " + toProperCase(itemNamePart);
        }

        if (itemName.trim().equals("Air")) {
            itemName = "Fists";
        }

        if (itemName.trim().equals("Bow")) {
            itemName = "Bow & Arrow";
        }

        return itemName.trim();
    }

    private static String toProperCase(String str)
    {
        if (str.length() < 1) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
