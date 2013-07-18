package makmods.levelstorage.gui;

import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXpCharger extends Container {
	protected TileEntityXpCharger tileEntity;

	public ContainerXpCharger(InventoryPlayer inventoryPlayer,
			TileEntityXpCharger te) {
		tileEntity = te;
		addSlotToContainer(new SlotBookCharger(tileEntity, 0, 80, 35));
		bindPlayerInventory(inventoryPlayer);
	}

	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
			int slotIndex) {

		ItemStack itemStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack slotItemStack = slot.getStack();
			itemStack = slotItemStack.copy();
			
			if (slotIndex < TileEntityXpCharger.INVENTORY_SIZE) {
				if (!this.mergeItemStack(slotItemStack,
						TileEntityXpCharger.INVENTORY_SIZE,
						inventorySlots.size(), false)) {
					return null;
				}
			} else {
				// WARNING: the following code is for this current case only. this won't work for you
				//if (!(slotItemStack.getItem() instanceof ItemLevelStorageBook))
				//	return null;
				if (!SlotBookCharger.checkItemValidity(slotItemStack))
					return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
						TileEntityXpCharger.INVENTORY_SIZE, false)) {
					return null;
				}
			}

			if (slotItemStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemStack;
	}
}
