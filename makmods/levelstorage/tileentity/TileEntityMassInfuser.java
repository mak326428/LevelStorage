package makmods.levelstorage.tileentity;

import makmods.levelstorage.logic.uumsystem.UUMHelper;
import makmods.levelstorage.logic.uumsystem.UUMRecipeParser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMassInfuser extends TileEntity implements IInventory,
		IHasButtons, ISidedInventory {
	// Phantom Slots + Crafting grid + Slot for UUM + Slot for crafting output
	public static final int INVENTORY_SIZE = 9 + 9 + 1 + 1;
	public static final String INVENTORY_NAME = "Mass Infuser";

	// DON'T DO ANYTHING (ANYTHING!) with first nine slots (they are phantom's,
	// using them would be bad)
	public ItemStack[] inv;

	public int page = 0;
	public static final int RECIPES_PER_PAGE = 9;
	public static final int MAX_PAGE = UUMRecipeParser.instance.recipes.size()
			/ RECIPES_PER_PAGE - 1;

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (page > MAX_PAGE)
				page = MAX_PAGE;
			if (page < 0)
				page = 0;
			int slot = 0;
			for (int i = RECIPES_PER_PAGE * page; i < RECIPES_PER_PAGE
					* (page + 1); i++) {
				this.setInventorySlotContents(slot,
						UUMRecipeParser.instance.recipes.get(i).getOutput()
								.copy());
				slot++;
			}
		}
	}

	public void handleRecipeSelection(int rec) {
		if (rec < 9) {
			ItemStack curr = inv[rec];
			for (int i = 9; i < 9 + 9; i++) {
				inv[i] = null;
			}
			if (curr != null) {
				ItemStack[] res = UUMHelper.getUUMRecipe(curr.copy());
				if (res != null) {
					for (int i = 0; i < res.length; i++) {
						int adder = 9;
						inv[adder + i] = res[i];
					}
				}
				inv[19] = curr.copy();
			}
		}
	}

	public void handlePageUpdate(int inc) {
		page += inc;
	}

	@Override
	public void handleButtonClick(int buttonId) {
		switch (buttonId) {
		case 1:
			handlePageUpdate(-1);
			break;
		case 2:
			handlePageUpdate(1);
			break;
		}

	}

	public TileEntityMassInfuser() {
		this.inv = new ItemStack[INVENTORY_SIZE];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null) {
			this.setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		// return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord,
		// this.zCoord) == this
		// && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5,
		// this.zCoord + 0.5) < 64;
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return INVENTORY_SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.inv[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return INVENTORY_NAME;
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
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
		return stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
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
		par1NBTTagCompound.setTag("Inventory", itemList);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

}
