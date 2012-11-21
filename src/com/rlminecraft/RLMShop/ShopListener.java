package com.rlminecraft.RLMShop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ShopListener implements Listener {
	
	private RLMShop plugin;
	
	public ShopListener(RLMShop instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak (BlockBreakEvent event) {
		// Check if
	}
	
}
