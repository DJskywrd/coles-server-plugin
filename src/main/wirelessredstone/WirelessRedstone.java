package main.wirelessredstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import main.Main;

public class WirelessRedstone {
	static JavaPlugin plugin = Main.getPlugin(Main.class);
	
	public static void updateReceivers(String id, String world) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("receivers"))
			config.createSection("receivers");
		if (!config.contains("transmitters"))
			config.createSection("transmitters");
		ConfigurationSection receivers = config.getConfigurationSection("receivers");
		if (!receivers.contains(id)) {
			List<Map<?, ?>> newList = new ArrayList<>();
			receivers.set(id, newList);
		}
		List<Map<?, ?>> receivers_in_id = receivers.getMapList(id);
		
		if (id.startsWith("global")) {
			boolean activeTransmitter = false;
			List<Map<?, ?>> transmitters_in_id = config.getConfigurationSection("transmitters").getMapList(id);
			for (Map<?, ?> transmitter : transmitters_in_id) {
				if (transmitter.get("powered") != null)
					if (transmitter.get("powered").equals(true) && transmitter.get("world").equals(world))
						activeTransmitter = true;
			}
			
			for (Map<?, ?> receiver : receivers_in_id) {
				if (receiver.get("world").equals(world)) {
					Block receiverBlock = new Location(Bukkit.getWorld(world), (int) receiver.get("x"), (int) receiver.get("y"), (int) receiver.get("z")).getBlock();
					Powerable powerable = (Powerable) receiverBlock.getBlockData();
					powerable.setPowered(activeTransmitter);
					receiverBlock.setBlockData(powerable);
				}
			}
		}
		plugin.saveConfig();
	}
	
	public static void updateTransmitter(String id, int x, int y, int z, String world, boolean powered) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("transmitters"))
			config.createSection("transmitters");
		if (!config.getConfigurationSection("transmitters").contains(id)) {
			List<Map<?, ?>> transmitterList = new ArrayList<>();
			config.getConfigurationSection("transmitters").set(id, transmitterList);
		}
		List<Map<?, ?>> transmitterList = config.getConfigurationSection("transmitters").getMapList(id);
		
		int i = 0;
		int iToReplace = 0;
		for (Map<?, ?> transmitter : transmitterList) {
			if (transmitter.get("world").equals(world) && transmitter.get("x").equals(x) && transmitter.get("y").equals(y) && transmitter.get("z").equals(z)) {
				iToReplace = i;
			}
			i++;
		}
		Map<String, Object> transmitter = new HashMap<>();
		transmitter.put("world", world);
		transmitter.put("x", x);
		transmitter.put("y", y);
		transmitter.put("z", z);
		transmitter.put("powered", powered);
		transmitterList.set(iToReplace, transmitter);
		config.getConfigurationSection("transmitters").set(id, transmitterList);
		plugin.saveConfig();
		updateReceivers(id, world);
	}
	
	public static void addTransmitter(String id, String world, int x, int y, int z) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("transmitters"))
			config.createSection("transmitters");
		if (!config.getConfigurationSection("transmitters").contains(id)) {
			List<Map<?, ?>> transmitterList = new ArrayList<>();
			config.getConfigurationSection("transmitters").set(id, transmitterList);
		}
		List<Map<?, ?>> transmitterList = config.getConfigurationSection("transmitters").getMapList(id);
		
		Map<String, Object> transmitter = new HashMap<>();
		transmitter.put("world", world);
		transmitter.put("x", x);
		transmitter.put("y", y);
		transmitter.put("z", z);
		transmitterList.add(transmitter);
		config.getConfigurationSection("transmitters").set(id, transmitterList);
		plugin.saveConfig();
		updateReceivers(id, world);
	}
	
	public static void addReceiver(String id, String world, int x, int y, int z) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("receivers"))
			config.createSection("receivers");
		if (!config.getConfigurationSection("receivers").contains(id)) {
			List<Map<?, ?>> receiverList = new ArrayList<>();
			config.getConfigurationSection("receivers").set(id, receiverList);
		}
		List<Map<?, ?>> receiverList = config.getConfigurationSection("receivers").getMapList(id);
		
		Map<String, Object> receiver = new HashMap<>();
		receiver.put("world", world);
		receiver.put("x", x);
		receiver.put("y", y);
		receiver.put("z", z);
		receiverList.add(receiver);
		config.getConfigurationSection("receivers").set(id, receiverList);
		plugin.saveConfig();
		updateReceivers(id, world);
	}
	
	public static void removeTransmitter(String id, String world, int x, int y, int z) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("transmitters"))
			config.createSection("transmitters");
		if (!config.getConfigurationSection("transmitters").contains(id)) {
			List<Map<?, ?>> transmitterList = new ArrayList<>();
			config.getConfigurationSection("transmitters").set(id, transmitterList);
		}
		List<Map<?, ?>> transmitterList = config.getConfigurationSection("transmitters").getMapList(id);
		
		int i = 0;
		int iToReplace = 0;
		for (Map<?, ?> transmitter : transmitterList) {
			if (transmitter.get("world").equals(world) && transmitter.get("x").equals(x) && transmitter.get("y").equals(y) && transmitter.get("z").equals(z)) {
				iToReplace = i;
			}
			i++;
		}
		transmitterList.remove(iToReplace);
		config.getConfigurationSection("transmitters").set(id, transmitterList);
		plugin.saveConfig();
		updateReceivers(id, world);
	}
	
	public static void removeReceivers(String id, String world, int x, int y, int z) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("receivers"))
			config.createSection("receivers");
		if (!config.getConfigurationSection("receivers").contains(id)) {
			List<Map<?, ?>> receiverList = new ArrayList<>();
			config.getConfigurationSection("receivers").set(id, receiverList);
		}
		List<Map<?, ?>> receiverList = config.getConfigurationSection("receivers").getMapList(id);
		
		int i = 0;
		int iToReplace = 0;
		for (Map<?, ?> receiver : receiverList) {
			if (receiver.get("world").equals(world) && receiver.get("x").equals(x) && receiver.get("y").equals(y) && receiver.get("z").equals(z)) {
				iToReplace = i;
			}
			i++;
		}
		receiverList.remove(iToReplace);
		config.getConfigurationSection("receivers").set(id, receiverList);
		plugin.saveConfig();
		updateReceivers(id, world);
	}
}
