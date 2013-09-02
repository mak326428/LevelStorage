package makmods.levelstorage.logic.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * A bunch of helpers for easier
 * inventory management
 * @author mak326428
 *
 */
public class InventoryUtil {

	/**
	 * Adds a stack to an inventory
	 * 
	 * @param inv
	 *            Inventory
	 * @param slot
	 *            ID of the slot
	 * @param whatToAdd
	 *            What to add
	 * @param simulate
	 *            Do not actually add to inventory, just simulate
	 * @return Whether or not whatToAdd was added
	 */
	public static boolean addToInventory(IInventory inv, int slot,
	        ItemStack whatToAdd, boolean simulate) {
		ItemStack stackEx = inv.getStackInSlot(slot);
		if (stackEx == null) {
			if (!simulate)
				inv.setInventorySlotContents(slot, whatToAdd.copy());
			return true;
		} else {
			if (stackEx.itemID == whatToAdd.itemID
			        && stackEx.getItemDamage() == whatToAdd.getItemDamage()) {
				if (stackEx.stackSize + whatToAdd.stackSize <= inv
				        .getInventoryStackLimit()) {
					ItemStack nStack = stackEx.copy();
					nStack.stackSize += whatToAdd.stackSize;
					if (!simulate)
						inv.setInventorySlotContents(slot, nStack);
					return true;
				}
			} else
				return false;
		}
		return false;
	}

}
