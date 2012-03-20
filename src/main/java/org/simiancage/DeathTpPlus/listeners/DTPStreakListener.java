package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.SpoutManager;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.utils.DTPConfig;

public class DTPStreakListener implements Listener {

    private final DeathTpPlus plugin;
    private static final int SOUND_DISTANCE = 50;

    public DTPStreakListener(DeathTpPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeathStreakEvent(DeathStreakEvent event) {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKillStreakEvent(KillStreakEvent event) {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
        final Location location = event.getVictim().getLocation();
        if (DTPConfig.configFlags.get(DTPConfig.ConfigFlagType.PLAY_SOUNDS) && DeathTpPlus.spout != null) {
            if (event.isMultiKill()) {
                // Play our multikill sound
                playMultiKillSound(event);
                
                // Schedule the killstreak message for slightly after in case the player has a kill-streak;
                final Integer kills = DeathTpPlus.streakLog.getRecord(event.getPlayer().getName()).getCount();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playKillStreakSound(kills, location);
                    }
                }, 40);
            } else {
                playKillStreakSound(event.getKills(), location);
            }
        }
    }

    private void playMultiKillSound(KillStreakEvent event) {
        String soundName = DTPConfig.getMultiKillSound(event.getKills());
        if (soundName == null) {
            return;
        }
        String url = DTPConfig.getSoundUrl() + soundName + DTPConfig.getSoundFormat();
        SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, event.getVictim().getLocation(), SOUND_DISTANCE);
    }

    private void playKillStreakSound(Integer kills, Location loc) {
        String soundName = DTPConfig.getKillStreakSound(kills);
        if (soundName == null) {
            return;
        }
        String url = DTPConfig.getSoundUrl() + soundName + DTPConfig.getSoundFormat();
        SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, loc, SOUND_DISTANCE);
    }
}
