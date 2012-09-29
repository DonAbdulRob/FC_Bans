package me.Destro168.FC_Bans.Utils;

import me.Destro168.FC_Suite_Shared.ArgParser;

public class BanArgParser extends ArgParser
{
	public BanArgParser(String[] args_) 
	{
		super(args_);
	}
	
	//command [arg1] <-- default argument to skip, pass in 1 to skip it.
	public void setPunishReason(int argsToSkip_)
	{
		setLastArg(argsToSkip_);
		
		//If the person that input the command input permanent, then we want to remove it from the reason. Used in FC_Bans.
		finalArgument.replaceAll("[Permanent]", "Permanent");
		finalArgument.replaceAll("[Empty]", "Empty");
		
		//If the reason is empty, then we want to set it to [NONE]"
		if (finalArgument.equals(""))
			finalArgument = "None";
	}
}
