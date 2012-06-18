package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathLocationRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigValueType;

public class DeathTpCommand extends BasicCommand
{
    public DeathTpCommand(DeathTpPlus plugin)
    {
        super("Recall");
        setDescription("Teleport to where you last died");
        setUsage("/dtp recall");
        setArgumentRange(0, 0);
        setIdentifiers("recall");
        setPermission("deathtpplus.deathtp");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot recall themselves!");
            return true;
        }

        boolean worldTravel = false;
        Player player = (Player) sender;
        String thisWorld = player.getWorld().getName().toString();

        if (DTPConfig.configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("permissions")) {
            worldTravel = DeathTpPlus.hasPermission(player, "deathtpplus.worldtravel");
        }
        else if (DTPConfig.configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("yes")) {
            worldTravel = true;
        }

        if (!canTp(player))
            return true;

        DeathLocationRecord locationRecord = DeathTpPlus.deathLocationLog.getRecord(player.getName());

        if (locationRecord != null && locationRecord.getWorld() != null) {

            World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
            Location deathLocation = deathWorld.getHighestBlockAt(locationRecord.getLocation().getBlockX(), locationRecord.getLocation().getBlockZ()).getLocation();

            if (!thisWorld.equals(deathWorld.getName())) {
                if (worldTravel) {
                    deathLocation.setWorld(deathWorld);
                    player.teleport(deathLocation);
                    registerTp(player);
                }
                else {
                    player.sendMessage("You do not have the right to travel between worlds!");
                }
            }
            else {
                player.teleport(deathLocation);
                registerTp(player);
            }
        }
        else {
            player.sendMessage("Your last death location is unknown!");
        }

        return true;
    }

    private Boolean canTp(Player player)
    {
        return (!isItemRequired() || hasItem(player)) && hasFunds(player);
    }

    private void registerTp(Player player)
    {
        if (isItemRequired() && hasItem(player)) {
            ItemStack itemInHand = player.getItemInHand();

            if (itemInHand.getAmount() == 1) {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
            }
            else {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                player.setItemInHand(itemInHand);
            }
        }

        if (hasFunds(player)) {
            double deathTpCost = Double.valueOf(DTPConfig.configValues.get(ConfigValueType.DEATHTP_COST).trim()).doubleValue();
            DeathTpPlus.economy.withdrawPlayer(player.getName(), deathTpCost);
            player.sendMessage(String.format("You spent %s to use this command.", DeathTpPlus.economy.format(deathTpCost)));
        }
    }

    private Boolean hasItem(Player player)
    {
        int chargeItem = Integer.parseInt(DTPConfig.configValues.get(ConfigValueType.CHARGE_ITEM));

        // costs item in inventory
        if (chargeItem == player.getItemInHand().getType().getId()) {
            return true;
        }

        player.sendMessage(String.format("You must be holding a(n) %s to use this command!", Material.getMaterial(chargeItem).toString().toLowerCase()));

        return false;
    }

    private Boolean isItemRequired()
    {
        return Integer.parseInt(DTPConfig.configValues.get(ConfigValueType.CHARGE_ITEM)) != 0;
    }

    private Boolean hasFunds(Player player)
    {
        double deathTpCost = Double.valueOf(DTPConfig.configValues.get(ConfigValueType.DEATHTP_COST).trim()).doubleValue();

        if (deathTpCost == 0)
            return true;

        // costs iconomy
        if (DeathTpPlus.economy != null) {
            if (DeathTpPlus.economy.getBalance(player.getName()) > deathTpCost) {
                return true;
            }
            else {
                player.sendMessage(String.format("You need %s coins to use this command!", DeathTpPlus.economy.format(deathTpCost)));
            }
        }
        return false;
    }
}
