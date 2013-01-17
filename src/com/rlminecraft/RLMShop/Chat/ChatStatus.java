package com.rlminecraft.RLMShop.Chat;

public enum ChatStatus {
	NONE,					// Normal chat
	
	// Initial prompts
	PROMPT_CREATE,			// New shop creation
	PROMPT_OWNER,			// Owner selects shop
	PROMPT_PLAYER,			// Non-owner selects shop
	PROMPT_ADMIN,			// Admin selects shop
	
	// Owner States
	INPUT_ADD,				// Adding items
	CONFIRM_ADD,			// Confirm adding items
	INPUT_REMOVE,			// Removing items
	CONFIRM_REMOVE,			// Confirm removing items
	INPUT_SET,				// Setting shop properties
	INPUT_SET_RETAIL,		// Setting retail price
	CONFIRM_SET_RETAIL,		// Confirm setting retail price
	INPUT_SETPAWN,			// Setting pawn price
	CONFIRM_SET_PAWN,		// Confirm setting pawn price
	INPUT_SET_MAX,			// Setting maximum quantity
	CONFIRM_SET_MAX,		// Confirm setting maximum quantity
	CONFIRM_DESTROY,		// Confirm destroying shop
	
	// PLAYER (non-owner)
	INPUT_BUY,				// Buying items
	CONFIRM_BUY,			// Confirm buying items
	INPUT_SELL,				// Selling items
	CONFIRM_SELL,			// Confirm selling items
}
