package makmods.levelstorage.gui.container;

import ic2.api.item.Items;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.SlotBookCharger;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerXpCharger extends Container {

	public static class SlotUUM extends Slot {
		public SlotUUM(IInventory par1IInventory, int par2, int par3, int par4) {
			super(par1IInventory, par2, par3, par4);
		}

		public static boolean checkItemValidity(ItemStack stack) {
			return (stack.getItem() == Items.getItem("matter").getItem());
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return checkItemValidity(stack);
		}
	}

	protected TileEntityXpCharger tileEntity;

	public ContainerXpCharger(InventoryPlayer inventoryPlayer,
	        TileEntityXpCharger te) {
		this.tileEntity = te;
		this.addSlotToContainer(new SlotBookCharger(this.tileEntity, 0, 80, 35));
		if (LevelStorage.chargerOnlyUUM) {
			this.addSlotToContainer(new SlotUUM(this.tileEntity, 1, 80, 15));
		}

		this.bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (LevelStorage.chargerOnlyUUM) {
			for (int i = 0; i < this.crafters.size(); i++) {
				ICrafting icrafting = (ICrafting) this.crafters.get(i);
				icrafting.sendProgressBarUpdate(this, 0,
				        this.tileEntity.getProgress());
			}
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (LevelStorage.chargerOnlyUUM) {
			switch (i) {
				case 0:
					this.tileEntity.setProgress(j);
					;
					break;
			}
		}
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

			if (slotIndex < TileEntityXpCharger.INVENTORY_SIZE) {
				if (!this.mergeItemStack(slotItemStack,
				        TileEntityXpCharger.INVENTORY_SIZE,
				        this.inventorySlots.size(), false))
					return null;
			} else {
				// WARNING: the following code is for this current case only.
				// this won't work for you
				// if (!(slotItemStack.getItem() instanceof
				// ItemLevelStorageBook))
				// return null;
				if (!SlotBookCharger.checkItemValidity(slotItemStack)
				        || !SlotUUM.checkItemValidity(slotItemStack))
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
