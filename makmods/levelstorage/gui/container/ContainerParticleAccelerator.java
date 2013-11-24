package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.gui.container.phantom.ContainerWithPhantomSlots;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ContainerParticleAccelerator extends ContainerWithPhantomSlots {

	protected TileEntityParticleAccelerator tileEntity;
	private short lastProgress = -1;

	public ContainerParticleAccelerator(InventoryPlayer inventoryPlayer,
			TileEntityParticleAccelerator te) {
		this.tileEntity = te;
		// this.addSlotToContainer(new SlotChecked(te, 0, 80, 15));
		this.addSlotToContainer(new SlotChecked(te, 1, 125, 34));
		// this.addSlotToContainer(new SlotChecked(te, 2, 125, 42));
		this.addSlotToContainer(new IVSampleSlot(tileEntity.phantomInventory,
				0, 41, 34));
		this.bindPlayerInventory(inventoryPlayer);
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
			icrafting.sendProgressBarUpdate(this, 5, tileEntity.mode);
			icrafting.sendProgressBarUpdate(this, 6, tileEntity.getProgress());
			icrafting.sendProgressBarUpdate(this, 7, tileEntity.maxProgress);
			icrafting.sendProgressBarUpdate(this, 8, tileEntity.getFluidTank()
					.getFluidAmount());
		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 4:
			tileEntity.setStored(value);
			break;
		case 5:
			tileEntity.mode = value;
			break;
		case 6:
			tileEntity.setProgress(value);
			break;
		case 7: {
			tileEntity.maxProgress = value;
			break;
		}
		case 8: {
			tileEntity.getFluidTank().setFluid(
					new FluidStack(LSFluids.instance.fluidIV, value));
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
