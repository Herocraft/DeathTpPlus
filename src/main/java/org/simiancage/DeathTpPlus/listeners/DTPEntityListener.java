package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigFlagType;
import org.simiancage.DeathTpPlus.utils.DTPUtils;

public class DTPEntityListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        DeathDetail deathDetail = DeathDetail.getDeathDetailFromDeathEvent(event);

        if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHTP)) {
            DeathTpPlus.deathLocationLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS)) {
            DeathTpPlus.streakLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {
            DeathTpPlus.deathLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_DEATHNOTIFY)) {

            String deathMessage = DTPConfig.getDeathMessage(deathDetail);

            if (event instanceof PlayerDeathEvent) {
                ((PlayerDeathEvent) event).setDeathMessage(deathMessage);
            }

            // CraftIRC
            if (DeathTpPlus.craftIRCEndPoint != null) {
                DeathTpPlus.craftIRCEndPoint.sendMessage(DTPUtils.removeColorCodes(deathMessage));
            }
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_SIGN)) {
            // place sign
            Block signBlock = deathDetail.getPlayer().getWorld().getBlockAt(deathDetail.getPlayer().getLocation());
            signBlock.setType(Material.SIGN_POST);

            if (signBlock.getState() instanceof Sign) {
                Sign sign = (Sign) signBlock.getState();
                sign.setLine(0, "[RIP]");
                sign.setLine(1, deathDetail.getPlayer().getName());
                sign.setLine(2, "Died by");
                if (deathDetail.isPVPDeath()) {
                    sign.setLine(3, deathDetail.getKiller().getName());
                }
                else {
                    sign.setLine(3, deathDetail.getCauseOfDeath().toString().substring(0, 1) + deathDetail.getCauseOfDeath().toString().substring(1).toLowerCase());
                }
            }
        }
    }
}
