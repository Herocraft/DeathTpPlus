package org.simiancage.DeathTpPlus.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;

public class DTPConfig
{
    public static enum DeathEventType {
        BLAZE, BLOCK_EXPLOSION, CAVE_SPIDER, CONTACT, CREEPER, DISPENSER, DROWNING, ENDER_DRAGON, ENDERMAN, FALL, FIRE, FIRE_TICK,
        GHAST, GIANT, IRON_GOLEM, LAVA, LAVA_SLIME, LIGHTNING, MAGIC, MAGMA_CUBE, MONSTER, PIG_ZOMBIE, POISON, PVP, PVP_FISTS, PVP_TAMED, SILVERFISH,
        SKELETON, SLIME, SPIDER, SQUID, STARVATION, SUFFOCATION, SUICIDE, UNKNOWN, VILLAGER, VOID, WITCH, WITHER, WITHER_SKELETON, WOLF, ZOMBIE, ZOMBIE_VILLAGER
    };

    public static enum ConfigValueType {
        CHARGE_ITEM, DEATHTP_COST, DEATHTP_TAG, ALLOW_WORLDTRAVEL, MULTIKILL_TIMEWINDOW
    };

    public static enum ConfigFlagType {
        SHOW_DEATHNOTIFY, ALLOW_DEATHTP, SHOW_STREAKS, SHOW_SIGN, ALLOW_DEATHLOG, PLAY_SOUNDS, VERBOSE
    }

    private static Map<DeathEventType, List<String>> deathMessages = new HashMap<DeathEventType, List<String>>();
    private static List<String> deathStreakMessages;
    private static List<String> killStreakMessages;
    private static List<String> multiKillMessages;
    private static List<String> killStreakSounds;
    private static List<String> multiKillSounds;

    public static final Map<ConfigValueType, String> configValues = new HashMap<ConfigValueType, String>();
    public static final Map<ConfigFlagType, Boolean> configFlags = new HashMap<ConfigFlagType, Boolean>();
    public static boolean worldTravel = false;

    private static final String CONFIG_FILE = "config.yml";
    private static final String DEFAULT_DEATH_MESSAGE = "%n died from unknown causes";
    private static File configFile;
    private static Random random = new Random();
    private static String soundUrl;
    private static String soundFormat;

    private FileConfiguration configuration;
    private DeathTpPlus plugin;

    public DTPConfig(DeathTpPlus plugin)
    {
        this.plugin = plugin;

        configFile = new File(DeathTpPlus.DATA_FOLDER, CONFIG_FILE);
        configuration = getConfig(configFile);

        // Configuration nodes
        for (ConfigValueType configNode : ConfigValueType.values()) {
            configValues.put(configNode, configuration.getString(configNode.toString().toLowerCase().replace("_", "-"), ""));
        }

        for (ConfigFlagType configNode : ConfigFlagType.values()) {
            configFlags.put(configNode, Boolean.valueOf(configuration.getString(configNode.toString().toLowerCase().replace("_", "-"), "")));
        }

        // Death Event nodes
        for (DeathEventType deathEventType : DeathEventType.values()) {
            deathMessages.put(deathEventType, configuration.getStringList(mapTypeToNodeName(deathEventType)));
            if (configFlags.get(ConfigFlagType.VERBOSE)) {
                DeathTpPlus.logger.info(deathMessages.get(deathEventType).size() + " messages loaded for " + deathEventType);
            }
        }

        // Kill Streak nodes
        killStreakMessages = configuration.getStringList("killstreak");
        DeathTpPlus.logger.info(killStreakMessages.size() + " messages loaded for killstreak");

        // Death Streak nodes
        deathStreakMessages = configuration.getStringList("deathstreak");
        DeathTpPlus.logger.info(deathStreakMessages.size() + " messages loaded for deathstreak");

        multiKillMessages = configuration.getStringList("multikill");
        DeathTpPlus.logger.info(multiKillMessages.size() + " messages loaded for multikill");

        // Sound nodes
        killStreakSounds = configuration.getStringList("sounds.killstreaks");
        DeathTpPlus.logger.info(killStreakSounds.size() + " sounds loaded for killstreaks");

        multiKillSounds = configuration.getStringList("sounds.multikills");
        DeathTpPlus.logger.info(multiKillSounds.size() + " sounds loaded for multikill");

        soundUrl = configuration.getString("sounds.url");
        soundFormat = configuration.getString("sounds.format");

        if (configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("yes") || configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("no")
                || configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("permissions")) {
            DeathTpPlus.logger.info("allow-wordtravel is: " + configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL));
        }
        else {
            DeathTpPlus.logger.warning("Wrong allow-worldtravel value of " + configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL) + ". Defaulting to NO!");
        }
    }

    private FileConfiguration getConfig(File file)
    {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                InputStream inputStream = DTPConfig.class.getResourceAsStream(File.separator + file.getName());
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, length);

                inputStream.close();
                outputStream.close();

                DeathTpPlus.logger.info("Default config created successfully!");
            }
            catch (Exception e) {
                DeathTpPlus.logger.warning("Default config could not be created!");
            }
        }

        return plugin.getConfig();
    }

    private String mapTypeToNodeName(DeathEventType deathEventType)
    {
        if (deathEventType == DeathEventType.CAVE_SPIDER) {
            return "cavespider";
        }
        else if (deathEventType == DeathEventType.PIG_ZOMBIE) {
            return "pigzombie";
        }

        String nodeName = deathEventType.toString().toLowerCase();
        if (!deathEventType.toString().equals("BLOCK_EXPLOSION") && !deathEventType.toString().equals("FIRE_TICK")) {
            nodeName = nodeName.replace("_", "-");
        }

        return nodeName;
    }

    public static String getDeathMessage(DeathDetail deathDetail)
    {
        String message;
        List<String> messages = deathMessages.get(deathDetail.getCauseOfDeath());

        if (messages == null) {
            message = DEFAULT_DEATH_MESSAGE;
        }
        else {
            message = messages.get(random.nextInt(messages.size())).replace("%n", deathDetail.getPlayer().getName());
        }

        if (deathDetail.isPVPDeath()) {
            message = message.replace("%i", deathDetail.getMurderWeapon()).replace("%a", deathDetail.getKiller().getName());
        }

        return DTPUtils.convertColorCodes(message);
    }

    public static String getDeathStreakMessage(Integer deathCount)
    {
        for (String message : deathStreakMessages) {
            String parts[] = message.split(":");
            if (Integer.parseInt(parts[0]) == -deathCount) {
                return DTPUtils.convertColorCodes(parts[1]);
            }
        }

        return null;
    }

    public static String getKillStreakMessage(Integer killCount)
    {
        for (String message : killStreakMessages) {
            String parts[] = message.split(":");
            if (Integer.parseInt(parts[0]) == killCount) {
                return DTPUtils.convertColorCodes(parts[1]);
            }
        }

        return null;
    }

    public static String getMultiKillMessage(Integer killCount)
    {
        for (String message : multiKillMessages) {
            String parts[] = message.split(":");
            if (Integer.parseInt(parts[0]) == killCount) {
                return DTPUtils.convertColorCodes(parts[1]);
            }
        }

        return null;
    }

    public static String getKillStreakSound(Integer killCount) {
        if (killCount == null || killStreakSounds == null) {
            return null;
        }
        for (String message : killStreakSounds) {
            String parts[] = message.split(":");
            if (Integer.parseInt(parts[0]) == killCount) {
                return DTPUtils.convertColorCodes(parts[1]);
            }
        }

        return null;
    }

    public static String getMultiKillSound(Integer killCount) {
        if (killCount == null || multiKillSounds == null) {
            return null;
        }
        for (String message : multiKillSounds) {
            String parts[] = message.split(":");
            if (Integer.parseInt(parts[0]) == killCount) {
                return DTPUtils.convertColorCodes(parts[1]);
            }
        }

        return null;
    }

    public static String getSoundUrl() {
        return soundUrl;
    }

    public static String getSoundFormat() {
        return soundFormat;
    }
}
