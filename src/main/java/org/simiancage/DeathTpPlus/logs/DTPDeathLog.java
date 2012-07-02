package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class DTPDeathLog implements Runnable
{
    private static final String DEATH_LOG_FILE = "deathlog.txt";
    private static final String DEATH_LOG_TMP = "deathlog.tmp";
    private static final String CHARSET = "UTF-8";
    private static final long SAVE_DELAY = 1 * (60 * 20); // 1 minutes
    private static final long SAVE_PERIOD = 3 * (60 * 20); // 3 minutes

    private Map<String, DeathRecord> deaths;
    private File deathLogFile;

    public DTPDeathLog(DeathTpPlus plugin)
    {
        deaths = new Hashtable<String, DeathRecord>();
        deathLogFile = new File(DeathTpPlus.dataFolder, DEATH_LOG_FILE);
        if (!deathLogFile.exists()) {
            try {
                deathLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create death log: " + e.toString());
            }
        }
        load();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, SAVE_DELAY, SAVE_PERIOD);
    }

    private void load()
    {
        try {
            BufferedReader deathLogReader = new BufferedReader(new InputStreamReader(new FileInputStream(deathLogFile), CHARSET));

            String line = null;
            while ((line = deathLogReader.readLine()) != null) {
                DeathRecord deathRecord = new DeathRecord(line);
                deaths.put(deathRecord.getKey(), deathRecord);
            }

            deathLogReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to edit death log: " + e.toString());
        }
    }

    public synchronized void save()
    {
        File tmpDeathLogFile = new File(DeathTpPlus.dataFolder, DEATH_LOG_TMP);

        try {
            BufferedWriter tmpDeathLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpDeathLogFile), CHARSET));

            for (DeathRecord deathRecord : deaths.values()) {
                tmpDeathLogWriter.write(deathRecord.toString());
                tmpDeathLogWriter.newLine();
            }

            tmpDeathLogWriter.close();
            if (!tmpDeathLogFile.renameTo(deathLogFile)) {
                throw new Exception("Failed to rename death log.");
            }
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Failed to edit death log: " + e.toString());
        }
    }

    public Hashtable<String, Integer> getTotalsByType(DeathRecordType type)
    {
        Hashtable<String, Integer> totals = new Hashtable<String, Integer>();

        for (DeathRecord record : getRecords()) {
            if (record.getType() == type) {
                if (totals.containsKey(record.getPlayerName())) {
                    totals.put(record.getPlayerName(), totals.get(record.getPlayerName()) + 1);
                }
                else {
                    totals.put(record.getPlayerName(), Integer.valueOf(1));
                }
            }
        }

        return totals;
    }

    public int getTotalByType(String playerName, DeathRecordType type)
    {
        List<DeathRecord> records = getRecords(playerName);
        int totalDeaths = 0;

        for (DeathRecord record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type) {
                totalDeaths += record.getCount();
            }
        }

        return totalDeaths;
    }

    public DeathRecord getRecordByType(String playerName, String eventName, DeathRecordType type)
    {
        List<DeathRecord> records = getRecords(playerName);

        for (DeathRecord record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type && record.getEventName().equalsIgnoreCase(eventName)) {
                return record;
            }
        }

        return null;
    }

    public List<DeathRecord> getRecordsByType(String playerName, DeathRecordType type)
    {
        List<DeathRecord> records = new ArrayList<DeathRecord>();

        for (DeathRecord record : getRecords(playerName)) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type) {
                records.add(record);
            }
        }

        return records;
    }

    private List<DeathRecord> getRecords(String playerName)
    {
        List<DeathRecord> records = new ArrayList<DeathRecord>();

        for (DeathRecord deathRecord : getRecords()) {
            if (playerName.equalsIgnoreCase(deathRecord.getPlayerName())) {
                records.add(deathRecord);
            }
        }

        return records;
    }

    private Collection<DeathRecord> getRecords()
    {
        return deaths.values();
    }

    public void setRecord(DeathDetail deathDetail)
    {
        if (deathDetail.isPVPDeath()) {
            setRecord(deathDetail.getKiller().getName(), DeathRecordType.kill, deathDetail.getPlayer().getName());
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getKiller().getName());
        }
        else {
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getCauseOfDeath().toString());
        }
    }

    private void setRecord(String playerName, DeathRecordType type, String eventName)
    {
        DeathRecord deathRecord = new DeathRecord(playerName, type, eventName, 1);
        if (deaths.containsKey(deathRecord.getKey())) {
            deaths.get(deathRecord.getKey()).incrementCount();
        }
        else {
            deaths.put(deathRecord.getKey(), deathRecord);
        }
    }

    @Override
    public void run()
    {
        save();
    }
}
