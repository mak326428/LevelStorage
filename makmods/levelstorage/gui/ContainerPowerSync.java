package makmods.levelstorage.gui;

import makmods.levelstorage.registry.ConductorType;
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

		for (int i = 0; i < crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, tileEntity.frequency);
		}
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		if (i == 0) {
			tileEntity.frequency = j;
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
