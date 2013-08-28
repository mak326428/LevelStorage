package makmods.levelstorage.gui;

import makmods.levelstorage.tileentity.TileEntityAdvancedMiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAdvancedMiner extends Container {

	protected TileEntityAdvancedMiner tileEntity;
	private short lastProgress = -1;
	public int energy = -1;

	public ContainerAdvancedMiner(InventoryPlayer inventoryPlayer,
	        TileEntityAdvancedMiner te) {
		this.tileEntity = te;
		this.addSlotToContainer(new SlotChecked(te, 0, 45, 22));
		this.addSlotToContainer(new SlotChecked(te, 1, 81, 22));
		this.addSlotToContainer(new SlotChecked(te, 2, 117, 22));
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

		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
			case 4:
				energy = value;
				break;
		}
	}

	public int gaugeEnergyScaled(int i) {
		if (energy <= 0) {
			return 0;
		}
		int r = energy * i / 16384;
		if (r > i) {
			r = i;
		}
		return r;
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
		return null;
	}
}
