package fi.septicuss.itokens.token;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fi.septicuss.itokens.ITokens;
import fi.septicuss.itokens.utils.MessageUtils;

public class TokenManager  {

	private static final NamespacedKey TOKEN_KEY = new NamespacedKey(ITokens.get(), "itoken_token");

	private FileConfiguration config;
	private ItemStack cachedTokenItem;

	public TokenManager(FileConfiguration config) {
		this.config = config;
	}
	

	public void load() {
		loadTokenItem();
	}

	private void loadTokenItem() {
		String materialString = config.getString("item.material", "gold_nugget");
		Material material = Material.GOLD_NUGGET;
		
		try {
			material = Material.valueOf(materialString.toUpperCase());
		} catch (IllegalArgumentException exception) {
			ITokens.warn("Unknown material " + materialString + " resorting to default gold nugget");
			return;
		}
		
		String readableMaterial = WordUtils.capitalizeFully(material.toString().replace("_", " "));
		String name = MessageUtils.color(config.getString("item.name", readableMaterial));
		
		List<String> lore = MessageUtils.color(config.getStringList("item.lore"));
		int modelData = config.getInt("item.modeldata", 0);
		
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.setCustomModelData(modelData);
		
		meta.getPersistentDataContainer().set(TOKEN_KEY, PersistentDataType.BYTE, (byte) 1);
		item.setItemMeta(meta);

		this.cachedTokenItem = item.clone();
	}

	public boolean isTokenItem(final ItemStack item) {
		if (item == null || item.getType().isAir()) {
			return false;
		}

		if (!item.hasItemMeta() || item.getItemMeta().getPersistentDataContainer() == null) {
			return false;
		}

		return item.getItemMeta().getPersistentDataContainer().has(TOKEN_KEY, PersistentDataType.BYTE);
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
		if (cachedTokenItem == null) return null;
		return cachedTokenItem.clone();
	}

}
