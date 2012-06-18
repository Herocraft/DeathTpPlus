package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class KillsCommand extends BasicCommand
{
    public KillsCommand(DeathTpPlus plugin)
    {
        super("Kills");
        setDescription("Display player kill counts");
        setUsage("/dtp kills §9[killer] [victim]");
        setArgumentRange(0, 2);
        setIdentifiers("kills");
        setPermission("deathtpplus.kills");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        int total;

        switch (args.length) {
            case 0:
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    total = DeathTpPlus.deathLog.getTotalByType(player.getName(), DeathRecordType.kill);
                    if (total >= 0) {
                        sender.sendMessage(String.format("You have %d PVP kill(s)", total));
                    }
                    else {
                        sender.sendMessage("No record found.");
                    }
                }
                else {
                    sender.sendMessage("Console cannot display deaths for themselves!");
                }
                break;
            case 1:
                total = DeathTpPlus.deathLog.getTotalByType(args[0], DeathRecordType.kill);
                if (total >= 0) {
                    sender.sendMessage(String.format("%s has %d PVP kill(s)", args[0], total));
                }
                else {
                    sender.sendMessage("No record found.");
                }
                break;
            case 2:
                DeathRecord record = DeathTpPlus.deathLog.getRecordByType(args[0], args[1], DeathRecordType.kill);
                if (record != null) {
                    sender.sendMessage(String.format("%s killed %s %d time(s)", args[0], args[1], record.getCount()));
                }
                break;
        }

        return true;
    }
}
