package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.gui.SlotTakeOnly;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.GUIHelper;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRockDesintegrator extends Container {

	public TileEntityRockDesintegrator tileEntity;

	public ContainerRockDesintegrator(InventoryPlayer inventoryPlayer,
			TileEntityRockDesintegrator te) {
		this.tileEntity = te;
		this.bindMachineInventory(te);
		this.bindPlayerInventory(inventoryPlayer);
	}

	protected void bindMachineInventory(TileEntityRockDesintegrator te) {
		final int baseX = 53;
		final int baseY = 9;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlotToContainer(new SlotChecked(te, j + i * 4, baseX
						+ j * 18, baseY + i * 18));
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			icrafting.sendProgressBarUpdate(this, 4,
					this.tileEntity.getStored());

		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 4: {
			tileEntity.setStored(value);
			break;
		}
		}
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
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack slotItemStack = slot.getStack();
			itemStack = slotItemStack.copy();
			// if slotIndex is less than massmelter's
			// inventory size, then it is shiftclicking FROM massmelter's
			// inventory
			// into player's inventory, otherwise = reverse
			if (slotIndex < tileEntity.getSizeInventory()) {
				if (!this.mergeItemStack(slotItemStack,
						tileEntity.getSizeInventory(), inventorySlots.size(),
						false)) {
					return null;
				}
			}

			if (slotItemStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		// return itemStack;
		return null;
	}

}
