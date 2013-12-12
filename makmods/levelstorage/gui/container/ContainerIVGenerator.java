package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.gui.SlotFrequencyCard;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.tileentity.TileEntityIVGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ContainerIVGenerator extends Container {
	protected TileEntityIVGenerator tileEntity;

	public ContainerIVGenerator(InventoryPlayer inventoryPlayer,
			TileEntityIVGenerator te) {
		this.tileEntity = te;
		this.addSlotToContainer(new SlotChecked(te, 0, 150, 11));
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

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			icrafting.sendProgressBarUpdate(this, 4,
					this.tileEntity.latestSpeed);
			icrafting.sendProgressBarUpdate(this, 5, this.tileEntity
					.getFluidTank().getFluidAmount());
		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 4: {
			tileEntity.latestSpeed = value;
			break;
		}
		case 5: {
			tileEntity.getFluidTank().setFluid(
					new FluidStack(LSFluids.instance.fluidIV, value));
			break;
		}
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

			if (slotIndex < 1) {
				if (!this.mergeItemStack(slotItemStack,
				        1,
				        this.inventorySlots.size(), false))
					return null;
			} else {
				if (!tileEntity.isItemValidForSlot(0, slotItemStack))
					return null;
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
				        1, false))
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
