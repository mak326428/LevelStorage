package makmods.levelstorage.gui;

import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPowerSync extends Container {

	protected TileEntityWirelessPowerSynchronizer tileEntity;

	public ContainerPowerSync(InventoryPlayer inventoryPlayer,
	        TileEntityWirelessPowerSynchronizer te) {
		this.tileEntity = te;
		this.bindPlayerInventory(inventoryPlayer);
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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.frequency);
			icrafting.sendProgressBarUpdate(this, 1,
			        this.tileEntity.deviceType.ordinal());
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.frequency = j;
				break;
			case 1:
				this.tileEntity.deviceType = SyncType.values()[j];
				break;
		}
	}

	// We have no slots, so.. just to prevent Ex...
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer,
	        int slotIndex) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
