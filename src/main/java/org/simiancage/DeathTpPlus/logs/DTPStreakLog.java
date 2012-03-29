package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.StreakRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigValueType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;

public class DTPStreakLog
{
    private static final String STREAK_LOG_FILE = "streak.txt";

    private File streakLogFile;

    public DTPStreakLog()
    {
        streakLogFile = new File(DeathTpPlus.dataFolder, STREAK_LOG_FILE);
        if (!streakLogFile.exists()) {
            try {
                streakLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create streak log: " + e.toString());
            }
        }
    }

    public StreakRecord getRecord(String playerName)
    {
        StreakRecord streak = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                streak = new StreakRecord(line);
                if (playerName.equalsIgnoreCase(streak.getPlayerName())) {
                    return streak;
                }
                else {
                    streak = null;
                }
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read streak log: " + e.toString());
        }

        return streak;
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

        List<StreakRecord> streakList = new ArrayList<StreakRecord>();

        StreakRecord killStreakRecord = null;
        StreakRecord deathStreakRecord = null;

        // read the file
        try {
            BufferedReader streakLogReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = streakLogReader.readLine()) != null) {
                StreakRecord streak = new StreakRecord(line);
                if (victimName.equalsIgnoreCase(streak.getPlayerName())) {
                    streak.setCount(streak.getCount() > 0 ? -1 : streak.getCount() - 1);
                    deathStreakRecord = streak;
                }
                if (killerName.equalsIgnoreCase(streak.getPlayerName())) {
                    streak.setCount(streak.getCount() < 0 ? 1 : streak.getCount() + 1);
                    streak.updateMultiKillCount(Long.valueOf(DTPConfig.configValues.get(ConfigValueType.MULTIKILL_TIMEWINDOW)));
                    killStreakRecord = streak;
                }
                streakList.add(streak);
            }

            streakLogReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read streak log: " + e.toString());
        }

        if (killStreakRecord == null) {
            killStreakRecord = new StreakRecord(killerName, 1, new Date(), 1);
            streakList.add(killStreakRecord);
        }

        if (deathStreakRecord == null) {
            deathStreakRecord = new StreakRecord(victimName, -1, new Date(0L), 0);
            streakList.add(deathStreakRecord);
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

        // Write streaks to file
        try {
            BufferedWriter streakLogWriter = new BufferedWriter(new FileWriter(streakLogFile));

            for (StreakRecord streak : streakList) {
                streakLogWriter.write(streak.toString());
                streakLogWriter.newLine();
            }

            streakLogWriter.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to write streak log: " + e.toString());
        }
    }
}
