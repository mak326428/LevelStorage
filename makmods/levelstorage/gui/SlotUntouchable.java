package makmods.levelstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUntouchable extends Slot {

	public SlotUntouchable(IInventory contents, int id, int x, int y) {
		super(contents, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return false;
	}

	public boolean canAdjust() {
		return false;
	}

	public boolean canShift() {
		return false;
	}
}