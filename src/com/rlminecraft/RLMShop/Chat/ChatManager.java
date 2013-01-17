package com.rlminecraft.RLMShop.Chat;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class ChatManager {
	
	private HashMap<String,ChatState> playerChatState;
	
	public ChatManager () {
		playerChatState = new HashMap<String,ChatState>();
	}
	
	
	public void resetMode (String player) {
		
	}
	
	
	public void sendMessage (Player player) {
		if (!playerChatState.containsKey(player.getName())) return;
		ChatState state = playerChatState.get(player.getName());
		switch (state.status) {
		PROMPT_CREATE:			// New shop creation
			Messages.CreationPrompt(player);
			break;
		PROMPT_OWNER:			// Owner selects shop
			Messages.OwnerPrompt(player);
			break;
		PROMPT_PLAYER:			// Non-owner selects shop
			Messages.PlayerPrompt(player, state.shop);
			break;
		PROMPT_ADMIN:			// Admin selects shop
			Messages.AdminPrompt(player, state.shop);
			break;
		INPUT_ADD:				// Adding items
			Messages.Add(player, state.shop);
			break;
		CONFIRM_ADD:			// Confirm adding items
			break;
		INPUT_REMOVE:			// Removing items
			break;
		CONFIRM_REMOVE:			// Confirm removing items
			break;
		INPUT_SET:				// Setting shop properties
			break;
		INPUT_SET_RETAIL:		// Setting retail price
			break;
		CONFIRM_SET_RETAIL:		// Confirm setting retail price
			break;
		INPUT_SETPAWN:			// Setting pawn price
			break;
		CONFIRM_SET_PAWN:		// Confirm setting pawn price
			break;
		INPUT_SET_MAX:			// Setting maximum quantity
			break;
		CONFIRM_SET_MAX:		// Confirm setting maximum quantity
			break;
		CONFIRM_DESTROY:		// Confirm destroying shop
			break;
		INPUT_BUY:				// Buying items
			break;
		CONFIRM_BUY:			// Confirm buying items
			break;
		INPUT_SELL:				// Selling items
			break;
		CONFIRM_SELL:			// Confirm selling items
			break;
		}
	}
	
}
