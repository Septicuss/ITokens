package me.septicuss.listener;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;

import me.septicuss.manager.ConfigManager;
import me.septicuss.manager.TokenManager;
import me.septicuss.utils.MessageUtils;

public class DeathListener implements Listener {

	private ConfigManager configManager;
	private TokenManager tokenManager;

	public DeathListener(final ConfigManager configManager, final TokenManager tokenManager) {
		this.configManager = configManager;
		this.tokenManager = tokenManager;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		final Player player = event.getEntity();
		final Inventory inventory = player.getInventory();

		if (!tokenManager.hasTokenItem(inventory)) {
			return;
		}

		event.setKeepInventory(true);
		event.setKeepLevel(true);

		event.getDrops().clear();
		event.setDroppedExp(0);

		tokenManager.deductTokenItem(inventory);

		message(player, "messages.player-saved-notify");

	}

	@EventHandler
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {

		final Entity damaged = event.getEntity();
		final Entity damager = event.getDamager();

		if (!(damaged instanceof Player) || !(damager instanceof Player)) {
			return;
		}

		final Player damagedPlayer = (Player) damaged;
		final Player damagerPlayer = (Player) damager;

		if (damagedPlayer.getHealth() - event.getFinalDamage() > 0) {
			return;
		}

		if (!tokenManager.hasTokenItem(damagedPlayer.getInventory())) {
			return;
		}

		message(damagerPlayer, "messages.killer-saved-notify");

	}

	private void message(Player player, String messagePath) {

		final List<String> coloredMessage = MessageUtils.color(configManager.getStringList(messagePath));
		coloredMessage.forEach(message -> {
			final String finalMessage = message.replace("%item_name%",
					tokenManager.getTokenItem().getItemMeta().getDisplayName());
			player.sendMessage(finalMessage);
		});

	}

}
