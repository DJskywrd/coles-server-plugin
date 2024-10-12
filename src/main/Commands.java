package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Commands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("locateplayer")) {
			if (args.length == 1) {
				if (!(Bukkit.getPlayer(args[0]) == null)) {
					Player plrToLocate = Bukkit.getPlayer(args[0]);
					sender.sendMessage(ChatColor.GREEN + plrToLocate.getName() + " has been located at " + 
							ChatColor.GOLD + plrToLocate.getLocation().getBlockX() + 
							" " + plrToLocate.getLocation().getBlockY() + 
							" " + plrToLocate.getLocation().getBlockZ());
					plrToLocate.sendMessage(ChatColor.DARK_RED + "You are being tracked by " + ChatColor.GOLD + sender.getName());
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid player.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No player entered.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("setantennaid")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Incorrect Usage.");
				} else {
					try {
						int id = Integer.parseInt(args[0]);
						if (id>0) {
							Player plr = (Player) sender;
							if (plr.getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Global Transmitter Antenna - ")) {
								ItemStack itemToChange = plr.getInventory().getItemInMainHand();
								ItemMeta itemMeta = itemToChange.getItemMeta();
								
								itemMeta.setDisplayName(ChatColor.GOLD + "Global Transmitter Antenna - " + args[0]);
								itemToChange.setItemMeta(itemMeta);
								sender.sendMessage(ChatColor.GREEN + "Successfully changed the ID to " + ChatColor.ITALIC + args[0]);
								return true;
							}
							
							if (plr.getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Global Receiver Antenna - ")) {
								ItemStack itemToChange = plr.getInventory().getItemInMainHand();
								ItemMeta itemMeta = itemToChange.getItemMeta();
								
								itemMeta.setDisplayName(ChatColor.GOLD + "Global Receiver Antenna - " + args[0]);
								itemToChange.setItemMeta(itemMeta);
								sender.sendMessage(ChatColor.GREEN + "Successfully changed the ID to " + ChatColor.ITALIC + args[0]);
								return true;
							}
							
							sender.sendMessage(ChatColor.RED + "Invalid item.");
						} else {
							sender.sendMessage(ChatColor.RED + "ID's can only be a positive number with no decimals");
						}
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "ID's can only be a positive number with no decimals");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Please use this command in game as a player.");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("serverreload")) {
			sender.getServer().reload();
		}
		return false;
	}
}
