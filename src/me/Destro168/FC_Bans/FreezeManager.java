package me.Destro168.FC_Bans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeManager 
{
	private List<FrozenPlayer> frozenPlayers;
	
	public FreezeManager()
	{
		frozenPlayers = new ArrayList<FrozenPlayer>();
	}
	
	public void startPlayerFreezes()
	{
		FrozenPlayer frozenPlayer;
		
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			frozenPlayer = new FrozenPlayer(player);
			frozenPlayers.add(frozenPlayer);
		}
	}
	
	public void startPlayerFreeze(Player player)
	{
		FrozenPlayer frozenPlayer = new FrozenPlayer(player);
		
		//Only add frozen players to the list.
		if (frozenPlayer.getIsFrozen() == true)
			frozenPlayers.add(frozenPlayer);
	}
	
	public void stopPlayerFreezeTask(String playerName)
	{
		//Stop freezes on frozen people.
		for (FrozenPlayer frozenPlayer : frozenPlayers)
		{
			if (frozenPlayer.getPlayerName().equals(playerName))
			{
				frozenPlayer.stopFreeze();
			}
		}
	}
}
