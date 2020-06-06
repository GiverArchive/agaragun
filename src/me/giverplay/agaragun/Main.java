package me.giverplay.agaragun;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor, Listener
{
	protected Inventory lixo = Bukkit.createInventory(null, 54, "§4§lLixeira");
	private List<String> cama = new ArrayList<>();
	
	@Override
	public void onEnable(){
		Bukkit.getConsoleSender().sendMessage("§a[GiverPlay] Iniciando plugin");
		
		new ComandoTPA(this);
		new ListenerPrevents(this);
		new ListenerChat(this);

		getCommand("lixo").setExecutor(new ComandoLixo(this));
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
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
	
	@EventHandler
	public void onSleep(PlayerBedEnterEvent e)
	{ 
		Player player = e.getPlayer();
		
		if(cama.contains(player.getName()))
		{
			player.sendMessage("§cVocê não pode mudar o tempo agora... Aguarde mais alguns segundos");
			return;
		}
		
		if(player.getWorld().getTime() < 12000L){
			return;
		}
		
		player.getWorld().setTime(0L);
		
		cama.add(player.getName());
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				cama.remove(player.getName());
			}
		}.runTaskLaterAsynchronously(this, 20 * 60);
		
		Bukkit.broadcastMessage("§a" + player.getName() + " §adormiu, agora é dia no mundo §f"
				+ e.getPlayer().getWorld().getName());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(!event.hasBlock()) return;
		Player player = event.getPlayer();
		
		if(player.getInventory().getItemInMainHand().getType() == Material.FEATHER
				&& player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("§4Limpar")){
			
			Block block = event.getClickedBlock();
			
			if(block.getType() == Material.CHEST){
				Chest chest = (Chest) block.getState();
				InventoryHolder holder = chest.getInventory().getHolder();
				
				if(holder instanceof DoubleChest){
					DoubleChest dc = (DoubleChest) holder;
					
					for(int i = 0; i < dc.getInventory().getSize(); i++){
						dc.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
					
					player.sendMessage("§cEste baú foi limpo!");
					
					return;
				}
				
				for(int i = 0; i < chest.getBlockInventory().getSize(); i++){
					chest.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
				
				player.sendMessage("§cEste baú foi limpo!");
			}
		}
	}
	
	protected boolean hasPerm(CommandSender player){
		return player.getName().equals("GiverPlay007") || player.getName().equalsIgnoreCase("LaTryaa_");
	}
	
	@EventHandler
	public void onArrow(EntityDamageByEntityEvent e){
		
		if(!(e.getDamager() instanceof Arrow)) return;
		
		ProjectileSource src = ((Arrow) e.getDamager()).getShooter();
		
		if(src instanceof Player){
			Player player = (Player) src;
			
			if(player.hasPermission("flecha.thor")){
				handle(e);
			}
		}
	}
	
	public void handle(EntityDamageByEntityEvent e){
		final Location loc = e.getEntity().getLocation();
		
		new BukkitRunnable()
		{
			int cont = 0;
			@Override
			public void run()
			{
				loc.getWorld().strikeLightning(loc);
				cont++;
				
				if(cont >= 5){
					Bukkit.getScheduler().cancelTask(this.getTaskId());
				}
			}
		}.runTaskTimer(this, 10, 10);
		
	}
}
