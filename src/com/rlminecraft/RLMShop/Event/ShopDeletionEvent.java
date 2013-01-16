package com.rlminecraft.RLMShop.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rlminecraft.RLMShop.Shop;

/**
 * Called upon deletion of a shop
 * @author Matt Fielding
 */
public class ShopDeletionEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	Shop shop;
	
	
	/**
	 * <b><i>Event Constructor</i></b><br>
	 * Removes a shop from the given storage medium if it exists
	 * @param storage where the shop is stored
	 * @param shop the shop to be deleted
	 */
	public ShopDeletionEvent(Shop shop) {
		this.shop = shop;
	}
	
	/**
	 * @return the shop being deleted
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