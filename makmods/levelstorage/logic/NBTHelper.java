package makmods.levelstorage.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Just a bunch of helpers (shortcuts, better to say) for operations with NBT
 * 
 * @author mak326428
 * 
 */
public class NBTHelper {

	/**
	 * Used for null-safety
	 * @param stack
	 */
	public static void checkNBT(ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
	}
	
	/**
	 * Shortcut for NBTTagCompound.hasKey()
	 */
	public static boolean verifyKey(ItemStack stack, String name) {
		checkNBT(stack);
		return stack.stackTagCompound.hasKey(name);
	}

	public static void setInteger(ItemStack stack, String name, int value) {
		checkNBT(stack);
		stack.stackTagCompound.setInteger(name, value);
	}
	
	public static int getInteger(ItemStack stack, String name) {
		checkNBT(stack);
		if (!verifyKey(stack, name))
			setInteger(stack, name, 0);
		return stack.stackTagCompound.getInteger(name);
	}
	
	public static void setString(ItemStack stack, String name, String value) {
		checkNBT(stack);
		stack.stackTagCompound.setString(name, value);
	}
	
	public static String getString(ItemStack stack, String name) {
		checkNBT(stack);
		if (!verifyKey(stack, name))
			setString(stack, name, "");
		return stack.stackTagCompound.getString(name);
	}

}
