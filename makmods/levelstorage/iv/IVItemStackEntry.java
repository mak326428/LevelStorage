package makmods.levelstorage.iv;

import net.minecraft.item.ItemStack;

public class IVItemStackEntry implements IVEntry {

	private final ItemStack stack;
	private final int value;

	public IVItemStackEntry(ItemStack stack, int value) {
		this.stack = stack;
		this.value = value;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getValue() {
		return value;
	}
	
	public IVItemStackEntry clone() {
		return new IVItemStackEntry(stack.copy(), value);
	}
}
