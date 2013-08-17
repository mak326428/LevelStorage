package makmods.levelstorage.api;

import net.minecraft.item.ItemStack;

/**
 * Represents simple ItemStack and its XP value
 */
public class XpStack {
	/**
	 * Assigned XP Value of this instance
	 */
	public int value;
	/**
	 * Assigned Stack of this instance
	 */

	public ItemStack stack;

	/**
	 * Creates new instance
	 * 
	 * @param stack
	 *            ItemStack for this instance
	 * 
	 * @param value
	 *            XP value for the given stack
	 */
	public XpStack(ItemStack stack, int value) {
		this.stack = stack;
		this.value = value;
	}
}
