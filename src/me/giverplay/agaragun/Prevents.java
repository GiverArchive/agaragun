package me.giverplay.agaragun;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Prevents implements Listener, CommandExecutor
{
	private Main plugin;
	
	private static ArrayList<String> cmds = new ArrayList<>();
	private static ArrayList<String> bypassses = new ArrayList<>();
	
	public Prevents(Main plugin)
	{
		plugin.getCommand("bypass").setExecutor(this);
		
		cmds.add("gamemode");
		cmds.add("gm");
		cmds.add("creativemode");
		cmds.add("survivalmode");
		cmds.add("spectatormode");
		cmds.add("survival");
		cmds.add("creative");
		cmds.add("spectator");
		cmds.add("adventure");
		cmds.add("adventuremode");
		cmds.add("give");
		cmds.add("fill");
		cmds.add("setblock");
		cmds.add("god");
		cmds.add("fly");
		cmds.add("godmode");
		cmds.add("heal");
		cmds.add("tp");
		cmds.add("teleport");
		cmds.add("tp2p");
		cmds.add("rp list");
		cmds.add("rp tp");
		cmds.add("rp teleport");
		cmds.add("op");
		cmds.add("deop");
		cmds.add("summon");
		cmds.add("spawnmob");
		cmds.add("spawnentity");
		cmds.add("spawnmonster");
		cmds.add("minecraft:");
		cmds.add("essentials:");
		cmds.add("worldedit:");
		cmds.add("//");
		cmds.add("schem");
		cmds.add("schematic");
		cmds.add("sp");
		cmds.add("superpickaxe");
		cmds.add("authme:");
		cmds.add("authme");
		
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandSend(ServerCommandEvent event){
		for(String s : cmds){
			if(event.getCommand().toLowerCase().startsWith(s) || event.getCommand().toLowerCase().startsWith("/" + s)){
				CommandSender sender = event.getSender();
				
				if(!plugin.hasPerm(sender) && !bypassses.contains(sender.getName())){
					sender.sendMessage("§cPor ordem, este comando está bloqueado, você não pode fazer uso dele, bobinho");
					event.setCancelled(true);
					
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandSendByPlayer(PlayerCommandPreprocessEvent event){
		for(String s : cmds){
			if(event.getMessage().toLowerCase().startsWith(s) || event.getMessage().toLowerCase().startsWith("/" + s)){
				Player sender = event.getPlayer();
				
				if(!plugin.hasPerm(sender) && !bypassses.contains(sender.getName())){
					sender.sendMessage("§cPor ordem, este comando está bloqueado, você não pode fazer uso dele, bobinho");
					event.setCancelled(true);
					
					return;
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args)
	{
		if(!plugin.hasPerm(sender)){
			sender.sendMessage("§cSem permissão");
			return true;
		}
		
		if(args.length == 0){
			sender.sendMessage("Especifique o nome do jogador");
			return true;
		}
		
		Player player;
		
		if((player = Bukkit.getPlayer(args[0])) == null){
			sender.sendMessage("§cJogador não encontrado");
			return true;
		}
		
		if(bypassses.contains(player.getName())){
			bypassses.remove(player.getName());
			sender.sendMessage("§cJogador " + player.getName() + " removido do bypass");
			return true;
		}
		
		bypassses.add(player.getName());
		sender.sendMessage("§aJogador " + player.getName() + " adicionado ao bypass");
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				bypassses.remove(player.getName());
				sender.sendMessage("§cJogador " + player.getName() + " removido do bypass");
			}
		}.runTaskLater(plugin, 20 * 300);
		
		return true;
	}
}
