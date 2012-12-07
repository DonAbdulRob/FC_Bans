package me.Destro168.FC_Bans.Utils;

import me.Destro168.FC_Suite_Shared.Messaging.MessageLib;

import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

public class BanMsgLib extends MessageLib
{
	Player player;
	ColouredConsoleSender console;
	
	public String getPunisherName()
	{
		if (player != null)
			return player.getName();
		else if (console != null)
			return "Console";
		
		return "Unspecified";
	}
	
	public BanMsgLib(Player player_)
	{
		super(player_);
		player = (Player) player_;
	}
	
	public BanMsgLib(ColouredConsoleSender console_)
	{
		super(console_);
		console = (ColouredConsoleSender) console_;
	}
	
	public boolean isImmune()
	{
		return standardError("Target is immune to punishment.");
	}
	
	public boolean errorBadDuration()
	{
		return standardError("Bad duration entered!");
	}
	
	public boolean errorAlreadyPunished()
	{
		return standardError("Command failed, this player is already under this punishment. Remove their current punishment then try again!");
	}
}







