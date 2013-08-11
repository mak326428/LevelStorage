package makmods.levelstorage.gui;

import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMassInfuser extends Container {
	protected TileEntityMassInfuser tileEntity;

	public ContainerMassInfuser(InventoryPlayer inventoryPlayer,
			TileEntityMassInfuser te) {
		this.tileEntity = te;
		this.addSlotToContainer(new Slot(this.tileEntity, 0, 80, 35));
		this.bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(inventoryPlayer,
						j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18,
					142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
			int slotIndex) {

		ItemStack itemStack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack slotItemStack = slot.getStack();
			itemStack = slotItemStack.copy();

			if (slotIndex < TileEntityXpGenerator.INVENTORY_SIZE) {
				// from the generator
				if (!this.mergeItemStack(slotItemStack,
						TileEntityXpGenerator.INVENTORY_SIZE,
						this.inventorySlots.size(), false))
					return null;
			} else {
				// into the generator
				// WARNING: the following code is for this current case only.
				// this won't work for you
				// if (!(slotItemStack.getItem() instanceof
				// ItemLevelStorageBook))
				// return null;
				//if (!SlotBook.checkItemValidity(slotItemStack))
				//	return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
						TileEntityXpGenerator.INVENTORY_SIZE, false))
					return null;
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
