package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.StreakRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigValueType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;

public class DTPStreakLog implements Runnable
{
    private static final String STREAK_LOG_FILE = "streak.txt";
    private static final long SAVE_DELAY = 3 * (60 * 20); // 3 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

    private Map<String, StreakRecord> streaks;
    private File streakLogFile;

    public DTPStreakLog(DeathTpPlus plugin)
    {
        streaks = new Hashtable<String, StreakRecord>();
        streakLogFile = new File(DeathTpPlus.dataFolder, STREAK_LOG_FILE);
        if (!streakLogFile.exists()) {
            try {
                streakLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create streak log: " + e.toString());
            }
        }
        load();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
    }

    private void load()
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(streakLogFile));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                StreakRecord streak = new StreakRecord(line);
                streaks.put(streak.getPlayerName(), streak);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read streak log: " + e.toString());
        }
    }

    public synchronized void save()
    {
        try {
            BufferedWriter streakLogWriter = new BufferedWriter(new FileWriter(streakLogFile));

            for (StreakRecord streak : streaks.values()) {
                streakLogWriter.write(streak.toString());
                streakLogWriter.newLine();
            }

            streakLogWriter.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to write streak log: " + e.toString());
        }
    }

    public StreakRecord getRecord(String playerName)
    {
        return streaks.get(playerName);
    }

    public void setRecord(DeathDetail deathDetail)
    {
        String killerName;
        String victimName = deathDetail.getPlayer().getName();

        if (deathDetail.getCauseOfDeath() == DeathEventType.SUICIDE) {
            killerName = deathDetail.getCauseOfDeath().toString();
        }
        else if (deathDetail.getKiller() != null) {
            killerName = deathDetail.getKiller().getName();
        }
        else {
            return;
        }

        StreakRecord killStreakRecord;
        if (streaks.containsKey(killerName)) {
            killStreakRecord = streaks.get(killerName);
            killStreakRecord.incrementKillCount();
            killStreakRecord.updateMultiKillCount(Long.valueOf(DTPConfig.configValues.get(ConfigValueType.MULTIKILL_TIMEWINDOW)));
        }
        else {
            killStreakRecord = new StreakRecord(killerName, 1, new Date(), 1);
            streaks.put(killerName, killStreakRecord);
        }

        StreakRecord deathStreakRecord;
        if (streaks.containsKey(victimName)) {
            deathStreakRecord = streaks.get(victimName);
            deathStreakRecord.incrementDeathCount();
        }
        else {
            deathStreakRecord = new StreakRecord(victimName, -1, new Date(0L), 0);
            streaks.put(victimName, deathStreakRecord);
        }

        // Check to see if we should announce a streak
        if (deathDetail.getCauseOfDeath() != DeathEventType.SUICIDE && DTPConfig.configFlags.get(DTPConfig.ConfigFlagType.SHOW_STREAKS)) {
            // Deaths
            String deathStreakMessage = DTPConfig.getDeathStreakMessage(deathStreakRecord.getCount());
            if (deathStreakMessage != null) {
                Bukkit.getPluginManager().callEvent(new DeathStreakEvent(deathDetail.getPlayer(), deathDetail.getKiller(), deathStreakMessage, deathStreakRecord.getCount()));
            }
            // Multi Kills
            String multiKillMessage = DTPConfig.getMultiKillMessage(killStreakRecord.getMultiKillCount());
            if (multiKillMessage != null && killStreakRecord.isWithinMutiKillTimeWindow(Long.valueOf(DTPConfig.configValues.get(ConfigValueType.MULTIKILL_TIMEWINDOW)))) {
                Bukkit.getPluginManager().callEvent(new KillStreakEvent(deathDetail.getKiller(), deathDetail.getPlayer(), multiKillMessage, killStreakRecord.getMultiKillCount(), true));
            }
            // Kill Streak
            String killStreakMessage = DTPConfig.getKillStreakMessage(killStreakRecord.getCount());
            if (killStreakMessage != null) {
                Bukkit.getPluginManager().callEvent(new KillStreakEvent(deathDetail.getKiller(), deathDetail.getPlayer(), killStreakMessage, killStreakRecord.getCount(), false));
            }
        }
    }

    @Override
    public void run()
    {
        save();
    }
}
