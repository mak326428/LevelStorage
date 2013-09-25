package makmods.levelstorage.gui.logicslot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import makmods.levelstorage.gui.logicslot.LogicSlot;

public class HelperLogicSlot implements LogicSlot {

	private final IInventory boundInventory;
	private final int index;

	public HelperLogicSlot(IInventory inv, int index) {
		this.boundInventory = inv;
		this.index = index;
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return this.boundInventory.isItemValidForSlot(index, stack);
	}

	@Override
	public boolean isItemTheSame(ItemStack stack) {
		return stack == null ? false : stack.itemID == get().itemID
				&& stack.getItemDamage() == get().getItemDamage();
	}

	@Override
	public boolean canConsume(int amount) {
		if (get() == null)
			return false;
		ItemStack st = get();
		return st.stackSize >= amount;
	}

	@Override
	public boolean canAdd(int amount) {
		if (get() == null && amount <= boundInventory.getInventoryStackLimit())
			return true;
		ItemStack alreadyThere = get();
		if (boundInventory.getInventoryStackLimit() - alreadyThere.stackSize >= amount)
			return true;
		return false;
	}

	@Override
	public IInventory getInventory() {
		return boundInventory;
	}

	@Override
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

	@Override
	public void consume(int amount) {
		add(-amount);
	}

	@Override
	public ItemStack get() {
		return boundInventory.getStackInSlot(index);
	}

	@Override
	public boolean add(ItemStack what, boolean simulate) {
		if (get() != null) {
			if (get().getItemDamage() != what.getItemDamage()
					|| get().itemID != what.itemID) {
				return false;
			} else {
				int freeSpace = boundInventory.getInventoryStackLimit()
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

}
