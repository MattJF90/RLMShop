package com.rlminecraft.RLMShop;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.rlminecraft.RLMShop.Event.ShopCreationEvent;
import com.rlminecraft.RLMShop.Event.ShopDeletionEvent;
import com.rlminecraft.RLMShop.Event.ShopModificationEvent;
import com.rlminecraft.RLMShop.Prompt.PromptAction;
import com.rlminecraft.RLMShop.Prompt.PromptState;
import com.rlminecraft.RLMShop.Prompt.ShopPrompt;
import com.rlminecraft.util.Inv;

/**
 * <b>Listener</b><br>
 * Listens for any events required by RLMShop
 * @author Matt Fielding
 */
public class ShopListener implements Listener {
	
	private RLMShop plugin;
	
	/**
	 * <b>ShopListener Constructor</b><br>
	 * Listens for any events required by RLMShop
	 * @param instance
	 */
	public ShopListener(RLMShop instance) {
		plugin = instance;
	}
	
	/**
	 * Called when a player interacts with an item frame.
	 * On left-click, it calls the shop info screen.
	 * On right-click, it starts the shop prompt.
	 * @param event The PlayerInteractEvent
	 */
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		// Check the following:
		//  - player is targeting block (not air)
		//  - targeted block is an item frame
		//  - shop exists at block location
		if (event.getAction() != Action.LEFT_CLICK_BLOCK
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!event.hasBlock()) return;
		if (event.getClickedBlock().getType() != Material.ITEM_FRAME) return;
		Shop shop = plugin.store.accessShop(event.getClickedBlock().getLocation());
		if (shop == null) return;
		
		switch (event.getAction()) {
		case LEFT_CLICK_BLOCK:
			event.setCancelled(true);
			// View shop info
			shopView(event.getPlayer(), shop);
			break;
		case RIGHT_CLICK_BLOCK:
			event.setCancelled(true);
			// Begin prompt
			if (event.getPlayer().getName().equalsIgnoreCase(shop.getOwner())) {
				plugin.playerChatState.put(event.getPlayer().getName(), new PromptState(PromptAction.OWNERPROMPT, shop));
			} else if (event.getPlayer().hasPermission("rlmshop.admin")) {
				plugin.playerChatState.put(event.getPlayer().getName(), new PromptState(PromptAction.ADMINPROMPT, shop));
			} else {
				plugin.playerChatState.put(event.getPlayer().getName(), new PromptState(PromptAction.PLAYERPROMPT, shop));
			}
			break;
		}
	}
	
	//@EventHandler
	/*public void onBlockBreak (BlockBreakEvent event) {
		//Check for direct item-frame break
		if (event.getBlock().getType() == Material.ITEM_FRAME) {
			Shop shop = plugin.store.accessShop(event.getBlock().getLocation());
			if (shop != null) {
				if (event.getPlayer().getName() == shop.getOwner()) {
					event.getPlayer().sendMessage(ChatColor.RED + "To delete this shop, right-click on it and type \"destroy\".");
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to destroy this shop.");
				}
				event.setCancelled(true);
			}
			return;
		}
		// Check for indirect item-frame break
		/*int y = event.getBlock().getY();
		World world = event.getBlock().getWorld();
		for (int x = -1; x <= 1; x += 2) {
			for (int z = -1; z <= 1; z += 2) {
				if (x != 0 && z != 0) {
					Block block = event.getBlock().getRelative(x,0,y);
					Shop shop = plugin.store.accessShop(block.getLocation());
					if (shop != null && block.getType() == Material.ITEM_FRAME) {
						if (block.)
					}
				}
			}
		}*/
	//}
	
	@EventHandler
	public void onHangBreak (HangingBreakEvent event) {
		event.getEntity().getLocation();
		if (!(event.getEntity() instanceof ItemFrame)) return;
		Shop shop = plugin.store.accessShop(event.getEntity().getLocation());
		if (shop == null) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChat (AsyncPlayerChatEvent event) {
		if (plugin.playerChatState.containsKey(event.getPlayer().getName())) return;
		if (event.isCancelled()) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		String name = player.getName();
		PromptState state = plugin.playerChatState.get(name);
		if ((state.action != null && state.action != PromptAction.NONE) &&
			(	event.getMessage().equalsIgnoreCase("exit")
			|| event.getMessage().equalsIgnoreCase("quit")
			|| event.getMessage().equalsIgnoreCase("stop"))) {
			player.sendMessage("You have exited the shop prompt!");
		}
		switch (state.action) {
		case OWNERPROMPT:			// Initial owner prompt
			if (event.getMessage().toLowerCase() == "add") {
				ShopPrompt.Add(player, state.shop);
				state = new PromptState(PromptAction.ADD, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "remove") {
				ShopPrompt.Remove(player, state.shop);
				state = new PromptState(PromptAction.REMOVE, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "set") {
				ShopPrompt.Set(player);
				state = new PromptState(PromptAction.SET, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "destroy") {
				ShopPrompt.Destroy(player);
				state = new PromptState(PromptAction.DESTROY, state.shop);
				plugin.playerChatState.put(name,state);
			} else {
				ShopPrompt.Invalid(player);
				plugin.playerChatState.remove(name);
			}
			break;
		case ADD:					// Adding items
			int addAmount = ShopPrompt.IntResponse(event.getMessage());
			if (addAmount < 0) {
				ShopPrompt.Invalid_Repeat(player);
			} else {
				ShopPrompt.Complete(player);
				plugin.playerChatState.remove(name);
			}
			break;
		case CONFIRMADD:			// Confirm adding items
			// not used
			break;
		case REMOVE:				// Removing items
			int removeAmount = ShopPrompt.IntResponse(event.getMessage());
			if (removeAmount < 0) {
				ShopPrompt.Invalid_Repeat(player);
			} else {
				ShopPrompt.Complete(player);
				plugin.playerChatState.remove(name);
			}
			break;
		case CONFIRMREMOVE:			// Confirm removing items
			// not used
			break;
		case SET:					// Setting shop properties
			if (   event.getMessage().equalsIgnoreCase("retail")
				|| event.getMessage().equalsIgnoreCase("retail price")) {
				ShopPrompt.SetRetail(player);
				plugin.playerChatState.put(name, new PromptState(PromptAction.SETRETAIL,state.shop));
			} else
			if (   event.getMessage().equalsIgnoreCase("pawn")
				|| event.getMessage().equalsIgnoreCase("pawn price")) {
				ShopPrompt.SetPawn(player);
				plugin.playerChatState.put(name, new PromptState(PromptAction.SETPAWN,state.shop));
			} else
			if (   event.getMessage().equalsIgnoreCase("max")
				|| event.getMessage().equalsIgnoreCase("max quantity")) {
				ShopPrompt.SetMaxQuantity(player);
				plugin.playerChatState.put(name, new PromptState(PromptAction.SETMAXQUANTITY,state.shop));
			}
			break;
		case SETRETAIL:				// Setting retail price
			float buyPrice = ShopPrompt.FloatResponse(event.getMessage());
			if (buyPrice < 0 && buyPrice != -1) {
				ShopPrompt.ConfirmSetRetail(player, buyPrice, false);
				ShopPrompt.Invalid_Repeat(player);
			} else {
				ShopPrompt.ConfirmSetRetail(player, buyPrice, true);
				plugin.playerChatState.remove(name);
			}
			break;
		case CONFIRMSETRETAIL:		// Confirm setting retail price
			// not used
			break;
		case SETPAWN:				// Setting pawn price
			float sellPrice = ShopPrompt.FloatResponse(event.getMessage());
			if (sellPrice < 0 && sellPrice != -1) {
				ShopPrompt.ConfirmSetPawn(player, sellPrice, false);
				ShopPrompt.Invalid_Repeat(player);
			} else {
				ShopPrompt.ConfirmSetPawn(player, sellPrice, true);
				plugin.playerChatState.remove(name);
			}
			break;
		case CONFIRMSETPAWN:		// Confirm setting pawn price
			// not used
			break;
		case SETMAXQUANTITY:		// Setting maximum quantity
			int maxAmount = ShopPrompt.IntResponse(event.getMessage());
			if (maxAmount < 0 && maxAmount != -1) {
				ShopPrompt.ConfirmSetMaxQuantity(player, maxAmount, false);
				plugin.playerChatState.remove(name);
			} else {
				ShopPrompt.ConfirmSetMaxQuantity(player, maxAmount, true);
				plugin.playerChatState.remove(name);
			}
			break;
		case CONFIRMSETMAXQUANTITY:	// Confirm setting maximum quantity
			// not used
			break;
		case DESTROY:				// Destroying shop
			if (ShopPrompt.BoolResponse(event.getMessage()) == 1) {
				ShopPrompt.ConfirmDestroy(player);
			}
			else if (ShopPrompt.BoolResponse(event.getMessage()) == 0) {
				ShopPrompt.Cancel(player);
			} else {
				ShopPrompt.Invalid(player);
				break;
			}
			// On valid response, remove chat state (no shop prompt)
			plugin.playerChatState.remove(name);
			break;
		case CONFIRMDESTROY:		// Confirm destroying shop
			// not used
			break;
		case PLAYERPROMPT:			// Initial (non-owner) player prompt
			if (event.getMessage().toLowerCase() == "buy") {
				ShopPrompt.Buy(player, state.shop);
				state = new PromptState(PromptAction.BUY, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "sell") {
				ShopPrompt.Sell(player, state.shop);
				state = new PromptState(PromptAction.SELL, state.shop);
				plugin.playerChatState.put(name,state);
			} else {
				ShopPrompt.Invalid(player);
				plugin.playerChatState.remove(name);
			}
			break;
		case BUY:					// Buying items
			int buyAmount = ShopPrompt.IntResponse(event.getMessage());
			if (buyAmount < 0) {
				ShopPrompt.Invalid(player);
				plugin.playerChatState.remove(name);
			} else {
				ShopPrompt.ConfirmBuy(player, state.shop, buyAmount);
				plugin.playerChatState.put(name, new PromptState(PromptAction.CONFIRMBUY, state.shop, buyAmount));
			}
			break;
		case CONFIRMBUY:			// Confirm buying items
			if (ShopPrompt.BoolResponse(event.getMessage()) == 1) {
				shopBuy(player, state.shop, (int) state.arg);
				ShopPrompt.Complete(player);
				plugin.playerChatState.remove(name);
			}
			else if (ShopPrompt.BoolResponse(event.getMessage()) == 0) {
				ShopPrompt.Cancel(player);
				plugin.playerChatState.remove(name);
			} else {
				ShopPrompt.Invalid_Repeat(player);
			}
			break;
		case SELL:					// Selling items
			int sellAmount = ShopPrompt.IntResponse(event.getMessage());
			if (sellAmount < 0) {
				ShopPrompt.Invalid(player);
				plugin.playerChatState.remove(name);
			} else {
				ShopPrompt.ConfirmSell(player, state.shop, sellAmount);
				plugin.playerChatState.put(name, new PromptState(PromptAction.CONFIRMSELL, state.shop, sellAmount));
			}
			break;
		case CONFIRMSELL:			// Confirm selling items
			if (ShopPrompt.BoolResponse(event.getMessage()) == 1) {
				shopSell(player, state.shop, (int) state.arg);
				ShopPrompt.Complete(player);
				plugin.playerChatState.remove(name);
			}
			else if (ShopPrompt.BoolResponse(event.getMessage()) == 0) {
				ShopPrompt.Cancel(player);
				plugin.playerChatState.remove(name);
			} else {
				ShopPrompt.Invalid_Repeat(player);
			}
			break;
		case ADMINPROMPT:			// Initial admin prompt
			if (event.getMessage().toLowerCase() == "buy") {
				ShopPrompt.Buy(player, state.shop);
				state = new PromptState(PromptAction.BUY, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "sell") {
				ShopPrompt.Sell(player, state.shop);
				state = new PromptState(PromptAction.SELL, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "add") {
				ShopPrompt.Add(player, state.shop);
				state = new PromptState(PromptAction.ADD, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "remove") {
				ShopPrompt.Remove(player, state.shop);
				state = new PromptState(PromptAction.REMOVE, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "set") {
				ShopPrompt.Set(player);
				state = new PromptState(PromptAction.SET, state.shop);
				plugin.playerChatState.put(name,state);
			} else
			if (event.getMessage().toLowerCase() == "destroy") {
				ShopPrompt.Destroy(player);
				state = new PromptState(PromptAction.DESTROY, state.shop);
				plugin.playerChatState.put(name,state);
			} else {
				ShopPrompt.Invalid(player);
				plugin.playerChatState.remove(name);
			}
			break;
		default:					// No action or action == NONE
			event.setCancelled(false);
			plugin.playerChatState.remove(name);
			break;
		}
	}
	
	@EventHandler
	public void onShopCreate (ShopCreationEvent event) {
		plugin.store.createShop(event.getShop());
	}
	
	@EventHandler
	public void onShopModify (ShopModificationEvent event) {
		plugin.store.updateShop(event.getShop());
	}
	
	@EventHandler
	public void onShopDestroy (ShopDeletionEvent event) {
		plugin.store.deleteShop(event.getShop());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Sends shop info to a player.
	 * @param player the recipient of the shop info
	 * @param shop the shop whose info is to be sent
	 */
	public void shopView (Player player, Shop shop) {
		ChatColor key = ChatColor.LIGHT_PURPLE;
		ChatColor val = ChatColor.WHITE;
		player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "---- " + ChatColor.GREEN + "SHOP" + ChatColor.AQUA + " ----");
		
		// Display properties
		player.sendMessage(key + "Item:         " + val + shop.getMaterialData().getItemType().toString());
		if (shop.canRetail())
			player.sendMessage(key + "Retail Price: $" + val + String.format("%.2g%n", shop.getRetailPrice()));
		if (shop.canPawn())
			player.sendMessage(key + "Pawn Price:   $" + val + String.format("%.2g%n", shop.getPawnPrice()));
		if (!shop.isUnlimited()) {
			player.sendMessage(key + "Stock:        " + val + shop.getQuantity() + " / " + shop.getMaxQuantity());
		}
		player.sendMessage(key + "Owner:       " + val + shop.getOwner());
	}
	
	
	/**
	 * Causes a player to buy items from a given shop, depending on a number of factors.
	 * @param player the player purchasing items
	 * @param shop the shop from which items are being purchased
	 * @param quantity the number of items being purchased
	 * @return true if transaction successful<br>false otherwise
	 */
	public boolean shopBuy (Player player, Shop shop, int quantity) {
		// Check if retail is allowed
		if (!shop.canRetail()) {
			player.sendMessage(ChatColor.RED + "This shop does not allow retail of items.");
			return false;
		}
		// Check if shop has enough stock to handle the request
		if (shop.getQuantity() < quantity) {
			player.sendMessage(ChatColor.RED + "This shop does not have enough stock available.");
			if (plugin.getServer().getOfflinePlayer(shop.getOwner()).isOnline()
				&& shop.getQuantity() == 0) {
				plugin.getServer().getPlayer(shop.getOwner()).sendMessage(ChatColor.RED + "Your " + shop.getMaterialData().getItemType().toString() + " shop is out of stock.");
			}
			return false;
		}
		// Check economic requirements
		String name = player.getName();
		float cost = shop.getRetailPrice() * (float) quantity;
		//plugin.getServer().getServicesManager()
		if (!plugin.economy.hasAccount(name))
			plugin.economy.createPlayerAccount(name);
		if (plugin.economy.getBalance(name) < cost) {
			player.sendMessage(ChatColor.RED + "You do not have enough money."
				+ "You need $"
				+ String.format("%.2g%n", cost)
				+ " to complete this transaction.");
			return false;
		}
		// Add items to player's inventory
		int stackSize = shop.getMaterialData().toItemStack().getMaxStackSize();
		int amountToGet = quantity;
		HashMap<Integer,ItemStack> returnedItems = new HashMap<Integer,ItemStack>();
		while (amountToGet > 0
				&& player.getInventory().firstEmpty() != -1
				&& returnedItems.isEmpty()) {
			int amount;
			if (amountToGet > stackSize) {
				amount = stackSize;
			} else {
				amount = amountToGet;
			}
			ItemStack stack = shop.getMaterialData().toItemStack(amount);
			amountToGet -= amount;
			returnedItems = player.getInventory().addItem(stack);
		}
		// Remove non-giveable items from count
		if (!returnedItems.isEmpty()) {
			for (int i : returnedItems.keySet()) {
				amountToGet += returnedItems.get(i).getAmount();
			}
		}
		// Charge player for items actually given
		plugin.economy.withdrawPlayer(name, shop.getRetailPrice() * (quantity - amountToGet));
		plugin.economy.depositPlayer(shop.getOwner(), shop.getRetailPrice() * (quantity - amountToGet));
		player.sendMessage(ChatColor.GREEN + "You purchased "
				+ (quantity - amountToGet) + " "
				+ shop.getMaterialData().getItemType().toString() + " for $"
				+ String.format("%.2g%n", shop.getRetailPrice() * (quantity - amountToGet)) + ".");
		Shop newShop = new Shop(shop);
		newShop.removeQuantity(quantity - amountToGet);
		@SuppressWarnings("unused")
		ShopModificationEvent modify = new ShopModificationEvent(newShop);
		return true;
	}
	
	
	
	/**
	 * Causes a player to sell items to a given shop, depending on a number of factors.
	 * @param player the player selling items
	 * @param shop the shop to which items are being sold
	 * @param quantity the number of items being sold
	 * @return true if transaction successful<br>false otherwise
	 */
	public boolean shopSell (Player player, Shop shop, int quantity) {
		// Check if pawning is allowed
		if (!shop.canPawn()) {
			player.sendMessage(ChatColor.RED + "This shop does not allow pawning of items.");
			return false;
		}
		// Check if shop has enough stock capacity to handle the request
		if ((shop.getMaxQuantity() - shop.getQuantity()) < quantity) {
			player.sendMessage(ChatColor.RED + "This shop does not have enough stock capacity available.");
			if (plugin.getServer().getOfflinePlayer(shop.getOwner()).isOnline()
				&& shop.getQuantity() == shop.getMaxQuantity()) {
				plugin.getServer().getPlayer(shop.getOwner()).sendMessage(ChatColor.RED + "Your " + shop.getMaterialData().getItemType().toString() + " shop is at full capacity. Consider increasing its size.");
			}
			return false;
		}
		// Check economic requirements
		float cost = shop.getPawnPrice() * (float) quantity;
		if (!plugin.economy.hasAccount(shop.getOwner()))
			plugin.economy.createPlayerAccount(shop.getOwner());
		if (plugin.economy.getBalance(shop.getOwner()) < cost) {
			player.sendMessage(ChatColor.RED + "This shop's owner does not have enough money."
				+ "You need $"
				+ String.format("%.2g%n", cost)
				+ " to complete this transaction.");
			return false;
		}
		// Remove items from player's inventory
		String name = player.getName();
		if (Inv.count(player.getInventory(), shop.getMaterialData().getItemType()) < quantity) {
			player.sendMessage(ChatColor.RED + "You do not have " + quantity + " " + shop.getMaterialData().getItemType().toString() + ".");
			return false;
		}
		Inv.remove(player.getInventory(), shop.getMaterialData().getItemType(), quantity);
		// Pay player for items sold
		plugin.economy.depositPlayer(name, shop.getPawnPrice() * quantity);
		plugin.economy.withdrawPlayer(shop.getOwner(), shop.getPawnPrice() * quantity);
		player.sendMessage(ChatColor.GREEN + "You sold "
				+ (quantity) + " "
				+ shop.getMaterialData().getItemType().toString() + " for $"
				+ String.format("%.2g%n", shop.getPawnPrice() * quantity) + ".");
		Shop newShop = new Shop(shop);
		newShop.addQuantity(quantity);
		@SuppressWarnings("unused")
		ShopModificationEvent modify = new ShopModificationEvent(newShop);
		return true;
	}
	
}
