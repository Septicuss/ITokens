package fi.septicuss.itokens;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import fi.septicuss.itokens.command.ITokenCommand;
import fi.septicuss.itokens.listener.TokenListener;
import fi.septicuss.itokens.token.TokenManager;
import fi.septicuss.itokens.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;

public class ITokens extends JavaPlugin {

	private static ITokens instance;

	private TokenManager tokenManager;

	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		reload();
	}

	public void reload() {
		reloadConfig();
		
		tokenManager = new TokenManager(getConfig());
		tokenManager.load();

		getServer().getPluginManager().registerEvents(new TokenListener(getConfig(), tokenManager), this);
		
		ITokenCommand tokenCommand = new ITokenCommand(getConfig(), tokenManager);

		PluginCommand pluginTokenCommand = getCommand("itoken");
		pluginTokenCommand.setExecutor(tokenCommand);
		pluginTokenCommand.setTabCompleter(tokenCommand);
		
	}
	
	public static ITokens get() {
		return instance;
	}
	
	public static void warn(String message) {
		Bukkit.getConsoleSender().sendMessage(MessageUtils.color(ChatColor.RED + "[ITokens] " + message));
	}
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}
	
	public static NamespacedKey getNamespacedKey(String key) {
		return new NamespacedKey(instance, key);
	}

}
