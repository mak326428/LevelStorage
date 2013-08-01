package makmods.levelstorage.gui;

import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import makmods.levelstorage.item.ItemWirelessCharger;
import makmods.levelstorage.logic.NBTInventory;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWirelessCharger extends Container {
	
	public static class SlotCharging extends Slot {
		public SlotCharging(IInventory par1IInventory, int par2, int par3, int par4) {
			super(par1IInventory, par2, par3, par4);
		}

		public static boolean checkItemValidity(ItemStack stack) {
			return stack.getItem() instanceof IElectricItem;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return checkItemValidity(stack);
		}
	}

	protected NBTInventory inv;
	protected ItemStack callingStack;

	public ContainerWirelessCharger(InventoryPlayer inventoryPlayer,
			ItemStack callingStack) {
		this.inv = new NBTInventory(callingStack, ItemWirelessCharger.NBT_INVENTORY_SIZE);
		this.addSlotToContainer(new SlotCharging(inv, 0, 80,
				35));
		this.bindPlayerInventory(inventoryPlayer);
		this.callingStack = callingStack;
		inv.openChest();
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}
	
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
		inv.closeChest();
		super.onContainerClosed(par1EntityPlayer);
    }

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		//for (int i = 0; i < this.crafters.size(); i++) {
		//	ICrafting icrafting = (ICrafting) this.crafters.get(i);
		//	int mode = this.tileEntity.type == ConductorType.SINK ? 0 : 1;
		//	icrafting.sendProgressBarUpdate(this, 0, mode);
		//}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		super.updateProgressBar(i, j);
		//if (i == 0) {
		//	ConductorType type = j == 0 ? ConductorType.SINK
		///			: ConductorType.SOURCE;
		//	this.tileEntity.type = type;
		//}
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

			if (slotIndex < inv.inventorySize) {
				if (!this.mergeItemStack(slotItemStack,
						inv.inventorySize,
						this.inventorySlots.size(), false))
					return null;
			} else {
				// WARNING: the following code is for this current case only.
				// this won't work for you
				if (!(slotItemStack.getItem() instanceof IElectricItem))
					return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
						inv.inventorySize, false))
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
