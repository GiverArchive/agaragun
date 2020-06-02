package me.giverplay.agaragun;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ComandoLixo implements CommandExecutor
{
	Main plugin;
	public ComandoLixo(Main plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args)
	{
		if(!(sender instanceof Player)){
			sender.sendMessage("§cConsole tolo");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			player.openInventory(plugin.lixo);
		} else
			if(args[0].toLowerCase().contains("pena") && player.hasPermission("lixo.admin")){
				ItemStack item = new ItemStack(Material.FEATHER);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§4Limpar");
				meta.setLore(Arrays.asList(" ", " §4§lCuidado!", "§cIsso vai limpar o baú", "§cao clicar com o direito", " "));;
				item.setItemMeta(meta);
				
				player.getInventory().addItem(item);
			}
		
		return false;
	}
}

