package org.simiancage.DeathTpPlus.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DeathLocationRecord
{
	private String playerName;
	private Location location;
	private String worldName;

	public DeathLocationRecord()
	{
	}

	public DeathLocationRecord(Player player)
	{
		this.playerName = player.getName();
		this.location = player.getLocation();
		this.worldName = player.getWorld().getName();
	}

	public DeathLocationRecord(String record)
	{
		if (record != null) {
			String[] parts = record.split(":");

			if (parts.length == 5) {
				playerName = parts[0];
				location = new Location(null, Double.valueOf(parts[1]), Double.valueOf(parts[2]), Double.valueOf(parts[3]));
				worldName = parts[4];
			}
		}
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public String getWorldName()
	{
		return worldName;
	}

	public void setWorldName(String worldName)
	{
		this.worldName = worldName;
	}

	public World getWorld()
	{
		return Bukkit.getWorld(worldName);
	}

	public void setWorld(World world)
	{
		if (world != null) {
			setWorldName(world.getName());
		}
		else {
			setWorldName(null);
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s:%f:%f:%f:%s", playerName, location.getX(), location.getY(), location.getZ(), worldName);
	}
}
