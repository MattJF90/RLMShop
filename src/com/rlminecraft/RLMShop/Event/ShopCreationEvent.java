package com.rlminecraft.RLMShop.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rlminecraft.RLMShop.Shop;

/**
 * Called upon creation of a shop
 * @author Matt Fielding
 */
public class ShopCreationEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	Shop shop;
	
	/**
	 * <b><i>Event Constructor</i></b><br>
	 * Creates a new shop in the given storage medium
	 * @param storage where the shop is stored
	 * @param shop the shop to be stored
	 */
	public ShopCreationEvent(Shop shop) {
		this.shop = shop;
	}
	
	/**
	 * @return the shop being created
	 */
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