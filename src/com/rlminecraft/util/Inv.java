package com.rlminecraft.util;

import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Inv {
	
	public static int count (Inventory inv, Material material) {
		int c = 0;
		ListIterator<ItemStack> it = inv.iterator();
		while (it.hasNext()) {
			ItemStack stack = it.next();
			if (stack.getType() == material) {
				c += stack.getAmount();
			}
		}
		return c;
	}
	
	
	public static void remove (Inventory inv, Material material, int amount) {
		ListIterator<ItemStack> it = inv.iterator();
		int remaining = amount;
		int index = 0;
		while (it.hasNext() && remaining > 0) {
			ItemStack stack = it.next();
			if (stack.getType() == material) {
				if (stack.getAmount() <= remaining) {
					remaining -= stack.getAmount();
					inv.remove(stack);
				} else {
					stack.setAmount(stack.getAmount() - remaining);
					remaining = 0;
					inv.setItem(index, stack);
				}
			}
			index++;
		}
	}
	
}
