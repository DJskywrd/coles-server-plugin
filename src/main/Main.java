package main;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main extends JavaPlugin {
	int thisVersion = 10;
	FileConfiguration config = getConfig();
	
	@Override
	public void onEnable() {
		// Check Updates
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/DJskywrd/coles-server-plugin/releases/latest"))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
            	JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
            	int latestVersion = Integer.parseInt(responseJson.get("tag_name").getAsString().replace("v", "").replace(".", ""));
            	
            	if (latestVersion > thisVersion) {
            		getLogger().log(Level.INFO, "A newer version (" + responseJson.get("tag_name").getAsString() + ") has been found. Please wait for the update.");
            		
            		JsonArray updateAssets = responseJson.get("assets").getAsJsonArray();
        			HttpRequest updateRequest = HttpRequest.newBuilder()
                            .uri(URI.create(updateAssets.get(0).getAsJsonObject().get("browser_download_url").getAsString()))
                            .build();

                    HttpResponse<InputStream> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofInputStream());
                    
                    if (updateResponse.statusCode() == 200) {
                    	File update = new File(getDataFolder().getParentFile(), updateAssets.get(0).getAsJsonObject().get("name").getAsString());
                        Files.copy(updateResponse.body(), update.toPath());
                        getLogger().log(Level.INFO, "Update Successful! Please wait for server reload.");
                        getServer().reload();
                    } else if (updateResponse.statusCode() == 302) {
                    	HttpRequest redirectRequest = HttpRequest.newBuilder()
                                .uri(URI.create(updateResponse.headers().firstValue("Location").get()))
                                .build();
                    	HttpResponse<InputStream> redirectResponse = client.send(redirectRequest, HttpResponse.BodyHandlers.ofInputStream());
                    	File update = new File(getDataFolder().getParentFile(), updateAssets.get(0).getAsJsonObject().get("name").getAsString());
                        Files.copy(redirectResponse.body(), update.toPath());
                        getLogger().log(Level.INFO, "Update Successful! Please wait for server reload.");
                        getServer().reload();
                        return;
                    } else {
                    	throw new Exception("Status code not okay for uploading");
                    }
            	}
            } else {
            	throw new Exception("Status code not okay for checking");
            }
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error checking for updates: " + e.getMessage());
		}
		
		this.getCommand("locateplayer").setExecutor(new Commands());
		this.getCommand("setantennaid").setExecutor(new Commands());
		this.getCommand("serverreload").setExecutor(new Commands());
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		this.saveDefaultConfig();
		
		// Crafting
		ItemStack gta = new ItemStack(Material.REDSTONE_LAMP);
		ItemMeta gta_meta = gta.getItemMeta();
		gta_meta.setDisplayName(ChatColor.GOLD + "Global Transmitter Antenna - 000");
		gta.setItemMeta(gta_meta);
		
		NamespacedKey gta_key = new NamespacedKey(this, "global_transmitter_antenna");
		ShapedRecipe gta_recipe = new ShapedRecipe(gta_key, gta);
		gta_recipe.shape(" D ", " B ", " L ");
		gta_recipe.setIngredient('D', Material.DIAMOND);
		gta_recipe.setIngredient('B', Material.REDSTONE_BLOCK);
		gta_recipe.setIngredient('L', Material.LIGHTNING_ROD);
		Bukkit.addRecipe(gta_recipe);
		
		ItemStack rta = new ItemStack(Material.LIGHTNING_ROD);
		ItemMeta rta_meta = rta.getItemMeta();
		rta_meta.setDisplayName(ChatColor.GOLD + "Global Receiver Antenna - 000");
		rta.setItemMeta(rta_meta);
		
		NamespacedKey rta_key = new NamespacedKey(this, "global_receiver_antenna");
		ShapedRecipe rta_recipe = new ShapedRecipe(rta_key, rta);
		rta_recipe.shape(" D ", " R ", " L ");
		rta_recipe.setIngredient('D', Material.DIAMOND);
		rta_recipe.setIngredient('R', Material.REDSTONE);
		rta_recipe.setIngredient('L', Material.LIGHTNING_ROD);
		Bukkit.addRecipe(rta_recipe);
	}
	
	@Override
	public void onDisable() {
		
	}
}
