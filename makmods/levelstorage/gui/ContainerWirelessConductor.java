package makmods.levelstorage.gui;

import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWirelessConductor extends Container {

	protected TileEntityWirelessConductor tileEntity;

	public ContainerWirelessConductor(InventoryPlayer inventoryPlayer,
	        TileEntityWirelessConductor te) {
		this.tileEntity = te;
		this.addSlotToContainer(new SlotFrequencyCard(this.tileEntity, 0, 80,
		        35));
		this.bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			int mode = this.tileEntity.type == ConductorType.SINK ? 0 : 1;
			icrafting.sendProgressBarUpdate(this, 0, mode);
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (i == 0) {
			ConductorType type = j == 0 ? ConductorType.SINK
			        : ConductorType.SOURCE;
			this.tileEntity.type = type;
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
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack slotItemStack = slot.getStack();
			itemStack = slotItemStack.copy();

			if (slotIndex < TileEntityXpCharger.INVENTORY_SIZE) {
				if (!this.mergeItemStack(slotItemStack,
				        TileEntityXpCharger.INVENTORY_SIZE,
				        this.inventorySlots.size(), false))
					return null;
			} else {
				// WARNING: the following code is for this current case only.
				// this won't work for you
				if (!SlotFrequencyCard.checkItemValidity(slotItemStack))
					return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
				        TileEntityXpCharger.INVENTORY_SIZE, false))
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
