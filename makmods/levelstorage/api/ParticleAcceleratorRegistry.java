package makmods.levelstorage.api;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;

public class ParticleAcceleratorRegistry {
	// You're crazy if you ever modify this.
	// Made private for security from Greg.
	private static Map<ItemStack, Integer> itemValues;

	private static void addDefaultValues() {
		addValue(new ItemStack(Item.diamond), 40);
		addValue(new ItemStack(Item.netherStar), 20000);
		addValue(new ItemStack(Item.enderPearl), 70);
		// TODO: add more values.
	}

	public static void addValue(ItemStack stack, int value) {
		if (itemValues == null) {
			itemValues = Maps.newHashMap();
			addDefaultValues();
		}
		itemValues.put(stack.copy(), value);
	}

	/**
	 * Returns -1 if not found, otherwise actual value for the item.
	 * 
	 * @param stack
	 * @return
	 */
	public static int getValueFor(ItemStack stack) {
		if (stack == null)
			return -1;
		if (itemValues == null) {
			itemValues = Maps.newHashMap();
			addDefaultValues();
		}
		for (Entry<ItemStack, Integer> entry : itemValues.entrySet()) {
			if (entry.getKey() == null)
				continue;
			if (entry.getValue() == 0)
				continue;
			ItemStack currStack = entry.getKey();
			if (currStack.itemID == stack.itemID
					&& currStack.getItemDamage() == stack.getItemDamage())
				return entry.getValue();
		}
		return -1;
	}
}
