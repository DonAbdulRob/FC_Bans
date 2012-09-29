package me.Destro168.FC_Bans;

import java.util.List;
import java.util.logging.Logger;

import me.Destro168.FC_Bans.Commands.RainbowCE;
import me.Destro168.FC_Bans.Utils.ConfigSettingsManager;
import me.Destro168.FC_Bans.Utils.FC_BansPermissions;
import me.Destro168.Messaging.MessageLib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FC_Bans extends JavaPlugin 
{
	public final Logger log = Logger.getLogger("Minecraft");
	FileConfiguration config;
	ConfigSettingsManager csm;
	
	public static FC_Bans plugin;
	public static FreezeManager fm;
	
	private RainbowCE rainbowExecutor;
	
	@Override
	public void onDisable()
	{
		this.log.info("[FC_Bans] Disabled Successfully.");
	}
	
	@Override
	public void onEnable()
	{
		//Initialize variables.
		plugin = this;
		csm = new ConfigSettingsManager();
		fm = new FreezeManager();
		
		//Handle configuration
		csm.handleConfiguration();
		
		//Handle freezes.
		fm.startPlayerFreezes();
		
		//Register all commands.
		rainbowExecutor = new RainbowCE();
		getCommand(csm.getBanKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand(csm.getMuteKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand(csm.getKickKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand(csm.getWarnKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand(csm.getFreezeKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand(csm.getCheckKeyWord()).setExecutor((CommandExecutor) rainbowExecutor);
		getCommand("fc_bans").setExecutor((CommandExecutor) rainbowExecutor);
		
		//Register Listeners
		getServer().getPluginManager().registerEvents(new PlayerPreLogonListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerCommandListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
		getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
		getServer().getPluginManager().registerEvents(new PlayJoinEvent(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		
		//Start task to MOVE ALL OF THE PLAYERS! MUAHAHA
		this.log.info("[FC_Bans] Enabled Successfully.");
	}
	
	//Prevent banned players from logging in.
	public class PlayerPreLogonListener implements Listener
	{
		ConfigSettingsManager csm;
		private String name;
		PunishmentManager pm;
		
		public PlayerPreLogonListener() { }
		
		@EventHandler
		public void onPlayerPreLogin(PlayerPreLoginEvent event)
		{
			//Variable Declarations
			name = event.getName();
			pm = new PunishmentManager(name);
			csm = new ConfigSettingsManager();
			
			//If the player is banned, then...
			if (pm.isBanned() == true)
			{
				//If we want to show banned players warnings when they attempt to login, then...
				if (csm.getShowBannedPlayersAttemptedLogins() == true)
					showJoinWarning(0);
				
				if (pm.getIsPermaBanned() == true)
				{
					event.disallow(Result.KICK_BANNED, "You are permanently banned!");
					return;
				}
				else
				{
					event.disallow(Result.KICK_BANNED, "You are banned until " + pm.getUnbanDateNormal() + "!");
					return;
				}
			}
			
			//Do a multiaccount check
			if (csm.getPreventMultiAccounting())
			{
				//If they are a multi-accounter, then they were banned, so...
				if (csm.handleMultipleAccountUsers(event.getName(), event.getAddress().toString()) == true)
				{
					if (csm.getShowBannedPlayersAttemptedLogins() == true)
						showJoinWarning(0);
					
					event.disallow(Result.KICK_BANNED, "You are permanently banned!");
					
					return;
				}
			}
			
			//Ammend the warning based on what they are.
			if (pm.isMuted() == true)
			{
				showJoinWarning(1);
				
				if (pm.isFrozen() == true)
					messageViewers(name + " is also frozen.");
			}
			else if (pm.isFrozen() == true)
				showJoinWarning(2);
			else
				showJoinWarning(-1);
		}
		
		private void showJoinWarning(int punishType)
		{
			String playerJoinMessage = "";
			
			playerJoinMessage += name + " - Status: ";
			
			if (punishType == 0)
				playerJoinMessage += "Banned";
			else if (punishType == 1)
				playerJoinMessage += "Muted";
			else if (punishType == 2)
				playerJoinMessage += "Frozen";
			else
				playerJoinMessage += "Normal";
			
			playerJoinMessage += " - Warning Level: " + pm.getTotalWarnLevel();
			
			//Message the warning to players with the permission.
			messageViewers(playerJoinMessage);
		}
		
		private void messageViewers(String message)
		{
			FC_BansPermissions perms;
			MessageLib msgLib;
			
			//Message the warning to players with the permission.
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				perms = new FC_BansPermissions(player);
				
				if (perms.canViewJoinWarnings() == true)
				{
					msgLib = new MessageLib(player);
					msgLib.standardMessage(message);
				}
			}
			
			//Log the join message.
			FC_Bans.plugin.getLogger().info(message);
		}
	}
	
	public class PlayJoinEvent implements Listener
	{
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event)
		{
			fm.startPlayerFreeze(event.getPlayer());
		}
	}
	
	public class PlayerQuitListener implements Listener
	{
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event)
		{
			fm.stopPlayerFreezeTask(event.getPlayer().getName());
		}
	}
	
	//Log all player commands and stop muted players from communicating.
	public class PlayerCommandListener implements Listener
	{
		public PlayerCommandListener() { }
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerCommand(PlayerCommandPreprocessEvent event)
		{
			Player player = event.getPlayer();
			PunishmentManager pm = new PunishmentManager(player.getName());
			List<String> blockedCommands = csm.getBlockedCommands();
			String message = event.getMessage();
			boolean messageHasblockedCommand = false;
			String kickMessage;
			
			//See if the message contains one of the blocked commands.
			for (int i = 0; i < blockedCommands.size(); i++)
			{
				if (message.startsWith(blockedCommands.get(i) + " "))
				{
					plugin.getLogger().info(blockedCommands.get(i));
					
					messageHasblockedCommand = true;
					i = blockedCommands.size();
				}
			}
			
			//If it doesn't, return.
			if (messageHasblockedCommand == false)
				return;
			
			//If the person sending the message is muted, we want to log the message and tell the person not to chat.
			if (pm.isMuted() == true)
			{
				//Log command blcoked.
				FC_Bans.plugin.getLogger().info("[Command Blocked - " + player.getName() + "] " + event.getMessage());
				
				if (pm.getIsPermaMuted() == true)
					kickMessage = "[Blocked Command] You are permanently muted.";
				else
					kickMessage = "[Blocked Command] You are muted until " + pm.getUnmuteDateNormal() + "!";
				
				//Kick and send kick message.
				player.kickPlayer(kickMessage);
				
				//Apply a new punishment to the player.
				if (!csm.getAutoPunishLength().equals("0"))
					pm.punishPlayer(csm.getAutoPunishType(), csm.getAutoPunishLength(), "Using Blocked Command While Muted", "[Console]");
				
				//Cancel the event.
				event.setCancelled(true);
				
				//Return
				return;
			}
			
			//TODO <- FC_Bans -> Check for ability to tell people who are muted that the muted person is muted. Check for updates.
			/*
			//If the person chatting isn't muted, we have to intercept recievers of the message.
			for (Player recips : event.get)
			{
				//Create a new punishment manager for reciever.
				pm = new PunishmentManager(plugin, recips.getName());
				
				if (pm.isMuted() == true)
				{
					//Only send message if the message contains the recips name
					if (event.getMessage().toLowerCase().contains(pm.getName()))
						player.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Warning" + ChatColor.GRAY + "] " + ChatColor.YELLOW + recips.getName() + ChatColor.GRAY + " is muted.");
				}
			}
			*/
			
			if (csm.getLogAllPlayerCommands() == true)
				FC_Bans.plugin.getLogger().info("[Command - " + player.getName() + "] " + event.getMessage());
		}
	}
	
	//Block all chat by muted players.
	public class PlayerChatListener implements Listener
	{
		public PlayerChatListener() { }
		
		@EventHandler
		public void onPlayerChat(AsyncPlayerChatEvent event)
		{
			PunishmentManager pm = new PunishmentManager(event.getPlayer().getName());
			Player player = event.getPlayer();
			
			if (pm.isMuted() == true)
			{
				FC_Bans.plugin.getLogger().info("[Chat Blocked - " + player.getName() + "] " + event.getMessage());
				
				if (pm.getIsPermaMuted() == true)
					player.sendMessage(ChatColor.RED + "[Blocked] You are permanently muted.");
				else
					player.sendMessage(ChatColor.RED + "[Blocked] You are muted until " + pm.getUnmuteDateNormal() + "!");
				
				event.setCancelled(true);
			}
		}
	}
	
	//Catch players who are muted and stop them from placing signs.
	public class BlockPlaceListener implements Listener
	{
		public BlockPlaceListener() { }
		
		@EventHandler
		public void onBlockPlaceEvent(BlockPlaceEvent event)
		{
			Player player;
			boolean blocked = false;
			PunishmentManager pm;
			
			//If the event isn't a player, return null.
			if (event.getPlayer() == null)
				return;
			
			//Store player.
			player = event.getPlayer();
			
			//Create punishment manager.
			pm = new PunishmentManager(player.getName());
			
			if (pm.isMuted() == false)
				return;
			
			if (event.getBlock().getType() == Material.SIGN)
				blocked = true;
			else if (event.getBlock().getType() == Material.WALL_SIGN)
				blocked = true;
			else if (event.getBlock().getType() == Material.SIGN_POST)
				blocked = true;
			
			if (blocked == true)
			{
				FC_Bans.plugin.getLogger().info("[Sign Blocked - " + player.getName() + "] Attempted to put down a sign.");
				player.sendMessage(ChatColor.RED + "No placing signs while muted! No communicating!");
				event.setCancelled(true);
			}
		}
	}
}

/*

Version 0.4:

* Fixed issue where my command blocker for muted players was overriding all commands that started with the blocked commands.
* Modularized and optimized code massively. Was really having a ton of fun with it this time. Spent a few hours just trying to make the code the best it could be. Trying to get on dat Essentials Level, haha.
* Check for permanent punishements are now stored in configuration as a boolean.
* Bad duration input should be correctly handled and a nice message displayed on fails. Success = warning list will be shown.
* Fixed unban date not being correct.
* No more double-logging for blocked commands.
* Fixed /warn check [name] to actually work now.
* Confirmation given on /warn remove command.
* Warning list for /warn command is only shown in /warn [name] command now.
* Removed plugin.yml permissions. Were just yucking up my permissions making them not work.
* Lots of really good output updates for notifying players why they are banned, how long, when players are muted, how long, perma status, on and on.

Version 0.3:

I would like to first and foremost say that this version is HIGHLY EXPERIMENTAL. I have tested as much as I can, I really have, but I added so much stuff, that it is possible something isn't working. Anyway, there are tons of features in this version, as promised. Suggestions are welcome and please make tickets for bugs. Thanks :D

* New warning boolean - "type" which holds the type of ban. Time is also put in its own field. Warning level was moved down.
* Can't mute,ban already muted,banned players.
* New fc_bans.mute.check and fc_bans.ban.check fc_Bans.warn.check permissions.
* Standard error library created to optimize and sync code greatly.
* Updated warning list format to be a lot more useful now.
* Add warning delete sub-command, requires fc_bans.warn.delete to use.
* Updated the length that you can mute/ban for. Using a new far better method with Calenders that I should have just used, but was lazy. :P
* Any ban that is 52ws+ is considered permanent. If you want to unban a permanent ban then use the /unban command.
* /ban remove [name] command added. Requires fc_bans.ban.remove permission node.
* /mute remove [name] command added. Requires fc_bans.mute.remove permission node.
* New permission node, FC_Bans.user. Required to view help. Will have other uses later possibly.
* New permissions node, FC_Bans.immune. Makes you immune to kicks/bans/warns/mutes.
* New permission node, FC_Bans.admin. Gives access to every command. You still need the immune command with this, or you can just use fc_bans.*. Will be needed to change configuration settings with /fc_bans command.
* Configuration settings are now in, also made sure that userWarnings can be updated through config in the future.
* New Command fc_bans global to accompany global announcements setting. Needs fc_bans.admin to use and see in help.
* /ban ip [name] [duration] [reasons] is now usable.
* Can no longer message muted players either. A message is sent back telling the person that the person they are trying to chat to is muted.
* Brand new color scheme. :) Used the sexy GRAY + YELLOW SCHEME. Seeing that bold yellow WILL, and I mean WILL make you jizz yourself. 100% chance.
* Help command is made a lot better, use it... and use it often!
* Next version will offer a way to export bans, but for now nothing implemented. 

Please delete your configuration file when upgrading. You won't have to do this in the future unless I need to do major code rewrites, which even still, you probably wouldn't have to. :)

 */

/*
- Log all commands into console - done
- Warning - done
- Mute  - done
- Temporary/Permanent Bans - done
- Kick - done
- Block all signs on muted players.

- warn check
Tips for using this with Essentials:
Plugin.yml
If for some reason you find that Essentials is overriding the command of your favourite plugin, you can always remove the bind from the plugin.yml file located in the essentials.jar, this will tell essentials to not even to try to bind to this command. This option should only be tried if other avenues fail. 
*/

/*
 * Upcoming Features: When a player tries to speak and is muted it will show how long until unmuted.
*/






