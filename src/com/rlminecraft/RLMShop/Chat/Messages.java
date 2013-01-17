package com.rlminecraft.RLMShop.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.rlminecraft.RLMShop.Shop;

public class Messages {
	
	static final ChatColor HEADER   = ChatColor.LIGHT_PURPLE;
	static final ChatColor QUESTION = ChatColor.YELLOW;
	static final ChatColor CHOICE   = ChatColor.WHITE;
	static final ChatColor INFO     = ChatColor.GRAY;
	static final ChatColor SUCCESS  = ChatColor.GREEN;
	static final ChatColor FAILURE  = ChatColor.RED;
	
	
	/*
	 * CREATION PROMPTS
	 */
	public static void CreationPrompt (Player player) {
		player.sendMessage(HEADER   + "--- Shop Creation ---");
		player.sendMessage(QUESTION + "Would you like to create a shop?");
	}
	
	/*
	 * OWNER PROMPTS
	 */
	public static void OwnerPrompt (Player player) {
		player.sendMessage(QUESTION + "This is your shop.");
		player.sendMessage(QUESTION + "What would you like to do?");
		player.sendMessage(CHOICE   + "   add");
		player.sendMessage(CHOICE   + "   remove");
		player.sendMessage(CHOICE   + "   set");
		player.sendMessage(CHOICE   + "   destroy");
	}
	
	public static void Add (Player player, Shop shop) {
		player.sendMessage(QUESTION + "How much would you like to add to the shop?");
		player.sendMessage(INFO     + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
	}
	
	public static void CompleteAdd (Player player, Shop shop, boolean success) {
		if (success) {
			player.sendMessage(SUCCESS + "You have successfully added "
					+ shop.getMaterialData().getItemType().toString() + " to the shop.");
		} else {
			player.sendMessage(FAILURE + "Unable to add "
					+ shop.getMaterialData().getItemType().toString() + " to the shop.");
		}
	}
	
	public static void Remove (Player player, Shop shop) {
		player.sendMessage(QUESTION + "How much would you like to remove from the shop?");
		player.sendMessage(INFO     + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
	}
	
	public static void CompleteRemove (Player player, Shop shop, boolean success) {
		if (success) {
			player.sendMessage(SUCCESS + "You have successfully removed "
					+ shop.getMaterialData().getItemType().toString() + " from the shop.");
		} else {
			player.sendMessage(FAILURE + "Unable to remove "
					+ shop.getMaterialData().getItemType().toString() + " from the shop.");
		}
	}
	
	public static void Set (Player player) {
		player.sendMessage(QUESTION + "Which property would you like to set?");
		player.sendMessage(CHOICE   + "   retail price");
		player.sendMessage(CHOICE   + "   pawn price");
		player.sendMessage(CHOICE   + "   max quantity");
	}
	
	public static void SetRetail (Player player) {
		player.sendMessage(QUESTION + "What price would you like to sell each item at?");
	}
	
	public static void ConfirmSetRetail (Player player, float price) {
		player.sendMessage(QUESTION + "Are you sure you want to set your retail price to $"
				+ String.format("%.2g%n", price) + "?");
	}
	
	public static void CompleteSetRetail (Player player, float price, boolean success) {
		if (success) {
			player.sendMessage(SUCCESS + "You have successfully changed this shop's retail price to $"
					+ String.format("%.2g%n", price) + "!");
		} else {
			player.sendMessage(FAILURE + "Unable to set this shop's retail price.");
		}
	}
	
	public static void SetPawn (Player player) {
		player.sendMessage(QUESTION + "What price would you like to buy each item at?");
	}
	
	public static void ConfirmSetPawn (Player player, float price) {
		player.sendMessage(QUESTION + "Are you sure you want to set your pawn price to $"
				+ String.format("%.2g%n", price) + "?");
	}
	
	public static void CompleteSetPawn (Player player, float price, boolean success) {
		if (success) {
			player.sendMessage(SUCCESS + "You have successfully changed this shop's retail price to $"
					+ String.format("%.2g%n", price) + "!");
		} else {
			player.sendMessage(FAILURE + "Unable to set this shop's retail price.");
		}
	}
	
	public static void SetMaxQuantity (Player player) {
		player.sendMessage(QUESTION + "What is the maximum number of items you want yous shop to hold?");
	}
	
	public static void ConfirmSetMaxQuantity (Player player, int amount, boolean successful) {
		player.sendMessage(QUESTION + "Are you sure you want to set your max quantity to "
				+ amount + "?");
	}
	
	public static void CompleteSetMaxQuantity (Player player, int amount, boolean successful) {
		if (successful) {
			player.sendMessage(SUCCESS + "You have successfully changed this shop's maximum quantity to "
					+ amount + "!");
		} else {
			player.sendMessage(FAILURE + "Unable to change this shop's maximum quantity.");
		}
	}
	
	public static void ConfirmDestroy (Player player) {
		player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Are you sure you want to destroy this shop?");
	}
	
	public static void CompleteDestroy (Player player) {
		player.sendMessage(SUCCESS + "Shop destroyed!");
	}
	
	
	
	/*
	 * PLAYER PROMPTS
	 */
	public static void PlayerPrompt (Player player, Shop shop) {
		if (!shop.canRetail() && !shop.canPawn()) {
			player.sendMessage(ChatColor.YELLOW + "This shop is not yet configured.");
		}
		player.sendMessage(QUESTION + "What would you like to do?");
		if (shop.canRetail()) player.sendMessage(CHOICE + "   buy");
		if (shop.canPawn())   player.sendMessage(CHOICE + "   sell");
	}
	
	public static void Buy (Player player, Shop shop) {
		player.sendMessage(QUESTION + "How much would you like to buy?");
		player.sendMessage(INFO     + "  Current Stock: " + shop.getQuantity() + "/" + shop.getMaxQuantity());
		player.sendMessage(INFO     + "  Price per Item: $" + String.format("%.2g%n", shop.getRetailPrice()));
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
