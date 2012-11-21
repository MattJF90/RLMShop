package com.rlminecraft.RLMShop;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	
	HashMap<String,Object> conf;
	
	
	public Config (FileConfiguration config) {
		conf = new HashMap<String,Object>();
		
		conf.put("general.cost", get(config,"general.cost",15.0));
		
		conf.put("towny.town_only", get(config,"towny.town_only",true));
		conf.put("towny.shop_plot", get(config,"towny.shop_plot",true));
		
		conf.put("storage.type", get(config,"storage.type",StorageType.MYSQL));
		conf.put("storage.host", get(config,"storage.host","localhost"));
		conf.put("storage.database", get(config,"storage.database","minecraft"));
		conf.put("storage.username", get(config,"storage.username","root"));
		conf.put("storage.password", get(config,"storage.password",""));
	}
	
	
	public Object getSetting (String node) {
		if (conf.containsKey(node)) {
			return conf.get(node);
		} else {
			Logger console = Logger.getLogger(Config.class.getName());
			console.warning("Node " + node + " missing from the config!");
			return null;
		}
	}
	
	
	private Object get (FileConfiguration config, String node, Object def) {
		if (config.contains(node)) {
			return config.get(node);
		} else {
			return def;
		}
	}
	
}
