package me.giverplay.agaragun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements CommandExecutor, Listener
{
	protected Inventory lixo = Bukkit.createInventory(null, 54, "§4§lLixeira");
	protected Inventory regions = Bukkit.createInventory(null, 54, "§4RPs");
	private List<String> cama = new ArrayList<>();
	
	@Override
	public void onEnable(){
		Bukkit.getConsoleSender().sendMessage("§a[GiverPlay] Iniciando plugin");
		
		new TPA(this);
		new Prevents(this);
		new Chat(this);
		
		getCommand("rps").setExecutor(new RPS(this));
		getCommand("lixo").setExecutor(new Lixo(this));
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		setupRPS();
		
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
	
	protected void setupRPS()
	{
		if(!getConfig().isSet("rps")){
			getConfig().set("rps.0.name", "Exemplo");
			getConfig().set("rps.0.icon", "STONE");
			getConfig().set("rps.0.cmd", "rp tp exemplo world");
			saveConfig();
		}
		
		for(String s : getConfig().getConfigurationSection("rps").getKeys(false)){
			ItemStack item = new ItemStack(Material.matchMaterial(getConfig().getString("rps." + s + ".icon")));
			
			ItemMeta meta = item.getItemMeta();
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			
			meta.setDisplayName("§a" + getConfig().getString("rps." + s + ".name"));
			meta.setLore(Arrays.asList(" ", "§cClique para teleportar", " "));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			
			item.setItemMeta(meta);
			
			regions.setItem(Integer.valueOf(s), item);
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
	public void onClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		
		if(player.getOpenInventory().getTitle() == "§4RPs"){
			event.setCancelled(true);
			if(getConfig().isSet("rps." + String.valueOf(event.getSlot()))){
				String cmd = getConfig().getString("rps." + event.getSlot() + ".cmd");
				
				if(cmd != null){
					Bukkit.dispatchCommand(player, cmd);
				}
			}
		}
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
	
	@EventHandler
	public void onLaunch(PlayerInteractEvent e){
		Player player = e.getPlayer();
		
		if(!hasPerm(player)) return;
		
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) && 
				player.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
			
			Location loc = player.getLocation().clone();
			loc.setY(loc.getY() + 1.5D);
			
			Arrow arrow = player.getWorld().spawnArrow(loc, new Vector(0, 0, 0), 1, 1);
			arrow.setVelocity(player.getVelocity().multiply(10D));
			
			arrow.setShooter(player);
		}
	}
	
	@EventHandler
	public void rain(WeatherChangeEvent e){
		e.setCancelled(true);
	}
}
