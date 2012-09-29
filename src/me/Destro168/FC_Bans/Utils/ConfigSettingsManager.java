package me.Destro168.FC_Bans.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import me.Destro168.FC_Bans.FC_Bans;
import me.Destro168.FC_Bans.PunishmentManager;

public class ConfigSettingsManager
{
	private FC_Bans plugin;
	private FileConfiguration config;
	
	public ConfigSettingsManager()
	{
		plugin = FC_Bans.plugin;
	}
	
	//Gets
	public String getName(String ip) { config = plugin.getConfig(); return config.getString("PlayerIps." + ip); }
	private void setName(String ip, String name) 
	{
		config = plugin.getConfig();
		
		if (storeAllPlayerIps())
			config.set("PlayerIps." + ip, name);
		
		plugin.saveConfig();
	}
	
	public double getVersion() { config = plugin.getConfig(); return config.getDouble("Version"); }
	public boolean getGlobalAnnouncementsEnabled() { config = plugin.getConfig(); return config.getBoolean("Setting.GlobalAnnouncementsEnabled"); }
	public boolean getAutoShowWarningList() { config = plugin.getConfig(); return config.getBoolean("Setting.AutoShowWarningList"); }
	public boolean getDisplayWarnGiverNameOnPunish() { config = plugin.getConfig(); return config.getBoolean("Setting.DisplayWarnGiverNameOnPunish"); }
	public boolean getPurgeAllMultipleAccountUsers() { config = plugin.getConfig(); return config.getBoolean("Setting.PurgeAllMultipleAccountUsers"); }
	public boolean getPreventMultiAccounting() { config = plugin.getConfig(); return config.getBoolean("Setting.PreventMultiAccounting"); }
	
	public int getLowWarningLevel() { config = plugin.getConfig(); return config.getInt("Setting.LowWarningLevel"); }
	public int getMediumWarningLevel() { config = plugin.getConfig(); return config.getInt("Setting.MediumWarningLevel"); }
	public int getHighWarningLevel() { config = plugin.getConfig(); return config.getInt("Setting.HighWarningLevel"); }
	public int getExtremeWarningLevel() { config = plugin.getConfig(); return config.getInt("Setting.ExtremeWarningLevel"); }
	public int getWarningLevelWarn() { config = plugin.getConfig(); return config.getInt("Setting.WarningLevelWarn"); }
	public int getWarningLevelKick() { config = plugin.getConfig(); return config.getInt("Setting.WarningLevelKick"); }
	public int getWarningLevelBan() { config = plugin.getConfig(); return config.getInt("Setting.WarningLevelBan"); }
	public int getWarningLevelMute() { config = plugin.getConfig(); return config.getInt("Setting.WarningLevelMute"); }
	public int getWarningLevelFreeze() { config = plugin.getConfig(); return config.getInt("Setting.WarningLevelFreeze"); }
	public int getWarnBonusPerDayBan() { config = plugin.getConfig(); return config.getInt("Setting.WarnBonusPerDayBan"); }
	public int getWarnBonusPerDayMute() { config = plugin.getConfig(); return config.getInt("Setting.WarnBonusPerDayMute"); }
	public int getWarnBonusPerDayFreeze() { config = plugin.getConfig(); return config.getInt("Setting.WarnBonusPerDayFreeze"); }
	
	public List<String> getBlockedCommands() { config = plugin.getConfig(); return config.getStringList("Setting.BlockedCommands"); }
	public String getBanKeyWord() { config = plugin.getConfig(); return config.getString("Setting.BanKeyWord"); }
	public String getMuteKeyWord() { config = plugin.getConfig(); return config.getString("Setting.MuteKeyWord"); }
	public String getKickKeyWord() { config = plugin.getConfig(); return config.getString("Setting.KickKeyWord"); }
	public String getWarnKeyWord() { config = plugin.getConfig(); return config.getString("Setting.WarnKeyWord"); }
	public String getFreezeKeyWord() { config = plugin.getConfig(); return config.getString("Setting.FreezeKeyWord"); }
	public String getCheckKeyWord() { config = plugin.getConfig(); return config.getString("Setting.CheckKeyWord"); }
	public boolean getLogAllPlayerCommands() { config = plugin.getConfig(); return config.getBoolean("Setting.LogAllPlayerCommands"); }
	public boolean getShowBannedPlayersAttemptedLogins() { config = plugin.getConfig(); return config.getBoolean("Setting.ShowBannedPlayersAttemptedLogins"); }
	public int getAutoPunishType() { config = plugin.getConfig(); return config.getInt("Setting.AutoPunishType"); }
	public String getAutoPunishLength() { config = plugin.getConfig(); return config.getString("Setting.AutoPunishLength"); }
	
	public boolean storeAllPlayerIps() { config = plugin.getConfig(); return config.getBoolean("Setting.StoreAllPlayerIps"); }
	public boolean getDebugMode() { config = plugin.getConfig(); return config.getBoolean("Setting.debugMode"); }
	
	//Sets
	public void setVersion(double x) { config = plugin.getConfig(); config.set("Version", x); }
	public void setGlobalAnnouncementsEnabled(boolean x) { config = plugin.getConfig(); config.set("Setting.GlobalAnnouncementsEnabled", x); plugin.saveConfig(); }
	public void setAutoShowWarningList(boolean x) { config = plugin.getConfig(); config.set("Setting.AutoShowWarningList", x); plugin.saveConfig(); }
	public void setDisplayWarnGiverNameOnPunish(boolean x) { config = plugin.getConfig(); config.set("Setting.DisplayWarnGiverNameOnPunish", x); }
	public void setPurgeAllMultipleAccountUsers(boolean x) { config = plugin.getConfig(); config.set("Setting.PurgeAllMultipleAccountUsers", x); }
	public void setPreventMultiAccounting(boolean x) { config = plugin.getConfig(); config.set("Setting.PreventMultiAccounting",x); }
	
	public void setLowWarningLevel(int x) { config = plugin.getConfig(); config.set("Setting.LowWarningLevel",x); }
	public void setMediumWarningLevel(int x) { config = plugin.getConfig(); config.set("Setting.MediumWarningLevel",x); }
	public void setHighWarningLevel(int x) { config = plugin.getConfig(); config.set("Setting.HighWarningLevel",x); }
	public void setExtremeWarningLevel(int x) { config = plugin.getConfig(); config.set("Setting.ExtremeWarningLevel",x); }
	public void setWarningLevelWarn(int x) { config = plugin.getConfig(); config.set("Setting.WarningLevelWarn",x); }
	public void setWarningLevelKick(int x) { config = plugin.getConfig(); config.set("Setting.WarningLevelKick",x); }
	public void setWarningLevelBan(int x) { config = plugin.getConfig(); config.set("Setting.WarningLevelBan",x); }
	public void setWarningLevelMute(int x) { config = plugin.getConfig(); config.set("Setting.WarningLevelMute",x); }
	public void setWarningLevelFreeze(int x) { config = plugin.getConfig(); config.set("Setting.WarningLevelFreeze",x); }
	public void setWarnBonusPerDayBan(int x) { config = plugin.getConfig(); config.set("Setting.WarnBonusPerDayBan",x); }
	public void setWarnBonusPerDayMute(int x) { config = plugin.getConfig(); config.set("Setting.WarnBonusPerDayMute",x); }
	public void setWarnBonusPerDayFreeze(int x) { config = plugin.getConfig(); config.set("Setting.WarnBonusPerDayFreeze",x); }
	
	public void setBlockedCommands(List<String> x) { config = plugin.getConfig(); config.set("Setting.BlockedCommands", x); }
	public void setBanKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.BanKeyWord", x); }
	public void setMuteKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.MuteKeyWord", x); }
	public void setKickKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.KickKeyWord", x); }
	public void setWarnKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.WarnKeyWord", x); }
	public void setFreezeKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.FreezeKeyWord", x); }
	public void setCheckKeyWord(String x) { config = plugin.getConfig(); config.set("Setting.CheckKeyWord", x); }
	public void setLogAllPlayerCommands(boolean x) { config = plugin.getConfig(); config.set("Setting.LogAllPlayerCommands", x); }
	public void setShowBannedPlayersAttemptedLogins(boolean x) { config = plugin.getConfig(); config.set("Setting.ShowBannedPlayersAttemptedLogins", x); plugin.saveConfig(); }
	public void setAutoPunishType(int x) { config = plugin.getConfig(); config.set("Setting.AutoPunishType", x); }
	public void setAutoPunishLength(String x) { config = plugin.getConfig(); config.set("Setting.AutoPunishLength", x); }
	
	public void setStoreAllPlayerIps(boolean x) { config = plugin.getConfig(); config.set("Setting.StoreAllPlayerIps", x); }
	public void setDebugMode(boolean x) { config = plugin.getConfig(); config.set("Setting.debugMode", x); }
	
	//Basically creates default settings for when the plugin first runs.
	public void handleConfiguration()
	{
		//Get configuration file
		config = plugin.getConfig();
		
		//Update config files to 0.4
		if (getVersion() < 0.4)
		{
			//Header for configuration file
			config.options().header("These are configuration variables");
			
			//Set the new version
			setVersion(0.4);
			
			//Enable the feature automatic enable.
			setGlobalAnnouncementsEnabled(true);
			
			//Enable the feature automatic enable.
			setAutoShowWarningList(true);
		}
		
		// Upgrade to 0.42
		if (config.getDouble("Version") < 0.51)
		{
			//Set the new version
			setVersion(0.51);
			
			//Set new globals.
			setDisplayWarnGiverNameOnPunish(true);
			setPurgeAllMultipleAccountUsers(false);
			setPreventMultiAccounting(true);
			
			//Set up warning settings.
			setLowWarningLevel(12);
			setMediumWarningLevel(24);
			setHighWarningLevel(36);
			setExtremeWarningLevel(48);
			
			setWarningLevelWarn(1);
			setWarningLevelKick(2);
			setWarningLevelMute(3);
			setWarningLevelBan(4);
			setWarnBonusPerDayBan(2);
			setWarnBonusPerDayMute(1);
		}
		
		if (config.getDouble("Version") < 0.53)
		{
			setVersion(0.53);
			
			setWarnBonusPerDayMute(1);
			setWarnBonusPerDayBan(2);
		}
		
		if (config.getDouble("Version") < 0.7)
		{
			setVersion(0.7);
			
			//New blocked commands feature, store blocked commands.
			List<String> x = new ArrayList<String>();
			
			x.add("/msg");
			x.add("/m");
			x.add("/t");
			x.add("/tell");
			x.add("/etell");
			x.add("/whisper");
			x.add("/ewhisper");
			x.add("/mail");
			x.add("/email");
			x.add("/f desc");
			
			setBlockedCommands(x);
			
			//New configurable command names feature.
			setBanKeyWord("Ban");
			setMuteKeyWord("Mute");
			setKickKeyWord("Kick");
			setWarnKeyWord("Warn");
			
			setLogAllPlayerCommands(true);
			
			//New configurable option, blocked command while muted tempban.
			setAutoPunishType(1);
			setAutoPunishLength("5m");
		}
		
		//Update to 0.8
		if (config.getDouble("Version") < 0.81)
		{
			setVersion(0.81);
			
			//Transfer all player data to the new file storage system.
			PunishmentManager pm;
			
			for (OfflinePlayer player : Bukkit.getOfflinePlayers())
			{
				pm = new PunishmentManager(player.getName());
				
				pm.transferPlayerData();
			}
			
			FileConfiguration config = FC_Bans.plugin.getConfig();
			
			//Delete old warnings section.
			config.set("Warnings", null);
			
			//New Configuration option.
			setStoreAllPlayerIps(true);
		}
		
		if (config.getDouble("Version") < 1.0)
		{
			setVersion(1.0);
			
			setWarningLevelFreeze(0);
			setWarnBonusPerDayFreeze(1);
		}
		
		if (config.getDouble("Version") < 1.11)
		{
			setVersion(1.11);
			
			setFreezeKeyWord("freeze");
			setCheckKeyWord("check");
			setShowBannedPlayersAttemptedLogins(true);
		}
		
		if (config.getDouble("Version") < 1.2)
		{
			setVersion(1.2);
			
			//Store new debug mode.
			setDebugMode(false);
			
			//Transfer all player data to the new file storage system.
			PunishmentManager pm;
			
			for (OfflinePlayer player : Bukkit.getOfflinePlayers())
			{
				pm = new PunishmentManager(player.getName());
				pm.transferPlayerData2();
			}
		}
		
		//Save config
		plugin.saveConfig();
	}
	
	public boolean handleMultipleAccountUsers(String name, String ip) 
	{
		PunishmentManager firstRecord = new PunishmentManager(name);
		PunishmentManager secondRecord;
		boolean isBanned = false;
		String name2;
		
		//We want to replace all dots in the ip with stars.
		ip = ip.replace(".", "*");
		
		//For the ip given, we want to see if it already has a reserved name. If it has one, then...
		if (getName(ip) != null)
		{
			//Store that reserved name.
			name2 = getName(ip);
			
			//If the names are the same, then it is safe to let the player play.
			if (name2.equals(name))
				return false;
			
			//But if the names AREN'T the same, a value will never get returned, and thus, we have a multi-accounter.
			
			//Create a new punishment manager to check the record of this other name.
			secondRecord = new PunishmentManager(name2);
			
			//If auto-purge is enabled, purge those bastards!
			if (getPurgeAllMultipleAccountUsers() == true)
				isBanned = true;
			else
			{
				//We want to see if the other ip has current mute/ban. If he does, ban for multi-accounting. HUE, HUE, HUE.
				if (secondRecord.isBanned() == true)
					isBanned = true;
				
				if (secondRecord.isMuted() == true)
					isBanned = true;
			}
		}
		
		//Else, for new ips, we want to check them to see if the a player already exists with that ip.
		if (isBanned == true)
		{
			firstRecord.punishPlayer(1, "permanent", "[Automatic Detection]", "[Server]");
			return true;
		}
		else
		{
			setName(ip, name);
		}
		
		plugin.saveConfig();
		
		return false;
	}
}








