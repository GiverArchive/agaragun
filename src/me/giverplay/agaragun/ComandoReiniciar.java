package me.giverplay.agaragun;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Código original por GuiHSilva
 */
public class ComandoReiniciar implements CommandExecutor, Listener {

  public static boolean reiniciando;
  public static boolean bloqueartudo;
  private static String msgblock = "§c * Nos últimos 30 segundos da reinicialização, não é possível interagir, para evitar a perda de itens!";
  private Main plugin;
  private BukkitTask task;

  public ComandoReiniciar(Main plugin) {
    this.plugin = plugin;

    plugin.getCommand("reiniciar").setExecutor(this);
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender.hasPermission("evolution.reiniciar")) {
      if (args.length == 1) {
        if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("cancelar")
                || args[0].equalsIgnoreCase("c")) {
          if (reiniciando) {
            task.cancel();
            for (Player p : Bukkit.getOnlinePlayers()) {
              Utils.sendAction(p, "§eA reinicialização foi cancelada!");
            }
            reiniciando = false;
            bloqueartudo = false;

            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(" §cO servidor não vai mais reiniciar!");
            Bukkit.broadcastMessage(" ");
            for (Player p : Bukkit.getOnlinePlayers()) {
              p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 3);
            }
            return true;
          } else {
            sender.sendMessage("§cO servidor não esta reiniciando...");
            return true;
          }
        }
      }
      if (args.length < 2) {
        sender.sendMessage("§cUso correto: /" + label + " [tempo] [s/m] <motivo>");
        return true;
      } else {
        if (reiniciando) {
          sender.sendMessage("§cO servidor já esta reiniciando... Para cancelar use §f/" + label + " c");
          return true;
        }

        if (args.length == 2) {
          int i;
          try {
            i = Integer.parseInt(args[0]);
          } catch (Exception e) {
            sender.sendMessage("§cO tempo deve ser em números inteiros!");
            return true;
          }
          if (i < 0) {
            sender.sendMessage("§cO tempo não pode ser negativo!");
            return true;
          }

          if (args[1].equalsIgnoreCase("s") || args[1].contains("second") || args[0].contains("segundo")) {
            if (i <= 35) {
              sender.sendMessage("§cO tempo deve maior que 35 segundos!");
              return true;
            }
            if (i > 1200) {
              sender.sendMessage("§cO tempo não pode ser maior que 20 minutos!");
              return true;
            }
            reiniciarServidor(i, null);
          }
          if (args[1].equalsIgnoreCase("m") || args[1].contains("minute") || args[0].contains("minuto")) {
            if ((i * 60) > 1200) {
              sender.sendMessage("§cO tempo não pode ser maior que 20 minutos!");
              return true;
            }
            reiniciarServidor(i * 60, null);
          }
        } else if (args.length > 2) {

          String msg = "";
          for (int i = 2; i < args.length; i++) {
            msg = msg + args[i] + " ";
          }
          int i;
          try {
            i = Integer.parseInt(args[0]);
          } catch (Exception e) {
            sender.sendMessage("§cO tempo deve ser em números inteiros!");
            return true;
          }
          if (i < 0) {
            sender.sendMessage("§cO tempo não pode ser negativo!");
            return true;
          }
          if (args[1].equalsIgnoreCase("s") || args[1].contains("second") || args[0].contains("segundo")) {
            if (i < 35) {
              sender.sendMessage("§cO tempo deve maior que 35 segundos!");
              return true;
            }
            if (i > 1200) {
              sender.sendMessage("§cO tempo não pode ser maior que 20 minutos!");
              return true;
            }
            reiniciarServidor(i, msg);
          }
          if (args[1].equalsIgnoreCase("m") || args[1].contains("minute") || args[0].contains("minuto")) {
            if ((i * 60) > 1200) {
              sender.sendMessage("§cO tempo não pode ser maior que 20 minutos!");
              return true;
            }
            reiniciarServidor(i * 60, msg);
          }

        }
      }
    } else {
      sender.sendMessage("§cSem permissão!");
    }
    return false;
  }

  private void reiniciarServidor(int i, String motivo) {
    reiniciando = true;
    long time = System.currentTimeMillis();
    long newtime = time + (i * 1000);

    int seconds = (int) ((newtime - System.currentTimeMillis()) / 1000);
    long minutes = (((newtime - System.currentTimeMillis()) / 1000) / 60);
    String tempo = "";
    if (minutes > 0) {
      tempo = minutes + " minuto" + (minutes > 0 ? "s" : "");
    } else {
      tempo = seconds + " segundos ";
    }

    Bukkit.broadcastMessage(" ");
    Bukkit.broadcastMessage(" §6O servidor vai reiniciar em §e" + tempo);

    if (motivo != null) {
      Bukkit.broadcastMessage(" §6Motivo: §e" + motivo.replaceAll("&", "§"));
    }

    Bukkit.broadcastMessage(" ");
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.playSound(p.getLocation(), Sound.MUSIC_DISC_WAIT, 10, 3);
    }
    task = new BukkitRunnable() {
      @Override
      public void run() {

        int a = (int) ((newtime - System.currentTimeMillis()) / 1000);
        int percent = (int) ((newtime - System.currentTimeMillis()) / 1000) * 100 / 100;
        if (percent == 0 || a == 0 || percent < 0 || a < 0) {
          for (Player p : Bukkit.getOnlinePlayers()) {
            Utils.sendAction(p, "§aReinicializando o servidor!!");
          }
          task.cancel();

          for (World w : Bukkit.getWorlds()) {
            w.save();
          }

          Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kickall");

          new BukkitRunnable() {
            @Override
            public void run() {
              Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }
          }.runTaskLater(plugin, 20);
        } else {
          if (percent == 10 || percent == 5 || percent == 4 || percent == 3 || percent == 2 || percent == 1
                  || percent == 30) {

            for (Player p : Bukkit.getOnlinePlayers()) {
              Utils.sendTitle(p, "Reiniciando em " + percent + " segundos", " ", 2, 3, 2);
              p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, -0);
            }
            bloqueartudo = true;
          }

          for (Player p : Bukkit.getOnlinePlayers()) {
            Utils.sendAction(p, "§aReiniciando em: §f" + Utils.createTimerLabel((int) ((System.currentTimeMillis() - newtime) / 1000)));
          }
        }
      }
    }.runTaskTimer(plugin, 0, 20);
  }

  @EventHandler
  private void bla(BlockPlaceEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getPlayer())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(msgblock);
        e.getPlayer().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla(PlayerCommandPreprocessEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getPlayer())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(msgblock);
        e.getPlayer().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla(PlayerInteractEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getPlayer())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(msgblock);
        e.getPlayer().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla(EntityDamageEvent e) {
    if (bloqueartudo) {
      if (!e.getEntity().isOp()) {
        e.setCancelled(true);
        e.getEntity().sendMessage(" ");
        e.getEntity().sendMessage(msgblock);
        e.getEntity().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla(EntityDamageByEntityEvent e) {
    if (bloqueartudo) {
      if (!e.getDamager().isOp()) {
        e.setCancelled(true);
        e.getDamager().sendMessage(" ");
        e.getDamager().sendMessage(msgblock);
        e.getDamager().sendMessage(" ");

      }
    }
  }

  @EventHandler
  private void bla2(BlockBreakEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getPlayer())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(msgblock);
        e.getPlayer().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla3(PlayerDropItemEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getPlayer())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(msgblock);
        e.getPlayer().sendMessage(" ");
      }
    }
  }

  @EventHandler
  private void bla(InventoryInteractEvent e) {
    if (bloqueartudo) {
      if (!plugin.hasPerm(e.getWhoClicked())) {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(" ");
        e.getWhoClicked().sendMessage(msgblock);
        e.getWhoClicked().sendMessage(" ");
      }
    }
  }
}
