package me.Destro168.FC_Bans.Utils;

import me.Destro168.FC_Suite_Shared.PermissionManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FC_BansPermissions extends PermissionManager
{
	public FC_BansPermissions(Player player_)
	{
		super(player_);
	}
	
	public FC_BansPermissions(boolean isConsole)
	{
		super(isConsole);
	}
	
	public boolean isAdmin()
	{
		if (isGlobalAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.admin"))
			return true;
		
		return isConsole;
	}
	
	public boolean isUser()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.user"))
			return true;
		
		return isConsole;
	}
	
	//Returns true if immune and false if not immune.
	public boolean isImmune(String name)
	{
		//Variable declarations
		Player player = Bukkit.getServer().getPlayer(name);
		
		//If console then the player isn't immune.
		if (isConsole == true)
			return false;
		
		//Return false if the player is null.
		if (player == null)
			return false;
		
		//If the player is an admin return true for being an admin.
		if (player.isOp() == true)
			return true;
		
		//If the person is immune return true.
		if (permission.has(player, "FC_Bans.immune"))
			return true;
		
		//By default not immune
		return false;
	}
	
	public boolean canViewJoinWarnings()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.viewjoinwarnings"))
			return true;
		
		return isConsole;
	}
	
	//Check for permission to use commands
	public boolean canBan()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.ban"))
			return true;
		
		return isConsole;
	}
	
	public boolean canBanCheck()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canBan() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.ban.check"))
			return true;
		
		return isConsole;
	}
	
	public boolean canBanRemove()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canBan() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.ban.remove"))
			return true;
		
		return isConsole;
	}
	
	public boolean canMute()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.mute"))
			return true;
		
		return isConsole;
	}
	
	public boolean canMuteCheck()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canMute() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.mute.check"))
			return true;
		
		return isConsole;
	}
	
	public boolean canMuteRemove()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canMute() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.mute.remove"))
			return true;
		
		return isConsole;
	}
	
	public boolean canCheck()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.check"))
			return true;
		
		return isConsole;
	}
	
	public boolean canKick()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.kick"))
			return true;
		
		return isConsole;
	}
	
	public boolean canWarn()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.warn"))
			return true;
		
		return isConsole;
	}
	
	public boolean canWarnCheck()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canWarn() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.warn.check"))
			return true;
		
		return isConsole;
	}
	
	public boolean canWarnRemove()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canWarn() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.warn.remove"))
			return true;
		
		return isConsole;
	}
	
	public boolean canFreeze()
	{
		if (isAdmin() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.freeze"))
			return true;
		
		return isConsole;
	}
	
	public boolean canFreezeCheck()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canFreeze() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.freeze.check"))
			return true;
		
		return isConsole;
	}
	
	public boolean canFreezeRemove()
	{
		if (isAdmin() == true)
			return true;
		
		else if (canFreeze() == true)
			return true;
		
		else if (permission.has(player, "FC_Bans.freeze.remove"))
			return true;
		
		return isConsole;
	}
}

















