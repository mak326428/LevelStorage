package makmods.levelstorage.gui.container;

import makmods.levelstorage.gui.SlotChecked;
import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ContainerLavaFabricator extends Container {
	protected TileEntityLavaFabricator tileEntity;

	public ContainerLavaFabricator(InventoryPlayer inventoryPlayer,
			TileEntityLavaFabricator te) {
		this.tileEntity = te;
		// this.addSlotToContainer(new SlotBook(this.tileEntity, 0, 80, 35));
		this.addSlotToContainer(new SlotChecked(this.tileEntity, 0, 129, 9));
		this.addSlotToContainer(new SlotChecked(this.tileEntity, 1, 129, 53));
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
					this.tileEntity.getStored());
			icrafting.sendProgressBarUpdate(this, 5, this.tileEntity
					.getFluidTank().getFluidAmount());
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
					new FluidStack(FluidRegistry.LAVA, value));
			break;
		}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
			int slotIndex) {
		return null;
	}
}
