package makmods.levelstorage.gui;

import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMassInfuser extends Container {
	protected TileEntityMassInfuser tileEntity;

	public ContainerMassInfuser(InventoryPlayer inventoryPlayer,
	        TileEntityMassInfuser te) {
		this.tileEntity = te;
		// this.addSlotToContainer(new Slot(this.tileEntity, 0, 80, 35));
		// UUM page
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotUntouchable(te, i, 19 + i * 18, 18));
		}
		// Crafting
		for (int l = 0; l < 3; l++) {
			for (int i1 = 0; i1 < 3; i1++) {
				this.addSlotToContainer(new SlotUntouchable(tileEntity, 9 + i1
				        + l * 3, 17 + i1 * 18, 85 + l * 18));
			}
		}
		this.addSlotToContainer(new SlotUntouchable(tileEntity, 19, 111, 103));
		// Player inventory
		bindPlayerInventory(inventoryPlayer);
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
			icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.page);
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.page = j;
				break;
		}
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(inventoryPlayer,
				        j + i * 9 + 9, 17 + j * 18, 174 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 17 + i * 18,
			        232));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
	        int slotIndex) {
		return null;
	}
}
