package me.Destro168.FC_Bans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.Destro168.ConfigManagers.CustomConfigurationManager;
import me.Destro168.FC_Bans.Utils.ConfigSettingsManager;
import me.Destro168.Messaging.BroadcastLib;
import me.Destro168.Messaging.MessageLib;
import me.Destro168.TimeUtils.DateManager;
import me.Destro168.TimeUtils.TimeStringParser;
import me.Destro168.FC_Suite_Shared.NameMatcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

public class PunishmentManager
{
	//Variable declarations
	private final int MAX_WARNINGS = 100;
	private CustomConfigurationManager profile;
	private final DateFormat dfm = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String playerName;
	private String playerPath;
	private ConfigSettingsManager csm;
	
	public void setName(String x) { playerName = x; }
	public String getName() { return playerName; }
	
	public String getWarningReason(int x) { return profile.getString(playerPath + x + ".reason"); }
	public String getWarningTime(int x) { return profile.getString(playerPath + x + ".time"); }
	public String getWarningLength(int x) { return profile.getString(playerPath + x + ".length"); }
	public String getWarningType(int x) { return profile.getString(playerPath + x + ".type"); }
	public String getWarnGiverName(int x) { return profile.getString(playerPath + x + ".warnGiverName"); }
	public int getWarningLevel(int x) { return profile.getInt(playerPath + x + ".level"); }
	
	public boolean getCreated() { return profile.getBoolean(playerPath + "created"); }
	public long getUnbanDate() { return profile.getLong(playerPath + "unbanDate"); }
	public long getUnmuteDate() { return profile.getLong(playerPath + "unmuteDate"); }
	public long getUnfreezeDate() { return profile.getLong(playerPath + "unfreezeDate"); }
	public boolean getIsPermaBanned() { return profile.getBoolean(playerPath + "isPermaBanned"); }
	public boolean getIsPermaMuted() { return profile.getBoolean(playerPath + "isPermaMuted"); }
	public boolean getIsIpBanned() { return profile.getBoolean(playerPath + "isIpBanned"); }
	public boolean getIsPermaFrozen() { return profile.getBoolean(playerPath + "isPermaFrozen"); }
	public String getIp() { return profile.getString(playerPath + "ip"); }
	
	public void setWarningReason(int x, String y) { profile.set(playerPath + x + ".reason", y); }
	public void setWarningTime(int x, String y) { profile.set(playerPath + x + ".time", y); }
	public void setWarningLength(int x, String y) { profile.set(playerPath + x + ".length", y); }
	public void setWarningType(int x, String y) { profile.set(playerPath + x + ".type", y); }
	public void setWarnGiverName(int x, String y) { profile.set(playerPath + x + ".warnGiverName", y); }
	public void setWarningLevel(int x, int y) { profile.set(playerPath + x + ".level", y); }
	
	public void setCreated(boolean x) { profile.set(playerPath + "created", x); }
	public void setUnbanDate(long x) { profile.set(playerPath + "unbanDate", x); }
	public void setUnmuteDate(long x) { profile.set(playerPath + "unmuteDate", x); }
	public void setUnfreezeDate(long x) { profile.set(playerPath + "unfreezeDate", x); }
	public void setIsPermaBanned(boolean x) { profile.set(playerPath + "isPermaBanned", x); }
	public void setIsPermaMuted(boolean x) { profile.set(playerPath + "isPermaMuted", x); }
	public void setIsIpBanned(boolean x) { profile.set(playerPath + "isIpBanned", x); }
	public void setIsPermaFrozen(boolean x) { profile.set(playerPath + "isPermaFrozen", x); }
	public void setIp(String x) { profile.set(playerPath + "ip", x); }
	
	public void deleteWarning(int x) { profile.set(playerPath + x, null); }
	public String getUnbanDateNormal() { if (getUnbanDate() > 0) return dfm.format(getUnbanDate()); else return "Not banned"; }
	public String getUnmuteDateNormal() { if (getUnmuteDate() > 0) return dfm.format(getUnmuteDate()); else return "Not Muted"; }
	public String getUnfreezeDateNormal() { if (getUnfreezeDate() > 0) return dfm.format(getUnfreezeDate()); else return "Not Frozen"; }
	
	public PunishmentManager(String playerName_)
	{
		//Variable declarations
		NameMatcher nm = new NameMatcher();
		csm = new ConfigSettingsManager();
		
		//Attempt to match the name.
		playerName = nm.getNameByMatch(playerName_);
		
		//Get easily compatible player name.
		if (playerName.equals(""))
			playerName = playerName_;
		
		//Use player name before modified for real name.
		profile = new CustomConfigurationManager(FC_Bans.plugin.getDataFolder().getAbsolutePath(), playerName);
		
		//Set the playerPath.
		playerPath = "FC_Bans.";
		
		//Check if the profile was made before.
		checkCreatedBefore();
	}
	
	public void updatePlayerWarnings()
	{
		boolean blankSpot = false;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Rearrange warnings so there are no spaces
		// - We find a non-blank warning and then check every warning before that to see if it is empty.
		// - If there is an empty warning, then we want to switch it to that posiiton.
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//'i' is the first non-blank warning.
		for (int i = 0; i < MAX_WARNINGS; i++)
		{
			//Store if there is a blank spot.
			if (getWarningReason(i) == null)
				blankSpot = true;
			
			//Find a nonblank warning.
			else if (getWarningReason(i) != null)
			{
				if (blankSpot == true)
				{
					//Set to false in the future.
					blankSpot = false;
					
					//J is the blank warning, move onto j
					for (int j = 0; j < i; j++)
					{
						//Find a non-blank warning.
						if (getWarningReason(j) == null)
						{
							moveWarning(j, i);
							j = i;
						}
					}
				}
			}
		}
	}
	
	//we set b onto a then clear b.
	public void moveWarning(int a, int b)
	{
		setWarningReason(a, getWarningReason(b));
		setWarningTime(a, getWarningTime(b));
		setWarningType(a, getWarningType(b));
		setWarningLevel(a, getWarningLevel(b));
		setWarningLength(a, getWarningLength(b));
		setWarnGiverName(a, getWarnGiverName(b));
		
		deleteWarning(b);
	}
	
	public void addWarning(String type, String time, String length, String reason, int warningLevel, String warnGiverName)
	{
		String warning = "";
		String[] broadcastMessage;
		BroadcastLib bLib = new BroadcastLib();
		
		//Add the warning to the first empty warning slot.s
		for (int i = 0; i < MAX_WARNINGS; i++)
		{
			if (getWarningReason(i) == null)
			{
				setWarningReason(i, reason);
				setWarningTime(i, time);
				setWarningLength(i, length);
				setWarningType(i, type);
				setWarningLevel(i, warningLevel);
				setWarnGiverName(i, warnGiverName);
				
				if (csm.getGlobalAnnouncementsEnabled() == true)
				{
					warning = getWarningReason(i);
					
					//Only display a warning if it's not null and not empty.
					if (warning != null)
					{
						broadcastMessage = new String[7];
						
						//Send them the warning with the tag and warning.
						broadcastMessage[0] = playerName + " has been ";
						
						if (getWarningType(i).equalsIgnoreCase("Freeze"))
							broadcastMessage[0] += "frozen!";
						else if (getWarningType(i).equalsIgnoreCase("Ban"))
							broadcastMessage[0] += "banned!";
						else if (getWarningType(i).equalsIgnoreCase("Warn"))
							broadcastMessage[0] += "warned!";
						else if (getWarningType(i).equalsIgnoreCase("Mute"))
							broadcastMessage[0] += "muted!";
						else if (getWarningType(i).equalsIgnoreCase("Kick"))
							broadcastMessage[0] += "kicked!";
						
						if (csm.getDisplayWarnGiverNameOnPunish() == true)
						{
							broadcastMessage[1] = " Issuer: ";
							broadcastMessage[2] = warnGiverName + ".";
						}
						
						if (!warning.equalsIgnoreCase("null") && !warning.equals(""))
						{
							broadcastMessage[3] = " Reason: ";
							broadcastMessage[4] = reason;
							
							if (!getWarningType(i).equalsIgnoreCase("kick") && !getWarningType(i).equalsIgnoreCase("warn"))
							{
								broadcastMessage[5] = " Length: ";
								broadcastMessage[6] = length;
							}
						}
						
						//Display the broadcast.
						bLib.standardBroadcast(broadcastMessage);
					}
				}
				
				//End execution
				return;
			}
		}
	}

	public int getTotalWarnLevel()
	{
		int totalLevel = 0;
		
		//Parse all warnings.
		for (int i = 0; i < MAX_WARNINGS; i++)
		{
			//Add the warning level.
			totalLevel += getWarningLevel(i);
		}
		
		return totalLevel;
	}
	
	public String getTotalWarnLevelColored()
	{
		int totalWarnLevel = getTotalWarnLevel();
		String warning;
		
		if (totalWarnLevel < csm.getLowWarningLevel())
			warning = ChatColor.GRAY + "" + totalWarnLevel;
		else if (totalWarnLevel < csm.getMediumWarningLevel())
			warning = ChatColor.GREEN + "" + totalWarnLevel;
		else if (totalWarnLevel < csm.getHighWarningLevel())
			warning = ChatColor.YELLOW + "" + totalWarnLevel;
		else if (totalWarnLevel < csm.getExtremeWarningLevel())
			warning = ChatColor.RED + "" + totalWarnLevel;
		else
			warning = ChatColor.DARK_RED + "" + totalWarnLevel;
		
		return warning;
	}
	
	//A more general function to send a player a list of warnings.
	public void showWarningList(CommandSender sender)
	{
		//Variable Declarations
		Map<Integer, String> warningMap = new HashMap<Integer, String>();
		String[] warningArray;
		String reason = "";
		MessageLib msgLib;
		
		//We want to rearrange the warnings before displaying them.
		updatePlayerWarnings();
		
		//Set up the messageLib to who we are sending the message to.
		if (sender instanceof Player)
			msgLib = new MessageLib((Player) sender);
		else if (sender instanceof ColouredConsoleSender)
			msgLib = new MessageLib((ColouredConsoleSender) sender);
		else
			return;
		
		//Store all valid warnings.
		for (int i = 0; i < MAX_WARNINGS; i++)
		{
			reason = getWarningReason(i);
			
			if (reason != null)
			{
				if (!reason.equals("null"))
				{
					warningMap.put(i, reason);
				}
			}
		}
		
		//Send the warning level.
		msgLib.standardMessage("Warning Level", getTotalWarnLevelColored());
		
		//Display all warnings.
		for (int i = 0; i < warningMap.size(); i++)
		{
			warningArray = new String[12];
			
			warningArray[0] = "Warning #";
			warningArray[1] = String.valueOf(i + 1);
			warningArray[2] = " [";
			warningArray[3] = getWarningType(i);
			warningArray[4] = "] - Giver: [";
			warningArray[7] = getWarnGiverName(i);
			warningArray[8] = "] - Reason: ";
			warningArray[9] = getWarningReason(i);
			
			if (!getWarningType(i).equals("Kick") && !getWarningType(i).equals("Warn"))
			{
				warningArray[10] = " - Length: ";
				warningArray[11] = getWarningLength(i);
			}
			
			msgLib.standardMessage(warningArray);
		}
	}
	
	public boolean isBanned()
	{
		Date now = new Date();
		
		//Always check if perma-banned first.
		if (getIsPermaBanned() == true)
			return true;
		
		//If they haven't served their time yet, then return true.
		if (now.getTime() <= getUnbanDate())
			return true;
		
		//Perform a check to see if they are ip-banned.
		if (getIsIpBanned() == true)
			return true;
		
		//If they pass all checks, use unban to clear all information.
		unban();
		
		return false;
	}
	
	public boolean isMuted()
	{
		Date now = new Date();
		
		//Always check if permanently muted first.
		if (getIsPermaMuted() == true)
			return true;
		
		//If they haven't served their time yet, then return true.
		if (now.getTime() <= getUnmuteDate())
			return true;
		
		//If they pass all checks unmute them.
		unmute();
		
		return false;
	}
	
	public boolean isFrozen()
	{
		Date now = new Date();
		
		//Always check if permanently muted first.
		if (getIsPermaFrozen() == true)
			return true;
		
		//If they haven't served their time yet, then return true.
		if (now.getTime() <= getUnfreezeDate())
			return true;
		
		//If they pass all checks unmute them.
		unfreeze();
		
		return false;
	}
	
	public void unban()
	{
		//Set unban date to 0.
		setUnbanDate(0);
		
		//Remove perma-bans.
		setIsPermaBanned(false);
		
		if (csm.getEnableBukkitBanSynergy())
		{
			Bukkit.getServer().getOfflinePlayer(playerName).setBanned(false);	//Set the player to unbanned through bukkit.
			
			if (getIp() != null)
				Bukkit.getServer().unbanIP(getIp()); //Enable IP ban through bukkit.
		}
		
		//Remove ip ban.
		setIsIpBanned(false);
	}
	
	public void unmute()
	{
		//Set unmute date to 0.
		setUnmuteDate(0);
		
		//Remove perma-mute
		setIsPermaMuted(false);
	}
	
	public void unfreeze()
	{
		//Set unmute date to 0.
		setUnfreezeDate(0);
		
		//Remove perma-mute
		setIsPermaFrozen(false);
	}
	
	/////////////////////////////////////////////////////////////////
	// Handle Punishments
	// Type 0 = Warn, Type 1 = Ban, Type 2 = Mute, Type 3 kick, Type 4 = freeze
	/////////////////////////////////////////////////////////////////
	
	private void checkCreatedBefore()
	{
		if (getCreated() == false)
		{
			setCreated(true);
			
			if (getUnbanDate() == 0)
				setUnbanDate(0);
			
			if (getUnmuteDate() == 0)
				setUnmuteDate(0);
			
			if (getIsPermaBanned() == false)
				setIsPermaBanned(false);
			
			if (getIsPermaMuted() == false)
				setIsPermaMuted(false);
			
			if (getIsPermaFrozen() == false)
				setIsPermaFrozen(false);
			
			//Store ip.
			if (Bukkit.getServer().getPlayer(playerName) != null)
				setIp(Bukkit.getServer().getPlayer(playerName).getAddress().toString());
		}
	}
	
	public void warnPlayer(String reason, String warnGiver)
	{
		Date now = new Date();
		Player player;
		MessageLib msgLib;
		
		//Add the warning
		addWarning("Warn", dfm.format(now), "0", reason, csm.getWarningLevelWarn(), warnGiver);
		
		if (Bukkit.getPlayer(playerName) != null)
		{	
			player = Bukkit.getServer().getPlayer(playerName);
			
			if (player.isOnline() == true)
			{
				msgLib = new MessageLib(player);
				
				//Tell the player that they have been warned by the warn giver.
				if (csm.getDisplayWarnGiverNameOnPunish() == true)
					msgLib.standardMessage("You have just been warned by " + warnGiver + ":");
				else
					msgLib.standardMessage("You have just been warned:");
				
				//Send the player the reason they were warned.
				msgLib.standardMessage("Reason: " + reason);
			}
		}
	}
	
	public boolean punishPlayer(int type, String duration, String reason, String punishGiver)
	{
		Date now = new Date();
		DateManager dm = new DateManager();
		TimeStringParser tsp = new TimeStringParser();
		
		String durationTimeText = "";
		long punishEndTime;
		int warnLevel = 0;
		int countDays;
		
		if (type == 0)
		{
			warnPlayer(reason, punishGiver);
			return true;
		}
		else if (type == 3)
		{
			kickPlayer(reason, punishGiver);
			return true;
		}
		
		//If the duration is permanent, then we want to treat the punishment as permanent.
		if (duration.contains("perm") || duration.equals(".") || duration.equals(""))
		{
			//Set durationTimeText to permanent.
			durationTimeText = "[Permanent]";
			
			if (type == 1)
			{
				//Add the warning
				addWarning("Ban", dfm.format(now), durationTimeText, reason, csm.getWarningLevelBan(), punishGiver);
				
				//Set the players ban record, permanent, true.
				setIsPermaBanned(true);
				
				if (csm.getEnableBukkitBanSynergy())
					Bukkit.getServer().getOfflinePlayer(playerName).setBanned(true);	//Set the player to banned through bukkit.
				
				//Alert the player that they are banned.
				dealPunishment(1, reason, durationTimeText, punishGiver);
			}
			else if (type == 2)
			{
				//Add the warning
				addWarning("Mute", dfm.format(now), durationTimeText, reason, csm.getWarningLevelMute(), punishGiver);
				
				//Set the players ban record, permanent, true.
				setIsPermaMuted(true);
				
				//Alert the player they are muted.
				dealPunishment(2, reason, durationTimeText, punishGiver);
			}
			else if (type == 4)
			{
				//Add the warning
				addWarning("Frozen", dfm.format(now), durationTimeText, reason, csm.getWarningLevelMute(), punishGiver);
				
				//Set the players ban record, permanent, true.
				setIsPermaFrozen(true);
				
				//Alert the player they are muted.
				dealPunishment(4, reason, durationTimeText, punishGiver);
			}
		}
		
		//Else parse user input to get duration text.
		else
		{
			//We want to first parse the duration to get the seconds they will be punished for.
			durationTimeText = tsp.parseUserInputTimeString(duration);
			
			//Create temp variable to count number of days to determine warn level.
			countDays = tsp.getIntSeconds();
			
			while (countDays > 86400)
			{
				countDays = countDays - 86400;
				warnLevel = warnLevel + csm.getWarnBonusPerDayBan();
			}
			
			//Store the time when the punishment will end.
			punishEndTime = dm.getFutureDate_Seconds(countDays);
			
			//If a ban
			if (type == 1)
			{
				//Set base warn level to ban warn level.
				warnLevel = csm.getWarningLevelBan();
				
				//Add the warning
				addWarning("Ban", dfm.format(now), durationTimeText, reason, warnLevel, punishGiver);
				
				//Set player unban date to a future date based on past time.
				setUnbanDate(punishEndTime);
				
				//Get the player
				dealPunishment(1, reason, durationTimeText, punishGiver);
				
				if (csm.getEnableBukkitBanSynergy())
					Bukkit.getServer().getOfflinePlayer(playerName).setBanned(true);	//Set the player to banned through bukkit.
			}
			
			//If a mute
			else if (type == 2)
			{
				//Set base warn level to ban warn level.
				warnLevel = csm.getWarningLevelMute();
				
				//Add the warning
				addWarning("Mute", dfm.format(now), durationTimeText, reason, warnLevel, punishGiver);
				
				//Set the players unmute time equal to current time + mute length.
				setUnmuteDate(punishEndTime);
				
				//Alert the player they are muted.
				dealPunishment(2, reason, durationTimeText, punishGiver);
			}
			
			//If a freeze
			else if (type == 4)
			{
				//Set base warn level to ban warn level.
				warnLevel = csm.getWarningLevelFreeze();
				
				//Add the warning
				addWarning("Freeze", dfm.format(now), durationTimeText, reason, warnLevel, punishGiver);
				
				//Set the players unmute time equal to current time + mute length.
				setUnfreezeDate(punishEndTime);
				
				//Alert the player they are muted.
				dealPunishment(4, reason, durationTimeText, punishGiver);
			}
		}
		
		return true;
	}
	
	private void dealPunishment(int type, String reason, String durationTimeText, String punishGiver)
	{
		MessageLib msgLib;
		Player player = Bukkit.getPlayer(playerName);
		
		if (type == 1)
		{
			if (player != null)
			{
				if (player.isOnline() == true)
				{
					if (csm.getDisplayWarnGiverNameOnPunish() == true)
						player.kickPlayer("You have been banned by " + punishGiver + ". Length: " + durationTimeText + " Reason: " + reason);
					else
						player.kickPlayer("You have been banned for. Length: " + durationTimeText + ". Reason: " + reason);
				}
			}
		}
		else if (type == 2)
		{
			if (player != null)
			{
				if (player.isOnline() == true)
				{
					msgLib = new MessageLib(player);
					
					if (csm.getDisplayWarnGiverNameOnPunish() == true)
						msgLib.standardMessage("You have been muted by " + punishGiver + ". Length: " + durationTimeText + " Reason: " + reason);
					else
						msgLib.standardMessage("You have been muted. Length: " + durationTimeText + " Reason: " + reason);
				}
			}
		}
		else if (type == 4)
		{
			if (player != null)
			{
				if (player.isOnline() == true)
				{
					msgLib = new MessageLib(player);
					
					if (csm.getDisplayWarnGiverNameOnPunish() == true)
						msgLib.standardMessage("You have been frozen by " + punishGiver + ". Length: " + durationTimeText + " Reason: " + reason);
					else
						msgLib.standardMessage("You have been frozen. Length: " + durationTimeText + " Reason: " + reason);
				}
			}
		}
	}

	public void kickPlayer(String reason, String kicker)
	{
		Date now = new Date();
		
		//Add the warning
		addWarning("Kick", dfm.format(now), "0", reason, csm.getWarningLevelKick(), kicker);
		
		//Kick the player
		if (Bukkit.getServer().getPlayer(playerName) != null)
		{
			if (Bukkit.getServer().getPlayer(playerName).isOnline() == true)
			{
				if (csm.getDisplayWarnGiverNameOnPunish() == true)
					Bukkit.getServer().getPlayer(playerName).kickPlayer("You have been kicked by " + kicker + ". Reason: " + reason);
				else
					Bukkit.getServer().getPlayer(playerName).kickPlayer("You have been kicked. Reason: " + reason);
			}
		}
	}
	
	public String getCorrectUnmuteDate()
	{
		if (getIsPermaMuted() == true)
		{
			return "Never";
		}
		else
		{
			return getUnmuteDateNormal();
		}
	}
	
	public String getCorrectUnbanDate()
	{
		if (getIsPermaBanned() == true)
		{
			return "Never";
		}
		else
		{
			return getUnbanDateNormal();
		}
	}
	
	public String getCorrectUnfreezeDate()
	{
		if (getIsPermaFrozen() == true)
		{
			return "Never";
		}
		else
		{
			return getUnfreezeDateNormal();
		}
	}
	
	public void sendIsBanned(Player messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unbanned on: " + getCorrectUnbanDate());
	}
	
	public void sendIsBanned(ColouredConsoleSender messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unbanned on: " + getCorrectUnbanDate());
	}
	
	public void sendIsMuted(Player messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unmuted on: " + getCorrectUnmuteDate());
	}
	
	public void sendIsMuted(ColouredConsoleSender messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unmuted on: " + getCorrectUnmuteDate());
	}
	
	public void sendIsFrozen(Player messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unfrozen on: " + getCorrectUnfreezeDate());
	}
	
	public void sendIsFrozen(ColouredConsoleSender messagePlayer)
	{
		MessageLib msgLib = new MessageLib(messagePlayer);
		msgLib.standardMessage(playerName + " will be unfrozen on: " + getCorrectUnfreezeDate());
	}
}

/*
Add warnings to the player based on level of infraction.

Minor Rulebreak = 1;
Medium Rulebreak = 3;
Major Rulebreak = 5;

Kick = 1
Mute = 1

*/
