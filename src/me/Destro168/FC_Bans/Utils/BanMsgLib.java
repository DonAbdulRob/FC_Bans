package me.Destro168.FC_Bans.Utils;

import me.Destro168.FC_Suite_Shared.Messaging.MessageLib;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BanMsgLib extends MessageLib
{
	CommandSender sender;
	
	Player player;
	ConsoleCommandSender console;
	
	public String getPunisherName()
	{
		if (sender instanceof Player)
			return player.getName();
		
		return "Console";
	}
	
	public BanMsgLib(CommandSender sender_)
	{
		super(sender_);
		sender = sender_;
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







