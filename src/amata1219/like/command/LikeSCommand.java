package amata1219.like.command;

import org.bukkit.command.CommandSender;

import amata1219.like.Util;

public class LikeSCommand implements CommandExecutor {

	@Override
	public void onCommand(CommandSender sender, Args args){
		if(!Util.isNotPlayer(sender))
			Util.status(Util.castPlayer(sender), true);
	}

}
