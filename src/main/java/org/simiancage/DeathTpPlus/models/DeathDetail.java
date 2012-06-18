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

    public static DeathDetail getDeathDetailFromDeathEvent(EntityDeathEvent event) {
    	DeathDetail deathDetail = new DeathDetail();
    	
    	deathDetail.setPlayer((Player) event.getEntity());

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) damageEvent).getDamager();
            if (damager instanceof Player) {
                if (((Player) damager).getItemInHand().getType().equals(Material.AIR)) {
                    deathDetail.setCauseOfDeath(DeathEventType.PVP_FISTS);
                }
                else {
                	deathDetail.setCauseOfDeath(DeathEventType.PVP);
                }
                deathDetail.setMurderWeapon(((Player) damager).getItemInHand().getType().toString());
                deathDetail.setKiller((Player) damager);
            }
            else if (damager instanceof Creature) {
                if (damager instanceof Tameable) {
                    if (((Tameable) damager).isTamed()) {
                    	deathDetail.setCauseOfDeath(DeathEventType.PVP_TAMED);
                    	deathDetail.setMurderWeapon(damager.getType().toString());
                        deathDetail.setKiller((Player) ((Tameable) damager).getOwner());
                    }
                }
                else {
                    try {
                    	deathDetail.setCauseOfDeath(DeathEventType.valueOf(damager.getType().toString()));
                    }
                    catch (IllegalArgumentException e) {
                    	deathDetail.setCauseOfDeath(DeathEventType.UNKNOWN);
                    }
                }
            }
            else if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                	deathDetail.setCauseOfDeath(DeathEventType.PVP);
                	deathDetail.setMurderWeapon(((Projectile) damager).toString().replace("Craft", ""));
                    deathDetail.setKiller((Player) ((Projectile) damager).getShooter());
                }
                else if (((Projectile) damager).getShooter() instanceof Creature) {
                    try {
                    	deathDetail.setCauseOfDeath((DeathEventType.valueOf( ((Projectile) damager).getShooter().getType().toString())));
                    }
                    catch (IllegalArgumentException e) {
                    	deathDetail.setCauseOfDeath(DeathEventType.UNKNOWN);
                    }
                }
                else {
                	deathDetail.setCauseOfDeath(DeathEventType.DISPENSER);
                }
            }
            else if (damager instanceof TNTPrimed) {
            	deathDetail.setCauseOfDeath(DeathEventType.BLOCK_EXPLOSION);
            }
            else {
                DeathTpPlus.logger.info("Unknown enitity damager" + damager);
            }
        }
        else if (damageEvent != null) {
        	deathDetail.setCauseOfDeath(DeathEventType.valueOf(damageEvent.getCause().toString()));
        }

        if (deathDetail.getCauseOfDeath() == null) {
        	deathDetail.setCauseOfDeath(DeathEventType.UNKNOWN);
            deathDetail.setMurderWeapon("unknown");
        }
        
    	return deathDetail;
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
        return causeOfDeath == DeathEventType.PVP || causeOfDeath == DeathEventType.PVP_FISTS || causeOfDeath == DeathEventType.PVP_TAMED;
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
