package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.DeathLocationRecord;

public class DTPDeathLocationLog implements Runnable
{
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final long SAVE_DELAY = 5 * (60 * 20); // 5 minutes
    private static final long SAVE_PERIOD = 5 * (60 * 20); // 5 minutes

    private Map<String, DeathLocationRecord> deathLocations;
    private File deathLocationLogFile;

    public DTPDeathLocationLog(DeathTpPlus plugin)
    {
        deathLocations = new Hashtable<String, DeathLocationRecord>();
        deathLocationLogFile = new File(DeathTpPlus.dataFolder, LOCATION_LOG_FILE);
        if (!deathLocationLogFile.exists()) {
            try {
                deathLocationLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create death location log: " + e.toString());
            }
        }
        load();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
    }

    private void load()
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(deathLocationLogFile));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                DeathLocationRecord deathLocation = new DeathLocationRecord(line);
                deathLocations.put(deathLocation.getPlayerName(), deathLocation);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read death location log: " + e.toString());
        }
    }

    private void save()
    {
        try {
            BufferedWriter deathLocationLogWriter = new BufferedWriter(new FileWriter(deathLocationLogFile));

            for (DeathLocationRecord deathLocation : deathLocations.values()) {
                deathLocationLogWriter.write(deathLocation.toString());
                deathLocationLogWriter.newLine();
            }

            deathLocationLogWriter.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to write death location log: " + e.toString());
        }
    }

    public DeathLocationRecord getRecord(String playerName)
    {
        return deathLocations.get(playerName);
    }

    public void setRecord(DeathDetail deathDetail)
    {
        deathLocations.put(deathDetail.getPlayer().getName(), new DeathLocationRecord(deathDetail.getPlayer()));
    }

    @Override
    public void run()
    {
        save();
    }
}
