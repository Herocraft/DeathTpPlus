package org.simiancage.DeathTpPlus.utils;

public class DTPUtils
{
    public static String convertColorCodes(String msg)
    {
        return msg.replaceAll("&([0-9a-fA-F])", "§$1");
    }

    public static String removeColorCodes(String msg)
    {
        return msg.replaceAll("§[0-9a-fA-F]", "");
    }
}
