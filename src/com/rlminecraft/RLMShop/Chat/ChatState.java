package com.rlminecraft.RLMShop.Chat;

import com.rlminecraft.RLMShop.Shop;

public class ChatState {
	public final ChatStatus status;
	public final Shop shop;
	public final float arg;
	
	public ChatState() {
		this(ChatStatus.NONE);
	}
	public ChatState (ChatStatus status) {
		this(status, null);
	}
	public ChatState (ChatStatus status, Shop shop) {
		this(status, shop, 0);
	}
	public ChatState (ChatStatus status, Shop shop, int arg) {
		this(status, shop, (float)arg);
	}
	public ChatState (ChatStatus status, Shop shop, float arg) {
		this.status = status;
		this.shop = shop;
		this.arg = arg;
	}
}
