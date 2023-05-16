package fi.septicuss.itokens.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fi.septicuss.itokens.ITokens;
import fi.septicuss.itokens.utils.MessageUtils;

public class TokenListener implements Listener {

	private ITokens plugin;

	public TokenListener(ITokens plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		for (ItemStack item : event.getView().getTopInventory().getContents()) {
			if (plugin.getTokenManager().isTokenItem(item)) {
				event.setCancelled(true);
				event.setResult(Result.DENY);
				event.getInventory().setResult(new ItemStack(Material.AIR));

				Player player = (Player) event.getWhoClicked();
				player.updateInventory();
				return;
			}
		}
	}

	// TODO: Spam clicking anvil result while renaming may still take exp
	@EventHandler
	public void onAnvil(PrepareAnvilEvent event) {
		for (ItemStack item : event.getInventory().getContents()) {
			if (plugin.getTokenManager().isTokenItem(item)) {
				event.setResult(null);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player player = event.getEntity();
		final Inventory inventory = player.getInventory();

		if (!plugin.getTokenManager().hasTokenItem(inventory)) {
			return;
		}

		event.setKeepInventory(true);
		event.setKeepLevel(true);

		event.getDrops().clear();
		event.setDroppedExp(0);

		plugin.getTokenManager().deductTokenItem(inventory);

		message(player, "messages.player-saved-notify");

	}

//	@EventHandler
//	public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
//
//		final Entity damaged = event.getEntity();
//		final Entity damager = event.getDamager();
//
//		if (!(damaged instanceof Player) || !(damager instanceof Player)) {
//			return;
//		}
//
//		final Player damagedPlayer = (Player) damaged;
//		final Player damagerPlayer = (Player) damager;
//
//		if (damagedPlayer.getHealth() - event.getFinalDamage() > 0) {
//			return;
//		}
//
//		if (!tokenManager.hasTokenItem(damagedPlayer.getInventory())) {
//			return;
//		}
//
//		message(damagerPlayer, "messages.killer-saved-notify");
//
//	}

	private void message(Player player, String messagePath) {
		final List<String> coloredMessage = MessageUtils.color(plugin.getConfig().getStringList(messagePath));
		coloredMessage.forEach(message -> {
			final String finalMessage = message.replace("%item_name%",
					plugin.getTokenManager().getTokenItem().getItemMeta().getDisplayName());
			player.sendMessage(finalMessage);
		});

	}

}
