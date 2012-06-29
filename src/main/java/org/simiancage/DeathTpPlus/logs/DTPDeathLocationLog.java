package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.DeathLocationRecord;

public class DTPDeathLocationLog implements Runnable
{
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final String CHARSET = "UTF-8";
    private static final long SAVE_DELAY = 2 * (60 * 20); // 2 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(deathLocationLogFile), CHARSET));
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

    public synchronized void save()
    {
        try {
            BufferedWriter deathLocationLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deathLocationLogFile), CHARSET));

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
