package com.rlminecraft.RLMShop.Prompt;

import com.rlminecraft.RLMShop.Shop;

public class PromptState {
	public final PromptAction action;
	public final Shop shop;
	public final float arg;
	
	public PromptState() {
		this(PromptAction.NONE);
	}
	public PromptState (PromptAction action) {
		this(action, null);
	}
	public PromptState (PromptAction action, Shop shop) {
		this(action, shop, 0);
	}
	public PromptState (PromptAction action, Shop shop, int arg) {
		this(action, shop, (float)arg);
	}
	public PromptState (PromptAction action, Shop shop, float arg) {
		this.action = action;
		this.shop = shop;
		this.arg = arg;
	}
}
