package makmods.levelstorage.gui.logicslot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class LogicSlot {

	private final IInventory boundInventory;
	private final int index;

	public LogicSlot(IInventory inv, int index) {
		this.boundInventory = inv;
		this.index = index;
	}

	public boolean isValid(ItemStack stack) {
		return this.boundInventory.isItemValidForSlot(index, stack);
	}

	public boolean isItemTheSame(ItemStack stack) {
		return stack == null ? false : stack.itemID == get().itemID
				&& stack.getItemDamage() == get().getItemDamage();
	}

	public boolean canConsume(int amount) {
		if (get() == null)
			return false;
		ItemStack st = get();
		return st.stackSize >= amount;
	}

	public boolean canAdd(int amount) {
		if (get() == null && amount <= boundInventory.getInventoryStackLimit())
			return true;
		ItemStack alreadyThere = get();
		if (boundInventory.getInventoryStackLimit() - alreadyThere.stackSize >= amount)
			return true;
		return false;
	}

	public IInventory getInventory() {
		return boundInventory;
	}

	public void add(int amount) {
		if (get() == null)
			throw new NullPointerException(
					"HelperLogicSlot: add(): get() returns null");
		ItemStack alreadyInThere = get().copy();
		alreadyInThere.stackSize += amount;
		if (alreadyInThere.stackSize <= 0) {
			this.boundInventory.setInventorySlotContents(index, null);
			return;
		}
		this.boundInventory.setInventorySlotContents(index, alreadyInThere);
	}

	public void consume(int amount) {
		add(-amount);
	}

	public ItemStack get() {
		return boundInventory.getStackInSlot(index);
	}

	public boolean add(ItemStack what, boolean simulate) {
		if (get() != null) {
			if (get().getItemDamage() != what.getItemDamage()
					|| get().itemID != what.itemID) {
				return false;
			} else {
				int freeSpace = what.getItem().getItemStackLimit()
						- get().stackSize;
				if (freeSpace >= what.stackSize) {
					if (!simulate) {
						ItemStack newIS = get().copy();
						newIS.stackSize += what.stackSize;
						boundInventory.setInventorySlotContents(index,
								newIS.copy());
						return true;
					}
					return true;
				} else
					return false;
			}
		} else {
			if (!simulate)
				boundInventory.setInventorySlotContents(index, what.copy());
			return true;
		}
	}
	
	public void set(ItemStack is) {
		boundInventory.setInventorySlotContents(index, is);
	}

}
