package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.listeners.DTPEntityListener;

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
            DTPEntityListener.showDeathMessage = true;
        }
        else if (args[0].equalsIgnoreCase("off")) {
            DTPEntityListener.showDeathMessage = false;
        }

        return true;
    }
}
