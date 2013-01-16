package com.rlminecraft.RLMShop.Prompt;

public enum PromptAction {
	NONE,					// Normal chat
	
	// OWNER
	OWNERPROMPT,			// Initial owner prompt
	ADD,					// Adding items
	CONFIRMADD,				// Confirm adding items
	REMOVE,					// Removing items
	CONFIRMREMOVE,			// Confirm removing items
	SET,					// Setting shop properties
	SETRETAIL,				// Setting retail price
	CONFIRMSETRETAIL,		// Confirm setting retail price
	SETPAWN,				// Setting pawn price
	CONFIRMSETPAWN,			// Confirm setting pawn price
	SETMAXQUANTITY,			// Setting maximum quantity
	CONFIRMSETMAXQUANTITY,	// Confirm setting maximum quantity
	DESTROY,				// Destroying shop
	CONFIRMDESTROY,			// Confirm destroying shop
	
	// PLAYER (non-owner)
	PLAYERPROMPT,			// Initial (non-owner) player prompt
	BUY,					// Buying items
	CONFIRMBUY,				// Confirm buying items
	SELL,					// Selling items
	CONFIRMSELL,			// Confirm selling items
	
	// ADMIN
	ADMINPROMPT				// Initial admin prompt
}
