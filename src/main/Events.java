package main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import main.wirelessredstone.WirelessRedstone;


public class Events implements Listener {
	JavaPlugin plugin = Main.getPlugin(Main.class);
	
	public void blockBroken(Block block) {
			if (!block.getMetadata("id").isEmpty()) {
				String id = block.getMetadata("id").get(0).asString();
				Location loc = block.getLocation();
				
				if (block.getType() == Material.REDSTONE_LAMP) {
					
					ItemStack customDrop = new ItemStack(Material.REDSTONE_LAMP);
					ItemMeta customMeta = customDrop.getItemMeta();
					
					customMeta.setDisplayName(ChatColor.GOLD + "Global Transmitter Antenna - " + id.replace("global_", ""));
					
					customDrop.setItemMeta(customMeta);
					block.getWorld().dropItemNaturally(loc, customDrop);
					
					WirelessRedstone.removeTransmitter(id, block.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				}
				
				if (block.getType() == Material.LIGHTNING_ROD) {
					
					ItemStack customDrop = new ItemStack(Material.LIGHTNING_ROD);
					ItemMeta customMeta = customDrop.getItemMeta();
					
					customMeta.setDisplayName(ChatColor.GOLD + "Global Receiver Antenna - " + id.replace("global_", ""));
					
					customDrop.setItemMeta(customMeta);
					block.getWorld().dropItemNaturally(loc, customDrop);
					
					WirelessRedstone.removeReceivers(id, block.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				}
			}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(ChatColor.GREEN + "i wish skibidi toilet rizz to " + ChatColor.GOLD + event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType() == Material.FIRE_CHARGE) {
				event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount()-1);
				event.getPlayer().getWorld().spawnEntity(event.getPlayer().getEyeLocation(), EntityType.FIREBALL);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		if (item.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Global Transmitter Antenna")) {
			String id = item.getItemMeta().getDisplayName().replace(ChatColor.GOLD + "Global Transmitter Antenna - ", "global_");
			Location loc = event.getBlock().getLocation();
			
			event.getBlock().setMetadata("id", new FixedMetadataValue(plugin, id));
			WirelessRedstone.addTransmitter(id, event.getBlock().getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		
		if (item.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Global Receiver Antenna")) {
			String id = item.getItemMeta().getDisplayName().replace(ChatColor.GOLD + "Global Receiver Antenna - ", "global_");
			Location loc = event.getBlock().getLocation();
			
			event.getBlock().setMetadata("id", new FixedMetadataValue(plugin, id));
			WirelessRedstone.addReceiver(id, event.getBlock().getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getBlock().getMetadata("id").isEmpty()) {
			event.setDropItems(false);
			blockBroken(event.getBlock());
		}
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		event.blockList().forEach(block -> blockBroken(block));
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().forEach(block -> blockBroken(block));
	}
	
	@EventHandler
	public void onRedstoneChange(BlockRedstoneEvent event) {
		if (!event.getBlock().getMetadata("id").isEmpty() && event.getBlock().getType() == Material.REDSTONE_LAMP) {
			String id = event.getBlock().getMetadata("id").get(0).asString();
			Location loc = event.getBlock().getLocation();
			
			if (event.getNewCurrent() > 0) {
				WirelessRedstone.updateTransmitter(id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getBlock().getWorld().getName(), true);
			} else {
				WirelessRedstone.updateTransmitter(id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), event.getBlock().getWorld().getName(), false);
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntityType() == EntityType.FIREBALL) {
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 4, true);
		}
	}
}
