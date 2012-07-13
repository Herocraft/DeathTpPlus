package org.simiancage.DeathTpPlus.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class ReportCommand extends BasicCommand
{
    private static final int CMDS_PER_PAGE = 8;

    public ReportCommand(DeathTpPlus plugin)
    {
        super("Report");
        setDescription("Display your kill/death report");
        setUsage("/dtp report §9<kills|deaths> §8[page#]");
        setArgumentRange(1, 2);
        setIdentifiers("report");
        setPermission("deathtpplus.report");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        int page = 0;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]) - 1;
            }
            catch (NumberFormatException e) {
            }
        }

        if (sender instanceof Player) {
            List<DeathRecord> records = null;
            if (args[0].equalsIgnoreCase("kills")) {
                records = DeathTpPlus.deathLog.getRecordsByType(((Player) sender).getName(), DeathRecordType.kill);
            }
            else if (args[0].equalsIgnoreCase("deaths")) {
                records = DeathTpPlus.deathLog.getRecordsByType(((Player) sender).getName(), DeathRecordType.death);
            }

            if (records == null || records.isEmpty()) {
                sender.sendMessage("No records found.");
            }
            else {
                Collections.sort(records, new Comparator<DeathRecord>()
                {
                    public int compare(DeathRecord record1, DeathRecord record2)
                    {
                        return record1.getEventName().compareToIgnoreCase(record2.getEventName());
                    }
                });

                int numPages = records.size() / CMDS_PER_PAGE;
                if (records.size() % CMDS_PER_PAGE != 0) {
                    numPages++;
                }
                if (page >= numPages || page < 0) {
                    page = 0;
                }

                sender.sendMessage("§c-----[ " + "§fDeathTpPlus Report <" + (page + 1) + "/" + numPages + ">§c ]-----");
                int start = page * CMDS_PER_PAGE;
                int end = start + CMDS_PER_PAGE;
                if (end > records.size()) {
                    end = records.size();
                }
                for (int c = start; c < end; c++) {
                    DeathRecord record = records.get(c);
                    sender.sendMessage(String.format("%s: %s", record.getEventName(), record.getCount()));
                }
            }
        }
        else {
            sender.sendMessage("Console cannot display reports for themselves!");
        }

        return true;
    }
}
