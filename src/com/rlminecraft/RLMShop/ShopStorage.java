package com.rlminecraft.RLMShop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.material.MaterialData;

public class ShopStorage {
	
	RLMShop parent;
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
	public ShopStorage (RLMShop instance, StorageType type, String host, String database, String username, String password) {
		this.parent = instance;
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
		// Disable plugin on failure
		if (!success) {
			parent.state = PluginState.CRASHED;
			parent.getPluginLoader().disablePlugin(parent);
		}
		this.db = db;
		
		// Perform memory storage setup
		storage = new HashMap<String,HashMap<Integer,HashMap<Integer,HashMap<Integer,Shop>>>>();
		creations = new LinkedList<Shop>();
		updates = new LinkedList<Shop>();
		trash = new LinkedList<Shop>();
		
		// Import existing shops from database
		// or disable plugin on failure
		if (!importShops()) {
			parent.state = PluginState.CRASHED;
			parent.getPluginLoader().disablePlugin(parent);
		}
	}
	
	
	/**
	 * Imports shops from the database
	 * @return true if successfully imported shops<br>false otherwise
	 */
	public boolean importShops () {
		// Clear the current data if not already empty
		if (!storage.isEmpty()) storage = new HashMap<String,HashMap<Integer,HashMap<Integer,HashMap<Integer,Shop>>>>();
		if (!creations.isEmpty()) creations = new LinkedList<Shop>();
		if (!updates.isEmpty()) updates = new LinkedList<Shop>();
		if (!trash.isEmpty()) trash = new LinkedList<Shop>();
		
		// Connect to MySQL
		if (!(this.db instanceof MySQLConnector)) return false;
		MySQLConnector db = (MySQLConnector) this.db;
		if (!db.connect()) return false;
		
		// Import shops
		db.execute("SELECT * FROM rlmshop");
		ResultSet results = db.getResults();
		try {
			results.first();
			while (!results.isAfterLast()) {
				try {
					// Get location
					int x = results.getInt("x");
					int y = results.getInt("y");
					int z = results.getInt("z");
					World world = parent.getServer().getWorld(results.getString("world"));
					Location loc = new Location(world, x, y, z);
					// Get item info
					int materialInt = results.getInt("material");
					int subdata = results.getInt("subdata");
					MaterialData material = new MaterialData(materialInt,(byte) subdata);
					// Get owner
					String owner = results.getString("owner");
					// Get price info
					int retail = results.getInt("retail");
					int pawn = results.getInt("pawn");
					// Quantity
					int quantity = results.getInt("quantity");
					int max_quantity = results.getInt("max_quantity");
					// Create shop
					Shop shop = new Shop(loc,material,owner,retail,pawn,quantity,max_quantity);
					// Insert shop into local storage (no need to recreate in DB)
					createShop(shop,false);
				}
				
				// Catch errors due to individual row read
				catch (SQLException e) {
					parent.console.warning("A shop failed to load!");
				}
				results.next();
			}
		}
		
		// Catch general (serious) SQL errors
		catch (SQLException e) {
			parent.console.severe("An error has occurred while loading shops from the database!");
			parent.console.severe("Shutting down RLMShop");
			parent.state = PluginState.CRASHED;
			parent.getPluginLoader().disablePlugin(parent);
		}
		return true;
	}
	
	
	/**
	 * Commits all local changes to the database
	 */
	public void localChangesToDB () {
		// Create shops
		localChangesToDB(ShopStatus.NEW);
		// Update shops
		localChangesToDB(ShopStatus.MODIFIED);
		// Delete shops
		localChangesToDB(ShopStatus.DELETED);
	}
	
	public void localChangesToDB (ShopStatus status) {
		ListIterator<Shop> shop;
		switch (status) {
		case NEW:
			shop = creations.listIterator();
			while (shop.hasNext()) {
				if (dbCreateShop(shop.next())) shop.remove();
			}
			break;
		case MODIFIED:
			shop = updates.listIterator();
			while (shop.hasNext()) {
				if (dbUpdateShop(shop.next())) shop.remove();
			}
			break;
		case DELETED:
			shop = trash.listIterator();
			while (shop.hasNext()) {
				if (dbDeleteShop(shop.next())) shop.remove();
			}
			break;
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
	
	
	/**
	 * Grabs a shop from local storage
	 * @param loc Location of the shop's item frame
	 * @return the requested shop (or null if non-existent)
	 */
	public Shop accessShop (Location loc) {
		String world = loc.getWorld().getName();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		if (   !storage.containsKey(world)
			|| !storage.get(world).containsKey(x)
			|| !storage.get(world).get(x).containsKey(y)
			|| !storage.get(world).get(x).get(y).containsKey(z)
			) return null;
		return storage.get(world).get(x).get(y).get(z);
	}
	
	
	/**
	 * Creates a shop in memory and queues it for a database save
	 * @param shop the shop to be created
	 * @return true on successful creation<br>false otherwise
	 */
	public boolean createShop (Shop shop) {
		return createShop(shop,true);
	}
	
	/**
	 * Creates a shop in memory, optionally allowing a database save
	 * @param shop the shop to be created
	 * @param log whether the shop is to be logged for DB save
	 * @return true on successful creation<br>false otherwise
	 */
	public boolean createShop (Shop shop, boolean log) {
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
			// Save prior changes to database if shop already has unsaved changes
			localChangesToDB(shop.getStatus());
			// Register as new shop for DB save
			if (log) {
				shop.setStatus(ShopStatus.NEW);
				creations.add(shop);
			}
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
		// Save prior changes to database if shop already has unsaved changes
		localChangesToDB(shop.getStatus());
		// Register as modified shop for next DB save
		shop.setStatus(ShopStatus.MODIFIED);
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
		// Save prior changes to database if shop already has unsaved changes
		localChangesToDB(shop.getStatus());
		// Register as deleted shop for DB save
		shop.setStatus(ShopStatus.DELETED);
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
			boolean successful = db.execute(query);
			if (successful) shop.setStatus(ShopStatus.ACTIVE);
			return successful;
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
			boolean successful = db.execute(query);
			if (successful) shop.setStatus(ShopStatus.ACTIVE);
			return successful;
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
