package me.giverplay.agaragun;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RPS implements CommandExecutor
{
	private Main plugin;
	
	public RPS(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("§4Tolo");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			player.openInventory(plugin.regions);
			return true;
		}
		
		if(!player.hasPermission("redprotect.admin")){
			player.sendMessage("§cTsk tsk tsk... Utilize apenas /rps");
			return true;
		}
		
		try{
			if(args[0].toLowerCase().contains("remove")){
				if(!plugin.getConfig().isSet("rps." + args[1])){
					player.sendMessage("§cRP não definida nas configs");
					return true;
				}
				
				plugin.getConfig().set("rps." + args[1], null);
				plugin.saveConfig();
				plugin.setupRPS();
				
				player.sendMessage("§aPronto!");
				
				return true;
			}
			
			if(args[0].toLowerCase().contains("add")){
				String name = args[1];
				String w = args[2];
				int sl = Integer.parseInt(args[3]);
				Material mat = Material.matchMaterial(args[4].toUpperCase());
				
				plugin.getConfig().set("rps." + sl + ".name", name);
				plugin.getConfig().set("rps." + sl + ".icon", mat.name());
				plugin.getConfig().set("rps." + sl + ".cmd", "rp tp " + name + " " + w);
				plugin.saveConfig();
				
				plugin.setupRPS();
				player.sendMessage("§aPronto!");
				
				return true;
			}
		} catch(Exception e){
			player.sendMessage("§cSintaxe incorreta do comando");
			player.sendMessage("§c/rps add NomeDaRP nomeDoMundo slotNoMenu ICONE");
			player.sendMessage("§c/rp remove slot");
		}
		
		
		return true;
	}
	
	
}
