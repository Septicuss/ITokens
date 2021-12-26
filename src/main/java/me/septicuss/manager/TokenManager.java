package me.septicuss.manager;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.septicuss.ITokens;
import me.septicuss.utils.MessageUtils;

public class TokenManager extends AbstractManager {

	private static final String TOKEN_TAG = "itoken_token";

	private ConfigManager configManager;
	private ItemStack cachedTokenItem;

	public TokenManager(final ConfigManager configManager) {
		this.configManager = configManager;
	}

	@Override
	public void reload() {

		loadTokenItem();

	}

	@Override
	public void disable() {

		configManager = null;
		cachedTokenItem = null;

	}

	private void loadTokenItem() {

		final Material material = configManager.getMaterial("item.material");
		final String name = MessageUtils.color(configManager.getString("item.name"));
		final List<String> lore = MessageUtils.color(configManager.getStringList("item.lore"));

		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);

		final NamespacedKey tokenKey = ITokens.getNamespacedKey(TOKEN_TAG);
		itemMeta.getPersistentDataContainer().set(tokenKey, PersistentDataType.BYTE, (byte) 1);

		item.setItemMeta(itemMeta);

		this.cachedTokenItem = item.clone();

	}

	public boolean isTokenItem(final ItemStack item) {

		if (item == null || item.getType().isAir()) {
			return false;
		}

		if (!item.hasItemMeta() || item.getItemMeta().getPersistentDataContainer() == null) {
			return false;
		}

		final NamespacedKey tokenKey = ITokens.getNamespacedKey(TOKEN_TAG);
		return item.getItemMeta().getPersistentDataContainer().has(tokenKey, PersistentDataType.BYTE);

	}

	public void deductTokenItem(final Inventory inventory) {

		ItemStack token = null;

		for (ItemStack item : inventory.getContents()) {
			if (isTokenItem(item)) {
				token = item;
				break;
			}
		}

		if (token == null || token.getType() == Material.AIR) {
			return;
		}

		token.setAmount(token.getAmount() - 1);

	}

	public boolean hasTokenItem(final Inventory inventory) {

		for (ItemStack item : inventory.getContents()) {
			if (isTokenItem(item)) {
				return true;
			}
		}

		return false;

	}

	public ItemStack getTokenItem() {

		return cachedTokenItem.clone();

	}

}
