package me.giverplay.agaragun;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TPA implements CommandExecutor
{
	public static HashMap<Player, Player> tparequest = new HashMap<>();
	private static ArrayList<Player> delay = new ArrayList<>();
	private Main main;
	
	public TPA(Main plugin){
		this.main = plugin;
		
		main.getCommand("tpa").setExecutor(this);
		main.getCommand("tpanegar").setExecutor(this);
		main.getCommand("tpaceitar").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("tpa")){
			
			
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cO console não pode se teleportar ate um jogador!");
				return true;
			} else {
				
				if(!main.hasPerm((Player) sender)){
					sender.sendMessage("§cComando bloqueado");
					return true;
				}
				
				if (args.length == 0) {
					sender.sendMessage("§cUso correto: /tpa [Jogador]");
					return true;
				} else if (args.length > 1) {
					sender.sendMessage("§cUso correto: /tpa [Jogador]");
					return true;
				} else {
					Player target = Bukkit.getPlayer(args[0]);
					if (checkPlayers(sender, target)) {
						return true;
					} else {
						if (delay.contains((Player) sender)) {
							sender.sendMessage("§cAguarde 20 segundos para usar este comando.");
							return true;
						}
						
						sender.sendMessage("§eVocê enviou um pedido de teleporte a §6" + target.getName());
						target.sendMessage(" ");
						target.sendMessage(" §e" + sender.getName() + " §7deseja ir até você.");
						
						TextComponent tpamsg = new TextComponent("§7Clique");
						BaseComponent[] hoveraceitar = new ComponentBuilder("§aAceitar pedido de teleporte de " + sender.getName()).create();
						BaseComponent[] hovernegar = new ComponentBuilder("§cNegar pedido de teleporte de " + sender.getName()).create();
						
						TextComponent aquiaceitar = new TextComponent(" §a§l§nAQUI§7 ");
						aquiaceitar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveraceitar));
						aquiaceitar.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaceitar " + sender.getName()));
						
						TextComponent aquinegar = new TextComponent(" §c§l§nAQUI§7 ");
						aquinegar.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovernegar));
						aquinegar.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpanegar " + sender.getName()));
						
						TextComponent ou = new TextComponent("§7para aceitar ou");
						TextComponent ou2 = new TextComponent("§7para negar.");
						
						tpamsg.addExtra(aquiaceitar);
						tpamsg.addExtra(ou);
						tpamsg.addExtra(aquinegar);
						tpamsg.addExtra(ou2);
						
						target.spigot().sendMessage(tpamsg);
						
						target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
						target.sendMessage(" ");
						delay.add((Player) sender);
						tparequest.put((Player) sender, target);
						BukkitScheduler sh = Bukkit.getScheduler();
						sh.scheduleSyncDelayedTask(main, new Runnable() {
							@Override
							public void run() {
								if (tparequest.get((Player) sender) == target) {
									tparequest.remove((Player) sender);
									sender.sendMessage("§cO seu pedido de teleporte para " + target.getName() + " expirou!");
									target.sendMessage("§cO pedido de teleporte de " + sender.getName() + " expirou!");
								}
								delay.remove((Player) sender);
							}
						}, 20 * 20);
						
					}
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("tpaceitar")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cO console não pode fazer isso!");
				return true;
			} else {
				if (args.length == 0) {
					sender.sendMessage("§cUso correto: /tpaceitar [Jogador]");
					return true;
				} else if (args.length > 1) {
					sender.sendMessage("§cUso correto: /tpaceitar [Jogador]");
					return true;
				} else if (args.length == 1) {
					Player requester = Bukkit.getPlayer(args[0]);
					if (checkPlayers(sender, requester)) {
						return true;
					} else {
						if (tparequest.get(requester) == sender) {
							tparequest.remove(requester);
							requester.teleport((Player) sender);
							requester.sendMessage("§eSeu pedido de teleporte foi aceito");
							sender.sendMessage("§eO pedido de teleporte foi aceito");
						} else {
							sender.sendMessage("§cNenhum pedido de teleporte para aceitar!");
							return true;
						}
					}
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("tpanegar")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cO console não pode fazer isso!");
				return true;
			} else {
				if (args.length == 0) {
					sender.sendMessage("§cUso correto: /tpanegar [Jogador]");
					return true;
				} else if (args.length > 1) {
					sender.sendMessage("§cUso correto: /tpanegar [Jogador]");
					return true;
				} else if (args.length == 1) {
					Player requester = Bukkit.getPlayer(args[0]);
					if (checkPlayers(sender, requester)) {
						return true;
					} else {
						if (tparequest.get(requester) == sender) {
							tparequest.remove(requester);
							requester.sendMessage("§eSeu pedido de teleporte foi negado");
							sender.sendMessage("§eO pedido de teleporte foi negado");
						} else {
							sender.sendMessage("§cNenhum pedido de teleporte para negar!");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean checkPlayers(CommandSender sender, Player target) {
		if (target == null) {
			sender.sendMessage("§cJogador não encontrado! ");
			return true;
		}
		if (sender.getName().equalsIgnoreCase(target.getName())) {
			sender.sendMessage("§cNão pode ser você mesmo!");
			return true;
		}
		return false;
	}
}
