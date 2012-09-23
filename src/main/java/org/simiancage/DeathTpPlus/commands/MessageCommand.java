package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigFlagType;

public class MessageCommand extends BasicCommand
{
    public MessageCommand(DeathTpPlus plugin)
    {
        super("Message");
        setDescription("Turn death messages on/off");
        setUsage("/dtp msg §9<on|off>");
        setArgumentRange(1, 1);
        setIdentifiers("msg");
        setPermission("deathtpplus.msg");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (args[0].equalsIgnoreCase("on")) {
            DTPConfig.configFlags.put(ConfigFlagType.SHOW_DEATHNOTIFY, true);
        }
        else if (args[0].equalsIgnoreCase("off")) {
            DTPConfig.configFlags.put(ConfigFlagType.SHOW_DEATHNOTIFY, false);
        }

        return true;
    }
}
