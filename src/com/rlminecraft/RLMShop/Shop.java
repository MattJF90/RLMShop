package com.rlminecraft.RLMShop;

import org.bukkit.Location;
import org.bukkit.material.MaterialData;

public class Shop {
	
	private Location frame;
	private MaterialData material;
	private String owner;
	private float price_retail;
	private float price_pawn;
	private int quantity;
	private int max_quantity;
	private ShopStatus status;
	
	/**
	 * Shop Constructor
	 * @param frame location of the shop (item frame)
	 * @param material item which the shop will sell/buy
	 * @param owner owner of the shop
	 */
	public Shop(Location frame, MaterialData material, String owner) {
		this.frame = frame;
		this.material = material;
		this.owner = owner;
		this.quantity = 0;
		this.status = ShopStatus.ACTIVE;
	}
	
	/**
	 * Shop Constructor
	 * @param frame location of the shop (item frame)
	 * @param material item which the shop will sell/buy
	 * @param owner owner of the shop
	 * @param unlimited whether the shop will have unlimited stock
	 */
	public Shop(Location frame, MaterialData material, String owner, boolean unlimited) {
		this.frame = frame;
		this.material = material;
		this.owner = owner;
		if (unlimited) {
			this.quantity = -1;
		} else {
			this.quantity = 0;
		}
		this.status = ShopStatus.ACTIVE;
	}
	
	/**
	 * Shop Constructor
	 * @param frame location of the shop (item frame)
	 * @param material material item which the shop will sell/buy
	 * @param owner owner of the shop
	 * @param retail retail (selling) price of the item
	 * @param pawn pawn (buying) price of the item
	 * @param quantity quantity the shop will have
	 * @param max_quantity maximum quantity the shop will hold
	 */
	public Shop(Location frame, MaterialData material, String owner, int retail, int pawn, int quantity, int max_quantity) {
		this.frame = frame;
		this.material = material;
		this.owner = owner;
		this.price_retail = retail;
		this.price_pawn = pawn;
		this.quantity = quantity;
		this.max_quantity = max_quantity;
		this.status = ShopStatus.ACTIVE;
	}
	
	
	public Shop(Shop shop) {
		this.frame = shop.getLocation();
		this.material = shop.getMaterialData();
		this.owner = shop.getOwner();
		this.price_retail = shop.getRetailPrice();
		this.price_pawn = shop.getPawnPrice();
		this.quantity = shop.getQuantity();
		this.max_quantity = shop.getMaxQuantity();
		this.status = shop.getStatus();
	}
	
	
	
	/* * * * * *
	 * SETTERS *
	 * * * * * */
	
	/**
	 * Sets the retail price of items in the shop
	 * @param price retail price of items
	 * @return true if successfully changed retail price<br>false otherwise
	 */
	public boolean setRetailPrice(int price) {
		if (price < 0 && price != -1) return false;
		this.price_retail = price;
		return true;
	}
	
	/**
	 * Sets the pawn price of items in the shop
	 * @param price pawn price of items
	 * @return true if successfully changed pawn price<br>false otherwise
	 */
	public boolean setPawnPrice(int price) {
		if (price < 0 && price != -1) return false;
		this.price_pawn = price;
		return false;
	}
	
	/**
	 * Adds items into the shop
	 * @param amount number of items to add
	 * @return true if successfully added items<br>false otherwise
	 */
	public boolean addQuantity(int amount) {
		if (amount < 0 || quantity + amount > max_quantity || quantity == -1) return false;
		quantity += amount;
		return true;
	}
	
	/**
	 * Removes items from the shop
	 * @param amount number of items to remove
	 * @return true if successfully removed items<br>false otherwise
	 */
	public boolean removeQuantity(int amount) {
		if (amount < 0 || quantity - amount < 0 || quantity == -1) return false;
		quantity -= amount;
		return true;
	}
	
	/**
	 * Sets quantity of items in the shop
	 * @param amount number of items the shop will have
	 * @return true if successfully set quantity<br>false otherwise
	 */
	public boolean setQuantity(int amount) {
		if (amount < 0 || amount > max_quantity) return false;
		quantity = amount;
		return true;
	}
	
	/**
	 * Sets max quantity of items in the shop
	 * @param amount maximum number of items the shop can have
	 * @return true if successfully set max quantity<br>false otherwise
	 */
	public boolean setMaxQuantity(int amount) {
		if (amount < quantity) return false;
		max_quantity = amount;
		return true;
	}
	
	/**
	 * Changes the status of the shop for saving purposes
	 * @param status the new status of the shop
	 * @return true if successfully changed status<br>false otherwise
	 */
	public boolean setStatus(ShopStatus status) {
		try {
			this.status = status;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	
	
	/* * * * * *
	 * GETTERS *
	 * * * * * */
	
	/**
	 * @return location of the shop (item frame)
	 */
	public Location getLocation() {
		return this.frame;
	}
	
	/**
	 * @return item in shop
	 */
	public MaterialData getMaterialData() {
		return this.material;
	}
	
	/**
	 * @return owner of shop
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @return retail price of shop items
	 */
	public float getRetailPrice() {
		return this.price_retail;
	}
	
	/**
	 * @return pawn price of shop items
	 */
	public float getPawnPrice() {
		return this.price_pawn;
	}
	
	/**
	 * @return quantity of items in shop
	 */
	public int getQuantity() {
		return this.quantity;
	}
	
	/**
	 * @return maximum quantity of items in shop
	 */
	public int getMaxQuantity() {
		return this.max_quantity;
	}
	
	/**
	 * @return the current save status of the shop
	 */
	public ShopStatus getStatus() {
		return this.status;
	}
	
	/**
	 * @return true if shop allows retail<br>false otherwise
	 */
	public boolean canRetail() {
		return this.price_retail != -1;
	}
	
	/**
	 * @return true if shop allows pawning<br>false otherwise
	 */
	public boolean canPawn() {
		return this.price_pawn != -1;
	}
	
	/**
	 * @return true if shop has unlimited quantity<br>false otherwise
	 */
	public boolean isUnlimited() {
		return this.quantity == -1;
	}
	
}
