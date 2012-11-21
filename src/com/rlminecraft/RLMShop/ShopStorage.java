package com.rlminecraft.RLMShop;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.Location;

public class ShopStorage {
	
	StorageType type;
	Object db;
	
	HashMap<String,HashMap<Integer,HashMap<Integer,HashMap<Integer,Shop>>>> storage;
	// Delayed-save lists
	List<Shop> creations;	// Shops created locally but not in db
	List<Shop> updates;		// Shops updated locally but not updated in db
	List<Shop> trash;		// Shops deleted locally but still in db
	
	/**
	 * Shop storage constructor for MySQL storage
	 * @param type storage format (StorageType.MYSQL)
	 * @param host location of database
	 * @param database database name
	 * @param username database username
	 * @param password database password
	 */
	public ShopStorage (StorageType type, String host, String database, String username, String password) {
		// Storage for MySQL only
		if (type != StorageType.MYSQL) {
			type = null;
			return;
		}
		
		// Perform database setup
		MySQLConnector db = new MySQLConnector(host, username, password);
		db.connect();
		db.execute("USE " + database);
		boolean success = db.execute(
				"CREATE TABLE IF NOT EXISTS rlmshop "
				// Location
				+ "x INT, "
				+ "y INT, "
				+ "z INT, "
				+ "world TEXT, "
				// Item
				+ "material INT, "
				+ "subdata INT, "
				// Owner
				+ "owner VARCHAR(16), "
				// Prices
				+ "retail FLOAT, "
				+ "pawn FLOAT, "
				// Quantity
				+ "quantity INT, "
				+ "max_quantity INT"
			);
		if (!success) {
			type = null;
			return;
		}
		this.db = db;
		
		// Perform memory storage setup
		storage = new HashMap<String,HashMap<Integer,HashMap<Integer,HashMap<Integer,Shop>>>>();
		creations = new LinkedList<Shop>();
		updates = new LinkedList<Shop>();
		trash = new LinkedList<Shop>();
	}
	
	
	/**
	 * Commits all local changes to the database
	 */
	public void localChangesToDB () {
		// Create shops
		ListIterator<Shop> shop = creations.listIterator();
		while (shop.hasNext()) {
			if (dbCreateShop(shop.next())) shop.remove();
		}
		// Update shops
		shop = updates.listIterator();
		while (shop.hasNext()) {
			if (dbUpdateShop(shop.next())) shop.remove();
		}
		// Delete shops
		shop = trash.listIterator();
		while (shop.hasNext()) {
			if (dbDeleteShop(shop.next())) shop.remove();
		}
	}
	
	
	public void localToDBIfChange () {
		if ( !creations.isEmpty()
		  || !updates.isEmpty()
		  || !trash.isEmpty()   ) {
			localToDB();
		}
	}
	
	
	public void localToDB () {
		// Conenct to database
		MySQLConnector db = null;
		if (this.db instanceof MySQLConnector) {
			db = (MySQLConnector) this.db;
		} else {
			return;
		}
		// Insert all data into database
		for (String world : storage.keySet()) {
			for (Integer x : storage.get(world).keySet()) {
				for (Integer y : storage.get(world).get(x).keySet()) {
					for (Integer z : storage.get(world).get(x).get(y).keySet()) {
						// Save data to database
						Shop shop = storage.get(world).get(x).get(y).get(z);
						String query = "INSERT INTO rlmshop VALUES ("
								// Location
								+ shop.getLocation().getBlockX() + ", "
								+ shop.getLocation().getBlockY() + ", "
								+ shop.getLocation().getBlockZ() + ", "
								+ shop.getLocation().getWorld().getName() + ", "
								// Item
								+ shop.getMaterialData().getItemTypeId() + ", "
								+ (int) shop.getMaterialData().getData() + ", "
								// Owner
								+ shop.getOwner() + ", "
								// Prices
								+ shop.getRetailPrice() + ", "
								+ shop.getPawnPrice() + ", "
								// Quantity
								+ shop.getQuantity() + ", "
								+ shop.getMaxQuantity() + ")"
						+ "ON DUPLICATE KEY UPDATE "
								+ "material=" + shop.getMaterialData().getItemTypeId() + ", "
								+ "subdata=" + (int) shop.getMaterialData().getData() + ", "
								+ "owner=" + shop.getOwner() + ", "
								+ "retail=" + shop.getRetailPrice() + ", "
								+ "pawn=" + shop.getPawnPrice() + ", "
								+ "quantity=" + shop.getQuantity() + ", "
								+ "max_quantity=" + shop.getMaxQuantity() + " "
							+ "WHERE "
								+ "x=" + shop.getLocation().getBlockX() + " AND "
								+ "y=" + shop.getLocation().getBlockY() + " AND "
								+ "z=" + shop.getLocation().getBlockZ() + " AND "
								+ "world=" + shop.getLocation().getWorld().getName() ;
						boolean result = db.execute(query);
						if (result) {
							System.out.println("Database write successful!");
						} else {
							System.out.println("ERROR: Database write failed!");
						}
					}
				}
			}
		}
	}
	
	
	/*/**
	 * Grabs a shop from the database
	 * @param loc Location of the shop's item frame
	 * @return the requested shop (or null if non-existent)
	 */ /*
	public Shop accessShop (Location loc) {
		return null;
	}*/
	
	
	/**
	 * Creates a shop in memory
	 * @param shop the shop to be created
	 * @return true on successful creation<br>false otherwise
	 */
	public boolean createShop (Shop shop) {
		String world = shop.getLocation().getWorld().getName();
		int x = shop.getLocation().getBlockX();
		int y = shop.getLocation().getBlockY();
		int z = shop.getLocation().getBlockZ();
		
		// Create internal maps if non-existant
		if (!storage.containsKey(world))
			storage.put(world, new HashMap<Integer,HashMap<Integer,HashMap<Integer,Shop>>>());
		if (!storage.get(world).containsKey(x))
			storage.get(world).put(x, new HashMap<Integer,HashMap<Integer,Shop>>());
		if (!storage.get(world).get(x).containsKey(y))
			storage.get(world).get(x).put(y, new HashMap<Integer,Shop>());
		if (!storage.get(world).get(x).get(y).containsKey(z)) {
			storage.get(world).get(x).get(y).put(z, shop);
			// Register as new shop for DB save
			creations.add(shop);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Updates a shop in memory
	 * @param shop the shop to be updated
	 * @return true on successful update<br>false otherwise
	 */
	public boolean updateShop (Shop shop) {
		String world = shop.getLocation().getWorld().getName();
		int x = shop.getLocation().getBlockX();
		int y = shop.getLocation().getBlockY();
		int z = shop.getLocation().getBlockZ();
		
		// Exit if shop does not exist in internal maps
		if (!storage.containsKey(world)) return false;
		if (!storage.get(world).containsKey(x)) return false;
		if (!storage.get(world).get(x).containsKey(y)) return false;
		if (!storage.get(world).get(x).get(y).containsKey(z)) return false;
		
		// Update shop
		storage.get(world).get(x).get(y).put(z, shop);
		// Register as modified shop for DB save
		updates.add(shop);
		return true;
	}
	
	
	/**
	 * Removes a shop from memory
	 * @param shop the shop to be deleted
	 * @return true on successful removal<br>false otherwise
	 */
	public boolean deleteShop (Shop shop) {
		String world = shop.getLocation().getWorld().getName();
		int x = shop.getLocation().getBlockX();
		int y = shop.getLocation().getBlockY();
		int z = shop.getLocation().getBlockZ();
		// Exit if shop does not exist in internal maps
		if (!storage.containsKey(world)) return false;
		if (!storage.get(world).containsKey(x)) return false;
		if (!storage.get(world).get(x).containsKey(y)) return false;
		if (!storage.get(world).get(x).get(y).containsKey(z)) return false;
		// Remove shop
		storage.get(world).get(x).get(y).remove(z);
		// Remove empty maps caused by removal
		if (storage.get(world).get(x).get(y).isEmpty()) {
			storage.get(world).get(x).remove(y);
			if (storage.get(world).get(x).isEmpty()) {
				storage.get(world).remove(x);
				if (storage.get(world).isEmpty()) {
					storage.remove(world);
				}
			}
		}
		// Register as deleted shop for DB save
		trash.add(shop);
		return true;
	}
	
	
	/**
	 * Inserts a new shop into the database
	 * @param shop
	 * @return true on successful creation<br>false otherwise
	 */
	public boolean dbCreateShop (Shop shop) {
		switch (type) {
		case MYSQL:
			// Check for proper storage
			MySQLConnector db = null;
			if (this.db instanceof MySQLConnector) {
				db = (MySQLConnector) this.db;
			} else {
				return false;
			}
			
			// Check and/or establish database connectivity
			if (!db.isConnected()) {
				if (!db.connect()) return false;
			}
			
			// Create new shop
			String query = "INSERT INTO rlmshop VALUES ("
					// Location
					+ shop.getLocation().getBlockX() + ", "
					+ shop.getLocation().getBlockY() + ", "
					+ shop.getLocation().getBlockZ() + ", "
					+ shop.getLocation().getWorld().getName() + ", "
					// Item
					+ shop.getMaterialData().getItemTypeId() + ", "
					+ (int) shop.getMaterialData().getData() + ", "
					// Owner
					+ shop.getOwner() + ", "
					// Prices
					+ shop.getRetailPrice() + ", "
					+ shop.getPawnPrice() + ", "
					// Quantity
					+ shop.getQuantity() + ", "
					+ shop.getMaxQuantity() + ")" ;
			return db.execute(query);
		default:
			return false;
		}
	}
	
	
	/**
	 * Updates a shop in the database
	 * @param shop
	 * @return true on successful update<br>false otherwise
	 */
	public boolean dbUpdateShop (Shop shop) {
		switch (type) {
		case MYSQL:
			// Check for proper storage
			MySQLConnector db = null;
			if (this.db instanceof MySQLConnector) {
				db = (MySQLConnector) this.db;
			} else {
				return false;
			}
			
			// Check and/or establish database connectivity
			if (!db.isConnected()) {
				if (!db.connect()) return false;
			}
			
			// Create new shop
			String query = "UPDATE rlmshop SET "
					+ "material=" + shop.getMaterialData().getItemTypeId() + ", "
					+ "subdata=" + (int) shop.getMaterialData().getData() + ", "
					+ "owner=" + shop.getOwner() + ", "
					+ "retail=" + shop.getRetailPrice() + ", "
					+ "pawn=" + shop.getPawnPrice() + ", "
					+ "quantity=" + shop.getQuantity() + ", "
					+ "max_quantity=" + shop.getMaxQuantity() + " "
				+ "WHERE "
					+ "x=" + shop.getLocation().getBlockX() + " AND "
					+ "y=" + shop.getLocation().getBlockY() + " AND "
					+ "z=" + shop.getLocation().getBlockZ() + " AND "
					+ "world=" + shop.getLocation().getWorld().getName() ;
			return db.execute(query);
		default:
			return false;
		}
	}
	
	
	/**
	 * Removes a shop from the database
	 * @param shop
	 * @return true on successful deletion<br>false otherwise
	 */
	public boolean dbDeleteShop (Shop shop) {
		return dbDeleteShop(shop.getLocation());
	}
	
	
	/**
	 * Removes a shop at a specific location from the database
	 * @param loc location of the shop
	 * @return true on successful deletion<br>false otherwise
	 */
	public boolean dbDeleteShop (Location loc) {
		switch (type) {
		case MYSQL:
			// Check for proper storage
			MySQLConnector db = null;
			if (this.db instanceof MySQLConnector) {
				db = (MySQLConnector) this.db;
			} else {
				return false;
			}
			
			// Check and/or establish database connectivity
			if (!db.isConnected()) {
				if (!db.connect()) return false;
			}
			
			// Delete shop at location
			String query = "DELETE FROM rlmshop WHERE "
					+ "x = " + loc.getBlockX() + " AND "
					+ "y = " + loc.getBlockY() + " AND "
					+ "z = " + loc.getBlockZ() + " AND "
					+ "world = " + loc.getWorld();
					;
			return db.execute(query);
		default:
			return false;
		}
	}
	
}
