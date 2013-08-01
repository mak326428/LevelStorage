package makmods.levelstorage.gui;

import java.util.HashMap;
import java.util.Map;

import makmods.levelstorage.logic.NBTInventory;
import net.minecraft.item.ItemStack;

public class NBTInventoryRegistry {

	public static HashMap<ItemStack, NBTInventory> registry;
	
	static {
		registry = new HashMap<ItemStack, NBTInventory>();
	}

	private NBTInventoryRegistry() {
		
	}

	public static ItemStack getStack(NBTInventory inv) {
		if (inv == null)
			return null;
		if (!registry.containsValue(inv))
			return null;
		for (Map.Entry entry : registry.entrySet()) {
			if (((NBTInventory)entry.getValue()) == inv)
				return ((ItemStack)entry.getKey());
		}
		return null;
	}
	
	public static void removeFromRegistry(ItemStack stack) {
		if (registry.containsKey(stack))
			return;
		registry.remove(stack);
	}
	
	public static void removeFromRegistry(NBTInventory stack) {
		if (registry.containsValue(stack))
			return;
		for (Map.Entry entry : registry.entrySet()) {
			if (((NBTInventory)entry.getValue()) == stack) {
				registry.remove(entry.getKey());
			}
		}
	}
	
	public static void addToRegistry(ItemStack stack, NBTInventory nbtinv) {
		if (registry.containsKey(stack))
			return;
		registry.put(stack, nbtinv);
	}
}
