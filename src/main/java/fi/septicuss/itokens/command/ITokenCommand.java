package fi.septicuss.itokens.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fi.septicuss.itokens.ITokens;
import fi.septicuss.itokens.token.TokenManager;
import fi.septicuss.itokens.utils.MessageUtils;

public class ITokenCommand implements CommandExecutor, TabCompleter {

	private static String COMMAND_PERMISSION = "itokens.command";
	private FileConfiguration config;
	private TokenManager tokenManager;

	public ITokenCommand(FileConfiguration config, TokenManager tokenManager) {
		this.config = config;
		this.tokenManager = tokenManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (!hasPermission(sender)) {
			return Arrays.asList("");
		}
		
		if (args.length == 1)
			return Arrays.asList("reload", "give");

		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!hasPermission(sender)) {
			message(sender, "messages.no-permission");
			return true;
		}

		parseITokenCommand(sender, args);
		return true;

	}

	private void parseITokenCommand(CommandSender sender, String[] args) {

		if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			messageCustom(sender, "&6&lIToken Help");
			messageCustom(sender, "&6/itoken &7give (player) (amount)");
			messageCustom(sender, "&6/itoken &7reload");
			return;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			messageCustom(sender, "&fReloading...");
			ITokens.get().reload();
			messageCustom(sender, "&fPlugin reloaded!");
			return;
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			
			if (args.length == 1) {
				messageCustom(sender, "&6/itoken &7give (player) (amount)");
				return;
			}
			
			Player target = null;
			int amount = 1;

			if (args.length >= 2) {
				target = Bukkit.getPlayerExact(args[1]);

				if (target == null || !target.isOnline()) {
					target = null;
				}

			}

			if (args.length >= 3) {

				try {
					Integer.parseInt(args[2]);
				} catch (NumberFormatException exception) {
					messageCustom(sender, "&cInvalid amount specified.");
					return;
				}

				amount = Integer.parseInt(args[2]);
			}

			if (target == null) {
				if (sender instanceof Player) {
					target = (Player) sender;
				} else {
					sender.sendMessage("§cInvalid target player");
					return;
				}
			}

			final ItemStack tokenItem = tokenManager.getTokenItem();
			tokenItem.setAmount(amount);

			target.getInventory().addItem(tokenItem);
			
			return;
		}
		


	}

	private boolean hasPermission(CommandSender sender) {
		return (sender.isOp() || !(sender instanceof Player) || sender.hasPermission(COMMAND_PERMISSION));
	}

	private void messageCustom(CommandSender sender, String message) {
		final String coloredMessage = MessageUtils.color(message);
		sender.sendMessage(coloredMessage);
	}

	private void message(CommandSender player, String messagePath) {
		final List<String> coloredMessage = MessageUtils.color(config.getStringList(messagePath));
		coloredMessage.forEach(message -> {
			final String finalMessage = message.replace("%item_name%",
					tokenManager.getTokenItem().getItemMeta().getDisplayName());
			player.sendMessage(finalMessage);
		});
	}

}
