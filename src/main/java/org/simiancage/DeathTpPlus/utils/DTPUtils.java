package org.simiancage.DeathTpPlus.utils;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public class DTPUtils
{
    public static String convertColorCodes(String msg)
    {
        return msg.replaceAll("&([0-9a-fA-F])", "§$1");
    }

    public static String removeColorCodes(String msg)
    {
        return msg.replaceAll("§[0-9a-fA-F]", "");
    }

    public static CreatureType getCreatureType(Entity entity)
    {
        if (entity instanceof Blaze)
            return CreatureType.BLAZE;
        if (entity instanceof CaveSpider)
            return CreatureType.CAVE_SPIDER;
        if (entity instanceof Chicken)
            return CreatureType.CHICKEN;
        if (entity instanceof Cow)
            return CreatureType.COW;
        if (entity instanceof Creeper)
            return CreatureType.CREEPER;
        if (entity instanceof EnderDragon)
            return CreatureType.ENDER_DRAGON;
        if (entity instanceof Enderman)
            return CreatureType.ENDERMAN;
        if (entity instanceof Ghast)
            return CreatureType.GHAST;
        if (entity instanceof Giant)
            return CreatureType.GIANT;
        if (entity instanceof MagmaCube)
            return CreatureType.MAGMA_CUBE;
        if (entity instanceof MushroomCow)
            return CreatureType.MUSHROOM_COW;
        if (entity instanceof Pig)
            return CreatureType.PIG;
        if (entity instanceof PigZombie)
            return CreatureType.PIG_ZOMBIE;
        if (entity instanceof Sheep)
            return CreatureType.SHEEP;
        if (entity instanceof Skeleton)
            return CreatureType.SKELETON;
        if (entity instanceof Slime)
            return CreatureType.SLIME;
        if (entity instanceof Snowman)
            return CreatureType.SNOWMAN;
        if (entity instanceof Silverfish)
            return CreatureType.SILVERFISH;
        if (entity instanceof Spider)
            return CreatureType.SPIDER;
        if (entity instanceof Squid)
            return CreatureType.SQUID;
        if (entity instanceof Villager)
            return CreatureType.VILLAGER;
        if (entity instanceof Zombie)
            return CreatureType.ZOMBIE;
        if (entity instanceof Wolf)
            return CreatureType.WOLF;

        return null;
    }
}
