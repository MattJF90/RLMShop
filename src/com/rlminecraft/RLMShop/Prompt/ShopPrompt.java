package com.rlminecraft.RLMShop.Prompt;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.rlminecraft.RLMShop.Shop;

public class ShopPrompt {
	
	/*
	 * OWNER PROMPTS
	 */
	public static void OwnerPrompt (Player player) {
		player.sendMessage(ChatColor.YELLOW + "This is your shop.");
		player.sendMessage(ChatColor.YELLOW + "What would you like to do?");
		player.sendMessage("   add");
		player.sendMessage("   remove");
		player.sendMessage("   set");
		player.sendMessage("   destroy");
	}
	
	public static void Add (Player player, Shop shop) {
		player.sendMessage(ChatColor.YELLOW + "How much would you like to add to the shop?");
		player.sendMessage(ChatColor.GRAY + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
	}
	
	public static void Remove (Player player, Shop shop) {
		player.sendMessage(ChatColor.YELLOW + "How much would you like to remove from the shop?");
		player.sendMessage(ChatColor.GRAY + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
	}
	
	public static void Set (Player player) {
		player.sendMessage(ChatColor.YELLOW + "Which property would you like to set?");
		player.sendMessage("   retail price");
		player.sendMessage("   pawn price");
		player.sendMessage("   max quantity");
	}
	
	public static void SetRetail (Player player) {
		player.sendMessage(ChatColor.YELLOW + "What price would you like to sell each item at?");
	}
	
	public static void ConfirmSetRetail (Player player, float price, boolean successful) {
		if (successful) {
			player.sendMessage(ChatColor.GREEN + "Shop retail price set to $" + String.format("%.2g%n", price));
		} else {
			player.sendMessage(ChatColor.RED + "Shop retail price not changed - invalid entry");
		}
	}
	
	public static void SetPawn (Player player) {
		player.sendMessage(ChatColor.YELLOW + "What price would you like to buy each item at?");
	}
	
	public static void ConfirmSetPawn (Player player, float price, boolean successful) {
		if (successful) {
			player.sendMessage(ChatColor.GREEN + "Shop pawn price set to $" + String.format("%.2g%n", price));
		} else {
			player.sendMessage(ChatColor.RED + "Shop pawn price not changed - invalid entry");
		}
	}
	
	public static void SetMaxQuantity (Player player) {
		player.sendMessage(ChatColor.YELLOW + "What is the maximum number of items you want yous shop to hold?");
	}
	
	public static void ConfirmSetMaxQuantity (Player player, int amount, boolean successful) {
		if (successful) {
			player.sendMessage(ChatColor.GREEN + "Shop maximum quantity has been set to " + amount);
		} else {
			player.sendMessage(ChatColor.RED + "Shop maximum quantity not changed - current quantity exceeds this limit");
		}
	}
	
	public static void Destroy (Player player) {
		player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Are you sure you want to destroy this shop?");
	}
	
	public static void ConfirmDestroy (Player player) {
		player.sendMessage(ChatColor.GREEN + "Shop destroyed!");
	}
	
	
	
	/*
	 * PLAYER PROMPTS
	 */
	public static void PlayerPrompt (Player player, Shop shop) {
		if (!shop.canRetail() && !shop.canPawn()) {
			player.sendMessage(ChatColor.YELLOW + "This shop is not yet configured.");
		}
		player.sendMessage(ChatColor.YELLOW + "What would you like to do?");
		if (shop.canRetail()) player.sendMessage("   buy");
		if (shop.canPawn()) player.sendMessage("   sell");
	}
	
	public static void Buy (Player player, Shop shop) {
		player.sendMessage(ChatColor.YELLOW + "How much would you like to buy?");
		player.sendMessage(ChatColor.GRAY + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
		player.sendMessage(ChatColor.GRAY + "  Price per Item: $" + String.format("%.2g%n", shop.getRetailPrice()));
	}
	
	public static void ConfirmBuy (Player player, Shop shop, int amount) {
		player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "---- " + ChatColor.GREEN + "Retail Transaction" + ChatColor.AQUA + " ----");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Item:   " + shop.getMaterialData().getItemType().toString());
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Amount: " + amount);
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Cost:   $" + String.format("%.2g%n", shop.getRetailPrice()*amount));
		player.sendMessage(ChatColor.YELLOW + "Do you accept?");
	}
	
	public static void Sell (Player player, Shop shop) {
		player.sendMessage(ChatColor.YELLOW + "How much would you like to sell?");
		player.sendMessage(ChatColor.GRAY + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
		player.sendMessage(ChatColor.GRAY + "  Income per Item: $" + String.format("%.2g%n", shop.getRetailPrice()));
	}
	
	public static void ConfirmSell (Player player, Shop shop, int amount) {
		player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "---- " + ChatColor.GREEN + "Pawn Transaction" + ChatColor.AQUA + " ----");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Item:   " + shop.getMaterialData().getItemType().toString());
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Amount: " + amount);
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Income: $" + String.format("%.2g%n", shop.getPawnPrice()*amount));
		player.sendMessage(ChatColor.YELLOW + "Do you accept?");
	}
	
	public static void Complete (Player player) {
		player.sendMessage(ChatColor.GREEN + "Transaction completed!");
	}
	
	public static void Cancel (Player player) {
		player.sendMessage(ChatColor.YELLOW + "Transaction cancelled!");
	}
	
	public static void Invalid (Player player) {
		player.sendMessage(ChatColor.RED + "Invalid entry! Exiting prompt mode.");
	}
	
	public static void Invalid_Repeat (Player player) {
		player.sendMessage(ChatColor.RED + "Invalid entry! Please try again.");
	}
	
	
	
	/*
	 * ADMIN PROMPTS
	 */
	public static void AdminPrompt (Player player, Shop shop) {
		player.sendMessage(ChatColor.YELLOW + "This is another player's shop.");
		player.sendMessage(ChatColor.YELLOW + "What would you like to do?");
		if (shop.canRetail()) player.sendMessage("   buy");
		if (shop.canPawn()) player.sendMessage("   sell");
		player.sendMessage("   add");
		player.sendMessage("   remove");
		player.sendMessage("   set");
		player.sendMessage("   destroy");
	}
	
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * */
	
	
	
	
	public static int IntResponse (String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return -2;
		}
	}
	
	public static float FloatResponse (String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return -2;
		}
	}
	
	public static int BoolResponse (String str) {
		String response = str.toLowerCase();
		if (   response == "yes"
			|| response == "y") {
			return 1;
		}
		else if (  response == "no"
				|| response == "n") {
			return 0;
		}
		else {
			return -1;
		}
	}
	
}
