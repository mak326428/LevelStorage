package makmods.levelstorage.logic;

import makmods.levelstorage.item.IHasNBTInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;

public class NBTInventory implements IInventory {

	public int inventorySize;

	public static final int SIZE = 1;
	public static final String INVENTORY_NAME = "NBT Inventory";

	// PLAYER - ITEMSTACK SPECIFIC FIELDS
	public int dimId;
	public String playerName;
	public int itemStackIndex;

	// END OF SPECIFIC FIELDS

	public NBTInventory(int dimId, String playerName, int itemStack) {
		this.dimId = dimId;
		this.playerName = playerName;
		this.itemStackIndex = itemStack;
		this.inv = new ItemStack[SIZE];
		this.inventorySize = SIZE;
		this.readFromNBT();
	}

	public ItemStack[] inv;

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null) {
			this.setInventorySlotContents(slot, null);
		}
		this.saveToNBT();
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openChest() {
		this.readFromNBT();
	}

	@Override
	public void closeChest() {
		this.saveToNBT();

	}

	public void saveToNBT() {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < this.inv.length; i++) {
			ItemStack stack = this.inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		try {
			// YES, it is a mess and pretty slow code,
			// but without it nothing will work. Period.
			for (Object ep : DimensionManager.getWorld(this.dimId).playerEntities) {
				if (((EntityPlayer) ep).username == this.playerName) {
					if (((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex] != null) {
						if (((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex]
								.getItem() instanceof IHasNBTInventory) {
							((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex].stackTagCompound
									.setTag("Inventory", itemList);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInventoryChanged() {
		this.saveToNBT();
		// System.out.println(boundTagCompound);
	}

	public void readFromNBT() {
		// System.out.println("readFromNBT");
		NBTTagList tagList = null;
		try {
			// YES, it is a mess and pretty slow code,
			// but without it nothing will work. Period.
			for (Object ep : DimensionManager.getWorld(this.dimId).playerEntities) {
				if (((EntityPlayer) ep).username == this.playerName) {
					if (((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex] != null) {
						if (((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex]
								.getItem() instanceof IHasNBTInventory) {
							tagList = ((EntityPlayer) ep).inventory.mainInventory[this.itemStackIndex].stackTagCompound
									.getTagList("Inventory");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		// System.out.println(tagList);
		// System.out.println(boundTagCompound);
	}

	@Override
	public int getSizeInventory() {
		return this.inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		this.readFromNBT();
		return this.inv[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				this.setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					this.setInventorySlotContents(slot, null);
				}
			}
		}
		this.saveToNBT();
		return stack;
	}

	@Override
	public String getInvName() {
		return INVENTORY_NAME;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.inv[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
		this.saveToNBT();
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return true;
	}

}
