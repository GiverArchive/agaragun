package me.giverplay.agaragun;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener
{
  private Main plugin;

  public Listeners(Main plugin)
  {
    this.plugin = plugin;

    Bukkit.getPluginManager().registerEvents(this, plugin);
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

  private void handle(EntityDamageByEntityEvent e){
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
    }.runTaskTimer(plugin, 10, 10);
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
}
