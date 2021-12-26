package me.septicuss.manager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.septicuss.utils.FileUtils;

public class ConfigManager extends AbstractManager {

	private static final String CONFIG_NAME = "config";
	private static final Material DEFAULT_MATERIAL = Material.WITHER_ROSE;

	private FileUtils fileUtils;
	private FileConfiguration config;

	public ConfigManager(final FileUtils fileUtils) {
		this.fileUtils = fileUtils;
		config = fileUtils.getFileConfiguration(CONFIG_NAME);
	}

	@Override
	public void reload() {

		config = fileUtils.getFileConfiguration(CONFIG_NAME);
		setDefaults();
	
	}

	@Override
	public void disable() {
		
		fileUtils.saveFileConfiguration(CONFIG_NAME, config);
	
	}
	private void setDefaults() {

		config.options().copyDefaults(true);
		
		config.addDefault("messages.player-saved-notify", Arrays.asList("&aYour items have been kept, because you had %item_name%&r in your inventory!"));
		config.addDefault("messages.killer-saved-notify", Arrays.asList("&cThe player killed did not drop any loot, due to having an %item_name%&r on them!"));
		config.addDefault("messages.no-permission", Arrays.asList("&cYou do not have permission to use this command."));
		
		config.addDefault("item.material", DEFAULT_MATERIAL.toString());
		config.addDefault("item.name", "&8&lKeep Inventory Token");
		config.addDefault("item.lore", Arrays.asList("&7Upon having this item in", "&7your inventory, you will", "&7keep your inventory", "&7after your death.", " ", "&eThis item has a one-time use."));
		
		fileUtils.saveFileConfiguration(CONFIG_NAME, config);
		
	}
	
	public Material getMaterial(String path) {
		
		final String materialName = getString(path).toUpperCase();
		
		try {
			Material.valueOf(materialName);
		} catch (Exception exception) {
			return DEFAULT_MATERIAL;
		}
		
		return Material.valueOf(materialName);
		
	}
	
	public String getString(String path) {
		
		return config.getString(path);
		
	}
	
	public List<String> getStringList(String path) {
		
		return config.getStringList(path);
		
	}
	
	
	


}
