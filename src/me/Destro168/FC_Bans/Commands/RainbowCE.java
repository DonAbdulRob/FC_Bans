package me.Destro168.FC_Bans.Commands;

import me.Destro168.FC_Bans.FC_Bans;
import me.Destro168.FC_Bans.PunishmentManager;
import me.Destro168.FC_Bans.Utils.BanArgParser;
import me.Destro168.FC_Bans.Utils.ConfigSettingsManager;
import me.Destro168.FC_Bans.Utils.BanMsgLib;
import me.Destro168.FC_Bans.Utils.FC_BansPermissions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RainbowCE implements CommandExecutor
{
	private final int checkType = 0;
	private final int banType = 1;
	private final int freezeType = 2;
	private final int kickType = 3;
	private final int muteType = 4;
	private final int warnType = 5;
	private final int helpType = 6;
	
	private PunishmentManager record;
	private BanMsgLib msgLib;
	private Player player;
	private ConsoleCommandSender console;
	private FC_BansPermissions perms;
	private BanArgParser bap;
	private CommandSender sender;
	private ConfigSettingsManager csm;
	private String arg0;
	private String arg1;
	private String arg2;
	private int commandType;
	
	public RainbowCE() { }
	 
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args2)
    {
		setCommandType(cmd);
		
		if (initialize(sender, args2) == false)
		{
			msgLib.standardMessage("Failed to initialize key variables for continued command execution.");
			return true;
		}
		
		//Attempt to execute command based on debug mode.
		if (csm.getDebugMode() == true)
			executeCommand();
		else
		{
			try { executeCommand(); }
			catch (NullPointerException e) { return msgLib.errorInvalidCommand(); }
			catch (ArrayIndexOutOfBoundsException e) { return msgLib.errorInvalidCommand(); }
		}
		
		return true;
    }
	
	private void setCommandType(Command cmd)
	{
		//Variable Declarations
		ConfigSettingsManager csm = new ConfigSettingsManager();
		
		//Set the command type to what it is.
		if (cmd.getName().equalsIgnoreCase(csm.getCheckKeyWord()))
			commandType = checkType;
		else if (cmd.getName().equalsIgnoreCase(csm.getBanKeyWord()))
			commandType = banType;
		else if (cmd.getName().equalsIgnoreCase(csm.getFreezeKeyWord()))
			commandType = freezeType;
		else if (cmd.getName().equalsIgnoreCase(csm.getKickKeyWord()))
			commandType = kickType;
		else if (cmd.getName().equalsIgnoreCase(csm.getMuteKeyWord()))
			commandType = muteType;
		else if (cmd.getName().equalsIgnoreCase(csm.getWarnKeyWord()))
			commandType = warnType;
		else
			commandType = helpType;
	}
	
	private boolean initialize(CommandSender sender_, String[] args2)
	{
		//Variable Declarations/Initializations
		bap = new BanArgParser(args2);
		msgLib = new BanMsgLib(sender);
		csm = new ConfigSettingsManager();
		arg0 = bap.getArg(0);
		arg1 = bap.getArg(1);
		arg2 = bap.getArg(2);
		
		sender = sender_;
		
		//Assign key variables based on command input and arguments.
		if (sender instanceof Player)
		{
			player = (Player) sender;
			perms = new FC_BansPermissions(player);
			msgLib = new BanMsgLib(sender);
		}
		else if (sender instanceof ConsoleCommandSender)
		{
			console = (ConsoleCommandSender) sender;
			perms = new FC_BansPermissions(true);
			msgLib = new BanMsgLib(sender);
		}
		else
		{
			FC_Bans.plugin.getLogger().info("Unknown command sender, returning ban command.");
			return false;
		}
		
		return true;
	}
	
	private boolean executeCommand()
	{
		//Handle check command if the first argument was check.
		if (arg0.equalsIgnoreCase("check"))
		{
			if (arg1.equalsIgnoreCase(""))
				return msgLib.errorInvalidCommand();
			
			checkSubCommand();
		}
		
		//Handle remove argument if first argument was remove.
		else if (arg0.equalsIgnoreCase("remove"))
		{
			if (arg1.equalsIgnoreCase(""))
				return msgLib.errorInvalidCommand();
			
			removeSubCommand();
		}
		
		//Simply take action if no argument was given.
		else
		{
			//Do not check immunity on check commands.
			if (commandType == checkType)
			{
				if (arg0.equalsIgnoreCase(""))
					return msgLib.errorInvalidCommand();
				
				checkCommand();
			}
			
			//Do not check commands on help commands.
			else if (commandType == helpType)
				helpCommand();
			
			//Check immunity for certain permissions.
			else
			{
				if (arg0.equalsIgnoreCase(""))
					return msgLib.errorInvalidCommand();
				
				//Check if the player is immune
				if (perms.isImmune(arg0) == true)
					return msgLib.isImmune();
				
				//Check what command it is being called and execute.
				if (commandType == banType)
					banCommand();
				
				else if (commandType == freezeType)
					freezeCommand();
				
				else if (commandType == kickType)
					kickCommand();
				
				else if (commandType == muteType)
					muteCommand();
				
				else if (commandType == warnType)
					warnCommand();
			}
		}
		
		return true;
	}
	
	private boolean checkSubCommand()
	{
		//First argument is check so get name from 2nd argument.
		record = new PunishmentManager(arg1);
		
		if (commandType == banType)
		{
			//If no permission, then cancel command use.
			if (perms.canBanCheck() == false)
				return msgLib.errorNoPermission();
			
			//Message punishment status.
			if (player != null)
				record.sendIsBanned(player);
			else if (console != null)
				record.sendIsBanned(console);
		}
		else if (commandType == freezeType)
		{
			//If no permission, then cancel command use.
			if (perms.canFreezeCheck() == false)
				return msgLib.errorNoPermission();
			
			//Message punishment status.
			if (player != null)
				record.sendIsFrozen(player);
			else if (console != null)
				record.sendIsFrozen(console);
		}
		else if (commandType == muteType)
		{
			//If no permission, then cancel command use.
			if (perms.canMuteCheck() == false)
				return msgLib.errorNoPermission();
			
			//Message punishment status.
			if (player != null)
				record.sendIsMuted(player);
			else if (console != null)
				record.sendIsMuted(console);
		}
		else if (commandType == warnType)
		{
			//Without permission deny the command.
			if (perms.canWarn() == false)
				return msgLib.errorNoPermission();
			
			//Send the player the warning list.
			record.showWarningList(sender);
		}
		
		return true;
	}
	
	private boolean removeSubCommand()
	{
		//First argument is remove so get name from 2nd argument.
		record = new PunishmentManager(arg1);
		
		if (commandType == banType)
		{
			//If no permission, then cancel command use.
			if (perms.canBanRemove() == false)
				return msgLib.errorNoPermission();
			
			//Unban the player.
			record.unban();
		}
		
		else if (commandType == freezeType)
		{
			//If no permission, then cancel command use.
			if (perms.canFreezeRemove() == false)
				return msgLib.errorNoPermission();
			
			//First argument is remove so get name from 2nd argument.
			record = new PunishmentManager(arg1);
			
			//Unfreeze the player.
			record.unfreeze();
			
			//Tell the player they have been unfreezed.
			if (Bukkit.getServer().getPlayer(arg1) != null)
			{
				if (Bukkit.getServer().getPlayer(arg1).isOnline())
				{
					//Unfreeze.
					FC_Bans.fm.stopPlayerFreezeTask(Bukkit.getServer().getPlayer(arg1).getName());
					
					//Tell the player they are unfrozen.
					BanMsgLib banLib = new BanMsgLib(Bukkit.getServer().getPlayer(arg1));
					banLib.standardMessage("You have been unfrozen.");
				}
			}
		}
		
		else if (commandType == muteType)
		{
			//If no permission, then cancel command use.
			if (perms.canMuteRemove() == false)
				return msgLib.errorNoPermission();
			
			//First argument is remove so get name from 2nd argument.
			record = new PunishmentManager(arg1);
			
			//Unmute the player.
			record.unmute();
			
			//Tell the player they have been unmuted.
			if (Bukkit.getServer().getPlayer(arg1) != null)
			{
				if (Bukkit.getServer().getPlayer(arg1).isOnline())
				{
					//Tell the person they are unmuted.
					BanMsgLib banLib = new BanMsgLib(Bukkit.getServer().getPlayer(arg1));
					banLib.standardMessage("You have been unmuted.");
				}
			}
		}
		
		else if (commandType == warnType)
		{
			//Without permission deny the command.
			if (perms.canWarnRemove() == false)
				return msgLib.errorNoPermission();
			
			//First argument is check so get name from 2nd argument.
			record = new PunishmentManager(arg1);
			
			try
			{
				//Attempt to delete the warning.
				record.deleteWarning(Integer.valueOf(arg2) - 1);
			}
			catch (NumberFormatException e)
			{
				return msgLib.errorInvalidCommand();
			}
		}
		
		//Send the player confirmation.
		return msgLib.successCommand();
	}
	
	private boolean checkCommand()
	{
		if (perms.canCheck() == false)
			return msgLib.errorNoPermission();
		
		//First argument is check so get name from 2nd argument.
		record = new PunishmentManager(arg0);
		
		//Send the player the warning list.
		record.showWarningList(sender);
		
		//Announce punishment status.
		if (player != null)
		{
			record.sendIsBanned(player);
			record.sendIsMuted(player);
			record.sendIsFrozen(player);
		}
		else if (console != null)
		{
			record.sendIsBanned(console);
			record.sendIsMuted(console);
			record.sendIsFrozen(console);
		}
		
		//Always return true.
		return true;
	}
	
	private boolean banCommand()
	{
		//If no permission, then cancel command use.
		if (perms.canBan() == false)
			return msgLib.errorNoPermission();
		
		//Check if argument ip is given.
		if (arg0 != null)
		{
			if (arg0.equalsIgnoreCase("ip"))
			{
				//Set final punish arg.
				bap.setPunishReason(3);
				
				//Ban the player
				if (actuallyBanPlayer(sender, arg1, arg2, bap.getFinalArg(), msgLib.getPunisherName()) == false)
					return true;
				
				//Handle ip stuff if the player has logged on before.
				if (Bukkit.getServer().getOfflinePlayer(arg1) != null)
				{
					//Set players ip ban to false.
					record.setIsIpBanned(true);
					
					if (csm.getEnableBukkitBanSynergy())
						Bukkit.getServer().banIP(record.getIp()); //Enable IP ban through bukkit.
				}
				
				return true;
			}
		}
		
		//Set final punish arg.
		bap.setPunishReason(2);
		
		//Set bans to permanent if no time is specified.
		if (arg1.equals(""))
			bap.setArg(1, "perm");
		
		//Ban the player
		if (actuallyBanPlayer(sender, arg0, arg1, bap.getFinalArg(), msgLib.getPunisherName()) == false)
			return msgLib.errorBadDuration();
		
		return true;
	}
	
	private boolean actuallyBanPlayer(CommandSender sender, String target, String duration, String reason, String punishGiver)
	{
		//Get the record from the first passed argument.
		record = new PunishmentManager(target);
		
		//If the player is banned already, warn the banner that the target is already banned.
		if (record.isBanned() == true)
			return msgLib.errorAlreadyPunished();
		
		//Ban the player
		if (record.punishPlayer(1, duration, reason, punishGiver) == false)
			return msgLib.errorBadDuration();
		
		//Attempt to show the warning list.
		attemptShowWarningList(target, PunishmentManager.PTYPE_BAN);
		
		return true;
	}
	
	private boolean freezeCommand()
	{
		//If no permission, then cancel command use.
		if (perms.canFreeze() == false)
			return msgLib.errorNoPermission();
		
		//Get name from 2nd argument.
		record = new PunishmentManager(arg0);
		
		if (record.isFrozen() == true)
			return msgLib.errorAlreadyPunished();
		
		//Set final punish arg.
		bap.setPunishReason(2);
		
		//Set freezes to permanent if no time is specified.
		if (arg1.equals(""))
			bap.setArg(1, "perm");
		
		//Freeze the player
		if (record.punishPlayer(4, arg1, bap.getFinalArg(), msgLib.getPunisherName()) == false)
			return msgLib.errorBadDuration();
		
		if (Bukkit.getServer().getPlayer(arg0) != null)
			FC_Bans.fm.startPlayerFreeze(Bukkit.getServer().getPlayer(arg0));
		
		//Attempt to show the warning list.
		attemptShowWarningList(arg0, PunishmentManager.PTYPE_FREEZE);
		
		return true;
	}
	
	private boolean kickCommand()
	{
		if (Bukkit.getServer().getPlayer(arg0) == null)
			return msgLib.errorPlayerNotOnline();
		
		if (perms.canKick() == false)
			return msgLib.errorNoPermission();
		
		//Set final punish arg.
		bap.setPunishReason(1);
		
		//Record
		record = new PunishmentManager(arg0);
		
		//Ban the player
		record.kickPlayer(bap.getFinalArg(), msgLib.getPunisherName());
		
		//Attempt to show the warning list.
		attemptShowWarningList(arg0, PunishmentManager.PTYPE_KICK);
		
		return true;
	}
	
	private boolean muteCommand()
	{
		//If no permission, then cancel command use.
		if (perms.canMute() == false)
			return msgLib.errorNoPermission();
		
		//Get name from 2nd argument.
		record = new PunishmentManager(arg0);
		
		if (record.isMuted() == true)
			return msgLib.errorAlreadyPunished();
		
		//Set final punish arg.
		bap.setPunishReason(2);
		
		//Set mutes to permanent if no time is specified.
		if (arg1.equals(""))
			bap.setArg(1, "perm");
		
		//Mute the player
		if (record.punishPlayer(2, arg1, bap.getFinalArg(), msgLib.getPunisherName()) == false)
			return msgLib.errorBadDuration();
		
		//Attempt to show the warning list.
		attemptShowWarningList(arg0, PunishmentManager.PTYPE_MUTE);
		
		return true;
	}
	
	private boolean warnCommand()
	{
		//Without permission deny the command.
		if (perms.canWarn() == false)
			return msgLib.errorNoPermission();
		
		//Get the record from the first passed argument.
		record = new PunishmentManager(arg0);
		
		//Set final punish arg.
		bap.setPunishReason(1);
		
		//Ban the player
		record.warnPlayer(bap.getFinalArg(), msgLib.getPunisherName());
		
		//Attempt to show the warning list.
		attemptShowWarningList(arg0, PunishmentManager.PTYPE_WARN);
		
		return true;
	}
	
	private boolean helpCommand()
	{
		//Atempt to parse for a command with the proper permissions.
		try
		{
			if (perms.isAdmin())
			{
				if (!arg0.equals(""))
				{
					if (arg0.equalsIgnoreCase("global") == true)
					{
						if (csm.getGlobalAnnouncementsEnabled() == true)
						{
							csm.setGlobalAnnouncementsEnabled(false);
							msgLib.standardMessage("Sucessfully disabled global announcements.");
						}
						else
						{
							csm.setGlobalAnnouncementsEnabled(true);
							msgLib.standardMessage("Sucessfully enabled global announcements.");
						}
						
						return true;
					}
					else if (arg0.equalsIgnoreCase("autowarn"))
					{
						if (csm.getAutoShowWarningList() == true)
						{
							csm.setAutoShowWarningList(false);
							msgLib.standardMessage("Sucessfully disabled automatic warnings.");
						}
						else
						{
							csm.setGlobalAnnouncementsEnabled(true);
							msgLib.standardMessage("Sucessfully enabled automatic warnings.");
						}
						
						return true;
					}
					else if (arg0.equalsIgnoreCase("join"))
					{
						if (csm.getShowBannedPlayersAttemptedLogins() == true)
						{
							csm.setShowBannedPlayersAttemptedLogins(false);
							msgLib.standardMessage("Sucessfully disabled automatic warnings.");
						}
						else
						{
							csm.setShowBannedPlayersAttemptedLogins(true);
							msgLib.standardMessage("Sucessfully enabled automatic warnings.");
						}
						
						return true;
					}
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e) { }
		
		if (perms.isUser())
		{
			if (!arg0.equals("2"))
			{
				msgLib.standardHeader("FC_Bans Help - Page 1");
				
				msgLib.standardMessage("/fc_bans 2 | Displays second page of help.");
				
				//Only display commands they have permission to below.
				if (perms.canCheck())
					msgLib.standardMessage("/check [name]");
				
				if (perms.canKick())
					msgLib.standardMessage("/kick [name] [reason1] [reason2] [...]");
				
				if (perms.canBan())
				{
					msgLib.standardMessage("/ban [name] [duration] [reason1] [reason2] [...]");
					msgLib.standardMessage("/ban ip [name] [duration] [reason1] [reason2] [...]");
				}
				
				if (perms.canBanCheck())
					msgLib.standardMessage("/ban check [name]");
				
				if (perms.canBanRemove())
					msgLib.standardMessage("/ban remove [name]");
				
				if (perms.canMute())
					msgLib.standardMessage("/mute [name] [duration] [reason1] [reason2] [...]");
				
				if (perms.canMuteCheck())
					msgLib.standardMessage("/mute check [name]");
				
				if (perms.canMuteRemove())
					msgLib.standardMessage("/mute remove [name]");
				
				if (perms.canWarn())
					msgLib.standardMessage("/warn [name] [reason1] [reason2] [...]");
				
				if (perms.canWarnCheck())
					msgLib.standardMessage("/warn check [name]");
				
				if (perms.canWarnRemove())
					msgLib.standardMessage("/warn remove [name] [number]");
			}
			else if (arg0.equals("2"))
			{
				msgLib.standardHeader("FC_Bans Help - Page 2");

				msgLib.standardMessage("/fc_bans | Displays first page of help.");
				
				if (perms.canFreezeCheck())
					msgLib.standardMessage("/freeze check [name]");
				
				if (perms.canFreezeRemove())
					msgLib.standardMessage("/freeze remove [name] [number]");
				
				if (perms.canFreeze())
					msgLib.standardMessage("/freeze [name] [duration] [reason1] [...]");
				
				if (perms.isAdmin())
				{
					msgLib.standardMessage("/fc_bans global"," - Toggles global announcements.");
					msgLib.standardMessage("/fc_bans autowarn"," - Toggles auto-warnings.");
					msgLib.standardMessage("/fc_bans join"," - Toggles banned player join message.");
				}
				
				msgLib.standardMessage("-------- Note --------");
				msgLib.standardMessage("[duration] Format = []s[]m[]h[]d[]w.");
				msgLib.standardMessage("'[]' can be substituted for any number.");
				msgLib.standardMessage("Example Duration","'5d3m' = 5 days and 3 minutes.");
				msgLib.standardMessage("Example Command","/ban Destro168 3h2m1s Dislikes pie.");
			}
    	}
		else
		{
			return msgLib.errorNoPermission();
		}
		
		return true;
	}
	
	private void attemptShowWarningList(String pName, int type)
	{
		//If the global setting auto-show warning list is on, then show the warning list.
		if (csm.getAutoShowWarningList())
			record.showWarningList(sender);
		else
		{
			String str_Type = "";
			
			switch (type)
			{
				case 0:
					str_Type = "Warn";
					break;
				case 1:
					str_Type = "Ban";
					break;
				case 2:
					str_Type = "Mute";
					break;
				case 3:
					str_Type = "Kick";
					break;
				case 4:
					str_Type = "Freeze";
					break;
				
			}
			
			msgLib.infiniteMessage("[",str_Type,"] Successful on ",pName," - Warning Level: ",record.getTotalWarnLevel() + "");
		}
	}
}








