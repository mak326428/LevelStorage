package makmods.levelstorage.logic;

import makmods.levelstorage.gui.SlotBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTInventory implements IInventory {

	public ItemStack boundStack;
	public int inventorySize;
	
	public int dimId;
	public String plUsername;
	public int slotId;

	public static final String INVENTORY_NAME = "NBT Inventory";

	public NBTInventory(ItemStack boundStack, int size, int dimId, String plUsername, int slotId) {
		this.boundStack = boundStack;
		this.inv = new ItemStack[size];
		this.inventorySize = size;
		this.dimId = dimId;
		this.plUsername = plUsername;
		this.slotId = slotId;
		readFromNBT();
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
		
		this.boundStack.stackTagCompound.setTag("Inventory", itemList);
		
	}

	public void onInventoryChanged() {
		saveToNBT();
		//System.out.println(boundTagCompound);
	}

	public void readFromNBT() {
		//System.out.println("readFromNBT");
		NBTTagList tagList = this.boundStack.stackTagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		//System.out.println(tagList);
		//System.out.println(boundTagCompound);
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
