package me.Destro168.FC_Bans;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FrozenPlayer 
{
	private Player player;
	private boolean isFrozen;
	Location frozenLocation;
	private int moveTID;
	
	public String getPlayerName() { return player.getName(); }
	public boolean getIsFrozen() { return isFrozen; }
	
	public FrozenPlayer(Player player_)
	{
		//Store player.
		player = player_;
		
		//By default not frozen.
		isFrozen = false;
		
		//Freeze the player.
		startFreeze();
	}
	
	private void startFreeze()
	{
		PunishmentManager pm = new PunishmentManager(player.getName());
		Location yLoc;
		World playerWorld = player.getWorld();
		int fallLoopBreak = 0;
		int playerY = 0;
		
		//If the player is frozen then we return because we can't help frozen people.
		if (pm.isFrozen() == false)
			return;
		
		//Set variables.
		isFrozen = true;
		
		//We want to set the y position to the center of a block on the ground.
		yLoc =  player.getLocation();
		
		while (playerWorld.getBlockAt(yLoc).getType() == Material.AIR || playerWorld.getBlockAt(yLoc).getType() == Material.TRIPWIRE || playerWorld.getBlockAt(yLoc).getType() == Material.TRIPWIRE_HOOK || playerWorld.getBlockAt(yLoc).getType() == Material.PAINTING
			|| playerWorld.getBlockAt(yLoc).getType() == Material.TORCH || playerWorld.getBlockAt(yLoc).getType() == Material.LADDER || playerWorld.getBlockAt(yLoc).getType() == Material.LEVER
			|| playerWorld.getBlockAt(yLoc).getType() == Material.LONG_GRASS || playerWorld.getBlockAt(yLoc).getType() == Material.REDSTONE_WIRE || playerWorld.getBlockAt(yLoc).getType() == Material.REDSTONE_TORCH_ON
			|| playerWorld.getBlockAt(yLoc).getType() == Material.REDSTONE_TORCH_OFF || playerWorld.getBlockAt(yLoc).getType() == Material.RED_MUSHROOM || playerWorld.getBlockAt(yLoc).getType() == Material.RED_ROSE
			|| playerWorld.getBlockAt(yLoc).getType() == Material.YELLOW_FLOWER || playerWorld.getBlockAt(yLoc).getType() == Material.BROWN_MUSHROOM)
		{
			//Move it down by one
			yLoc.setY(yLoc.getY() - 1);
			
			fallLoopBreak++;
			
			if (fallLoopBreak > 256)
				return;
		}
		
		//Set the new location equal to the found y location + 1. (so to not set to the block.
		playerY = (int) yLoc.getY() + 1;
		
		//Store new location to freeze at.
		frozenLocation = new Location(playerWorld, player.getLocation().getX(), playerY, player.getLocation().getZ(),
				player.getLocation().getYaw(), player.getLocation().getPitch());
		
		moveTID = Bukkit.getScheduler().scheduleSyncRepeatingTask(FC_Bans.plugin, new Runnable() 
		{
			public void run()
			{
				try
				{
					if (player.getLocation().getBlockX() != frozenLocation.getBlockX())
						player.teleport(frozenLocation);
					else if (player.getLocation().getBlockY() != frozenLocation.getBlockY())
						player.teleport(frozenLocation);
					else if (player.getLocation().getBlockZ() != frozenLocation.getBlockZ())
						player.teleport(frozenLocation);
				}
				catch (IllegalStateException e) { }
			}
		}, 0, 10);
	}
	
	public void stopFreeze()
	{
		Bukkit.getScheduler().cancelTask(moveTID);
	}
}



