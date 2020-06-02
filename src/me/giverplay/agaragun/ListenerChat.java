package me.giverplay.agaragun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListenerChat implements CommandExecutor
{
	public ListenerChat(Main main)
	{
		main.getCommand("local").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args)
	{
		if(!(sender instanceof Player)){
			sender.sendMessage("Tolo, console");
			return true;
		}
		
		final Player player = (Player) sender;
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < args.length; i++){
			sb.append(args[i]);
			sb.append(" ");
		}
		
		String msg = sb.toString().trim();
		int p = 0;
		
		for(Player rec : player.getWorld().getPlayers()){
			if(rec.getLocation().distanceSquared(player.getLocation()) < 100.00D){
				rec.sendMessage("§e[Local] §7" + player.getName() + " §a>> " + msg);
				if(rec != player) p++;
			}
		}
		
		if(p == 0) player.sendMessage("§eNinguem proximo para ouvir");
		
		return false;
	}
}
