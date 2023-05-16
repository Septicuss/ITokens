package fi.septicuss.itokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fi.septicuss.itokens.command.ITokenCommand;
import fi.septicuss.itokens.listener.DeathListener;
import fi.septicuss.itokens.manager.AbstractManager;
import fi.septicuss.itokens.manager.ConfigManager;
import fi.septicuss.itokens.manager.TokenManager;
import fi.septicuss.itokens.token.TokenManager;
import fi.septicuss.itokens.utils.FileUtils;

public class ITokens extends JavaPlugin {

	private static ITokens instance;

	private FileUtils fileUtils;
	private ConfigManager configManager;
	private TokenManager tokenManager;

	private Set<AbstractManager> managerSet = new HashSet<>();

	@Override
	public void onEnable() {

		instance = this;

		this.fileUtils = new FileUtils(this);
		this.configManager = new ConfigManager(fileUtils);
		this.tokenManager = new TokenManager(configManager);

		managerSet.addAll(Arrays.asList(configManager, tokenManager));
		managerSet.forEach(AbstractManager::reload);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new DeathListener(configManager, tokenManager), this);

		ITokenCommand tokenCommand = new ITokenCommand(configManager, tokenManager);
		PluginCommand pluginTokenCommand = getCommand("itoken");
		pluginTokenCommand.setExecutor(tokenCommand);
		pluginTokenCommand.setTabCompleter(tokenCommand);

	}

	@Override
	public void onDisable() {

		managerSet.forEach(AbstractManager::disable);

	}

	public static NamespacedKey getNamespacedKey(String key) {
		return new NamespacedKey(instance, key);
	}

}
