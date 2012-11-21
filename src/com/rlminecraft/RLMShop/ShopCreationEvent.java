package com.rlminecraft.RLMShop;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopCreationEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	Shop shop;
	
	
	public ShopCreationEvent(ShopStorage storage, Shop shop) {
		this.shop = shop;
		storage.createShop(this.shop);
	}
	
	public Shop getShop() {
		return this.shop;
	}
	
	
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}