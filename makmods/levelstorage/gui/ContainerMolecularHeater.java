package makmods.levelstorage.gui;

import java.util.ListIterator;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.util.GUIHelper;
import makmods.levelstorage.tileentity.TileEntityMolecularHeater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMolecularHeater extends Container {

	protected TileEntityMolecularHeater tileEntity;
	private short lastProgress = -1;
	public int energy = -1;
	public int progress = -1;

	public ContainerMolecularHeater(InventoryPlayer inventoryPlayer,
	        TileEntityMolecularHeater te) {
		this.tileEntity = te;
		// Input slots.
		this.addSlotToContainer(new SlotChecked(te, 0, 28, 17));
		this.addSlotToContainer(new SlotChecked(te, 1, 46, 17));
		this.addSlotToContainer(new SlotChecked(te, 2, 65, 17));
		this.addSlotToContainer(new SlotChecked(te, 3, 84, 17));
		// Output slots
		this.addSlotToContainer(new SlotTakeOnly(te, 4, 105, 35));
		this.addSlotToContainer(new SlotTakeOnly(te, 5, 124, 35));
		this.addSlotToContainer(new SlotTakeOnly(te, 6, 142, 35));
		this.addSlotToContainer(new SlotTakeOnly(te, 7, 161, 35));
		// Discharge slot
		this.addSlotToContainer(new SlotChecked(te, 8, 56, 53));

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
			icrafting.sendProgressBarUpdate(this, 5,
			        this.tileEntity.getProgress());

		}
	}

	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
			case 4: {
				energy = value;
				tileEntity.setStored(value);
				break;
			}
			case 5: {
				progress = value;
				tileEntity.setProgress(value);
				break;
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

	public  ItemStack transferStackInSlot(EntityPlayer ep, int slotIndex) {
		return GUIHelper.shiftClickSlot(this, ep, slotIndex);
	}

}
