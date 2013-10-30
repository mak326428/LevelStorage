package makmods.levelstorage.gui.container.phantom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWithPhantomSlots extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int modifier,
			EntityPlayer player) {
		Slot slot = slotNum < 0 ? null : (Slot) this.inventorySlots
				.get(slotNum);
		if (slot instanceof PhantomSlot) {
			return slotClickPhantom(slot, mouseButton, modifier, player);
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	private ItemStack slotClickPhantom(Slot slot, int mouseButton,
			int modifier, EntityPlayer player) {
		ItemStack stack = null;

		if (mouseButton == 2) {
			slot.putStack(null);

		} else if (mouseButton == 0 || mouseButton == 1) {
			InventoryPlayer playerInv = player.inventory;
			slot.onSlotChanged();
			ItemStack stackSlot = slot.getStack();
			ItemStack stackHeld = playerInv.getItemStack();

			if (stackSlot != null) {
				stack = stackSlot.copy();
			}

			if (stackSlot == null) {
				if (stackHeld != null) {
					fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
				}
			} else if (stackHeld == null) {
				// adjustPhantomSlot(slot, mouseButton, modifier);
				slot.putStack(null);
				slot.onPickupFromSlot(player, playerInv.getItemStack());
			}
		}
		return stack;
	}

	protected void fillPhantomSlot(Slot slot, ItemStack stackHeld,
			int mouseButton, int modifier) {
		if (!slot.isItemValid(stackHeld))
			return;
		if (!(slot instanceof PhantomSlot))
			return;
		int stackSize;
		if (((PhantomSlot) slot).isUnstackable())
			stackSize = 1;
		else
			stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}
		ItemStack phantomStack = stackHeld.copy();
		phantomStack.stackSize = stackSize;

		slot.putStack(phantomStack);
	}
}
