package makmods.levelstorage.gui;

import ic2.api.item.IElectricItem;
import makmods.levelstorage.logic.NBTInventory;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;

public class ContainerWirelessCharger extends Container {
	
	public class SlotItemCharger extends Slot {
		public SlotItemCharger(IInventory par1IInventory, int par2, int par3, int par4) {
			super(par1IInventory, par2, par3, par4);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof IElectricItem;
		}
	}
	
	public NBTInventory inventory;
	

	public ContainerWirelessCharger(int dimId, String playerName, int itemStack) {
		this.inventory = new NBTInventory(dimId, playerName, itemStack);
		this.addSlotToContainer(new SlotFrequencyCard(this.inventory, 0, 80,
				35));
		bindPlayerInventory(DimensionManager.getWorld(dimId).getPlayerEntityByName(playerName).inventory);
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

			if (slotIndex < NBTInventory.SIZE) {
				if (!this.mergeItemStack(slotItemStack,
						NBTInventory.SIZE,
						this.inventorySlots.size(), false))
					return null;
			} else {
				// WARNING: the following code is for this current case only.
				// this won't work for you
				if (slotItemStack != null) {
					if (!(slotItemStack.getItem() instanceof IElectricItem)) {
						return null;
					}
				}
				// End of warning
				if (!this.mergeItemStack(slotItemStack, 0,
						NBTInventory.SIZE, false))
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

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
