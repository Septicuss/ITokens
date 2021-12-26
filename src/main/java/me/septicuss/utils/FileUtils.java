package me.septicuss.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileUtils {

	private JavaPlugin plugin;

	public FileUtils(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public FileConfiguration getFileConfiguration(String fileName) {
		final File file = getYmlFile(fileName);
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveFileConfiguration(String fileName, FileConfiguration fileConfiguration) {
		final File file = getYmlFile(fileName);
		try {
			fileConfiguration.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private File getYmlFile(String fileName) {
		final String finalFileName = String.format("%s.yml", fileName.toLowerCase());
		return new File(plugin.getDataFolder(), finalFileName);
	}

}
