package me.Destro168.FC_Bans;

import java.util.List;
import java.util.logging.Logger;

import me.Destro168.FC_Bans.Commands.RainbowCE;
import me.Destro168.FC_Bans.Utils.ConfigSettingsManager;
import me.Destro168.FC_Bans.Utils.FC_BansPermissions;
import me.Destro168.FC_Suite_Shared.Messaging.MessageLib;

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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
		this.getLogger().info("Disabled Successfully.");
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
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		
		//Start task to MOVE ALL OF THE PLAYERS! MUAHAHA
		this.getLogger().info("Enabled Successfully.");
	}
	
	//Prevent banned players from logging in.
	public class PlayerPreLogonListener implements Listener
	{
		ConfigSettingsManager csm;
		private String name;
		PunishmentManager pm;
		
		public PlayerPreLogonListener() { }
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
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
					//Create the kick reason.
					if (csm.getDisplayWarnGiverNameOnPunish() == true)
						event.setKickMessage("You were permanently banned by " + pm.getMostRecentBanGiver() + " for " + pm.getMostRecentBanReason() + "!");
					else
						event.setKickMessage("You are permanently banned.");
					
					//Actually kick the player.
					event.setLoginResult(Result.KICK_BANNED);
					
					return;
				}
				else
				{
					//Create the kick reason.
					if (csm.getDisplayWarnGiverNameOnPunish() == true)
						event.setKickMessage("You were banned until " + pm.getUnbanDateNormal() + " by " + pm.getMostRecentBanGiver() + " for " + pm.getMostRecentBanReason() + "!");
					else
						event.setKickMessage("You are banned until " + pm.getUnbanDateNormal() + "!");

					//Actually kick the player.
					event.setLoginResult(Result.KICK_BANNED);
					
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
					
					//Create the kick reason.
					event.setKickMessage("You are permanently banned.");

					//Actually kick the player.
					event.setLoginResult(Result.KICK_BANNED);
					
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
			
			//Store players ip.
			pm.setIp(event.getAddress().getHostAddress());
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
	
	public class PlayerJoinListener implements Listener
	{
		public void onPlayerJoin(PlayerJoinEvent event)
		{
			//Begin player freezing.
			fm.startPlayerFreeze(event.getPlayer());
		}
	}
	
	public class PlayerQuitListener implements Listener
	{
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event)
		{
			//Variable Declarations
			Player eventPlayer = event.getPlayer();
			
			//End the freeze task (if one exists).
			fm.stopPlayerFreezeTask(eventPlayer.getName());
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
					kickMessage = "[Blocked] You are permanently muted.";
				else
					kickMessage = "[Blocked] You are muted until " + pm.getUnmuteDateNormal() + "!";
				
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






