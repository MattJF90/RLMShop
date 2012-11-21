package com.rlminecraft.RLMShop;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class RLMShop extends JavaPlugin {
	Logger console;
	public static Economy economy = null;
	Plugin towny = null;
	Config conf;
	ShopStorage store;
	
	/**
	 * Called upon enabling of plugin
	 */
	public void onEnable() {
		
		// Get logger
		console = this.getLogger();
		
		// Hook into plugins
		if (setupEconomy()) {
			console.info("Hooked into Vault");
		} else {
			console.severe("Vault not found! Disabling RLMShop...");
			this.setEnabled(false);
		}
		if (setupTowny()) {
			console.info("Hooked into Towny");
		}
		
		// Load config
		conf = new Config(getConfig());
		
		// Access storage method
		store = new ShopStorage(
				StorageType.MYSQL, 
				(String) conf.getSetting("storage.host"), 
				(String) conf.getSetting("storage.database"), 
				(String) conf.getSetting("storage.username"), 
				(String) conf.getSetting("storage.password")
			);
		
		// Register event lister
		this.getServer().getPluginManager().registerEvents(new ShopListener(this), this);
	}
	
	/**
	 * Called upon disabling of plugin
	 */
	public void onDisable() {
		store.localToDBIfChange();
	}
	
	/**
	 * Finds and sets up the economy via Vault
	 * @return true if hooked to economy, false otherwise
	 */
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return economyProvider != null;
	}
	
	/**
	 * Finds and hooks into Towny
	 * @return true if hooked to Towny, false otherwise
	 */
	private boolean setupTowny() {
		boolean enabled = getServer().getPluginManager().isPluginEnabled("Towny");
		if (enabled) {
			towny = getServer().getPluginManager().getPlugin("Towny");
		}
		return enabled;
	}
}
