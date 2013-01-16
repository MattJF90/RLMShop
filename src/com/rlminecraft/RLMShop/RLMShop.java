package com.rlminecraft.RLMShop;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.rlminecraft.RLMShop.Prompt.PromptState;

import net.milkbowl.vault.economy.Economy;

public class RLMShop extends JavaPlugin {
	PluginState state;
	Logger console;
	public Economy economy = null;
	Plugin towny = null;
	Config conf;
	ShopStorage store;
	HashMap<String,PromptState> playerChatState;
	
	/**
	 * Called upon enabling of plugin
	 */
	public void onEnable() {
		
		// Set initial plugin state
		state = PluginState.NORMAL;
		playerChatState = new HashMap<String,PromptState>();
		
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
				this,
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
		switch (state) {
		case NORMAL:
			store.localToDBIfChange();
			break;
		case CRASHED:
			console.severe("RLMShop has been shut down due to a severe error!");
			break;
		default:
			console.severe("RLMShop has been shut down for unknown reasons!");
		}
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
