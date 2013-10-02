package makmods.levelstorage.logic.util;

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
	 * 
	 * @param stack
	 */
	public static void checkNBT(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
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
		if (!verifyKey(stack, name)) {
			setInteger(stack, name, 0);
		}
		return stack.stackTagCompound.getInteger(name);
	}

	public static void decreaseInteger(ItemStack stack, String name, int value) {
		if (getInteger(stack, name) > 0) {
			setInteger(stack, name, getInteger(stack, name) - value);
		}
	}

	public static void decreaseIntegerIgnoreZero(ItemStack stack, String name,
	        int value) {
		setInteger(stack, name, getInteger(stack, name) - value);

	}

	public static void setString(ItemStack stack, String name, String value) {
		checkNBT(stack);
		stack.stackTagCompound.setString(name, value);
	}

	public static String getString(ItemStack stack, String name) {
		checkNBT(stack);
		if (!verifyKey(stack, name)) {
			setString(stack, name, "");
		}
		return stack.stackTagCompound.getString(name);
	}

	public static void setBoolean(ItemStack stack, String name, boolean value) {
		checkNBT(stack);
		stack.stackTagCompound.setBoolean(name, value);
	}

	public static boolean getBoolean(ItemStack stack, String name) {
		checkNBT(stack);
		if (!verifyKey(stack, name)) {
			setBoolean(stack, name, false);
		}
		return stack.stackTagCompound.getBoolean(name);
	}
	
	public static void invertBoolean(ItemStack stack, String name) {
		setBoolean(stack, name, !getBoolean(stack, name));
	}
	
	public static void setByte(ItemStack stack, String name, byte value) {
		checkNBT(stack);
		stack.stackTagCompound.setByte(name, value);
	}
	
	public static byte getByte(ItemStack stack, String name) {
		checkNBT(stack);
		if (!verifyKey(stack, name)) {
			setByte(stack, name, (byte)0);
		}
		return stack.stackTagCompound.getByte(name);
	}

	/**
	 * A bunch of helpers for easier item NBT cooldown
	 * 
	 * @author mak326428
	 * 
	 */
	public static class Cooldownable {

		public static final String COOLDOWN_NBT = "cooldown";

		/**
		 * Call this onUpdate() in your Item WARNING: you should check for
		 * !world.isRemote
		 * 
		 * @param stack
		 *            stack to update
		 * @param maxCooldown
		 *            maximum cooldown
		 */
		public static void onUpdate(ItemStack stack, int maxCooldown) {
			NBTHelper.checkNBT(stack);
			if (!NBTHelper.verifyKey(stack, COOLDOWN_NBT))
				NBTHelper.setInteger(stack, COOLDOWN_NBT, maxCooldown);
			if (NBTHelper.getInteger(stack, COOLDOWN_NBT) > 0)
				NBTHelper.decreaseInteger(stack, COOLDOWN_NBT, 1);
		}

		/**
		 * Checks for cooldown. Call it when your item needs to be used
		 * 
		 * @param stack
		 *            Stack to check
		 * @param maxCooldown
		 *            Maximum cooldown
		 * @return True if item's cooldown is zero, returns true and sets it to
		 *         maximum, otherwise false
		 */
		public static boolean use(ItemStack stack, int maxCooldown) {
			NBTHelper.checkNBT(stack);
			if (NBTHelper.getInteger(stack, COOLDOWN_NBT) > 0)
				return false;
			else {
				NBTHelper.setInteger(stack, COOLDOWN_NBT, maxCooldown);
				return true;
			}
		}
	}

}
