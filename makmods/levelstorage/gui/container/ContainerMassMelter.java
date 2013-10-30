package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ContainerMassMelter extends Container {
	protected TileEntityMassMelter tileEntity;

	public ContainerMassMelter(InventoryPlayer inventoryPlayer,
			TileEntityMassMelter te) {
		this.tileEntity = te;
		// this.addSlotToContainer(new SlotBook(this.tileEntity, 0, 80, 35));
		// this.addSlotToContainer(new SlotChecked(this.tileEntity, 0, 129, 9));
		// this.addSlotToContainer(new SlotChecked(this.tileEntity, 1, 129,
		// 53));
		this.addBurnSlots();
		this.bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	public void addBurnSlots() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// System.out.println(j + i * 3);
				this.addSlotToContainer(new SlotChecked(tileEntity, j + i * 3,
						63 + j * 18, 9 + i * 18));
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

	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			icrafting.sendProgressBarUpdate(this, 4,
					this.tileEntity.getStored());
			icrafting.sendProgressBarUpdate(this, 5, this.tileEntity
					.getFluidTank().getFluidAmount());
			icrafting.sendProgressBarUpdate(this, 6,
					this.tileEntity.getProgress());
			icrafting.sendProgressBarUpdate(this, 7,
					tileEntity.getMaxProgress());
		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 4: {
			tileEntity.setStored(value);
			break;
		}
		case 5: {
			tileEntity.getFluidTank().setFluid(
					new FluidStack(LSFluids.instance.fluidIV, value));
			break;
		}
		case 6: {
			tileEntity.setProgress(value);
			break;
		}
		case 7: {
			tileEntity.maxProgress = value;
			break;
		}
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
			} else {
				if (IVRegistry.hasValue(slotItemStack)) {
					if (!this.mergeItemStack(slotItemStack, 0,
							tileEntity.getSizeInventory(), false)) {
						return null;
					}
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
