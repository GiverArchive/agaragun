package me.giverplay.agaragun;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements CommandExecutor
{
	protected Inventory lixo = Bukkit.createInventory(null, 54, "§4§lLixeira");
	
	@Override
	public void onEnable(){
		Bukkit.getConsoleSender().sendMessage("§a[GiverPlay] Iniciando plugin");
		
		new ComandoTPA(this);
		new ListenerPrevents(this);
		new ListenerChat(this);
		new Listeners(this);
		new ComandoLixo(this);
		new ComandoReiniciar(this);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (int i = 53; i > -1; i--) {
					if (lixo.getItem(i) != null) {
						ItemStack item = lixo.getItem(i).clone();
						if (item != null) {
							if (!item.getType().equals(Material.AIR)) {
								lixo.setItem(i, new ItemStack(Material.AIR));
								if (!((i + 9) > 53)) {
									lixo.setItem(i + 9, item);
								}
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 5, 5);
		
		for(World w : Bukkit.getWorlds()){
			w.setDifficulty(Difficulty.HARD);
			Bukkit.broadcastMessage("§aDificuldade de mundo §f" + w.getName() + " §adefinida para §fDifficulty.HARD");
		}
	}
	
	@Override
	public void onDisable(){
		Bukkit.getConsoleSender().sendMessage("§a[GiverPlay] Desativando plugin");
	}

	protected boolean hasPerm(CommandSender player) {
		return player.getName().equals("GiverPlay007") || player.getName().equalsIgnoreCase("LaTryaa_");
	}
}
