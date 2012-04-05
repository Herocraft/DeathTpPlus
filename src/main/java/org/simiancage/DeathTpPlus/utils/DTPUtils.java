package org.simiancage.DeathTpPlus.utils;

public class DTPUtils
{
    public static String convertColorCodes(String msg)
    {
    	return msg.replaceAll("(?i)&([a-fklmnor0-9])", "§$1");
    }

    public static String removeColorCodes(String msg)
    {
    	return msg.replaceAll("(?i)§[a-fklmnor0-9]", "");
    }
}
