package makmods.levelstorage.tileentity;

import makmods.levelstorage.gui.SlotFrequencyCard;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.registry.WirelessConductorRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWirelessConductor extends TileEntity implements
		IWirelessConductor, IInventory {

	public static final String INVENTORY_NAME = "Wireless Conductor";
	
	public ConductorType type;

	// This will be changed when a new valid (!!!) card is inserted.
	public IWirelessConductor pair = null;

	public int receiveEnergy(int amount) {
		// What the heck are you asking me, i am source!
		if (this.type == ConductorType.SOURCE)
			return Integer.MAX_VALUE;
		return 0;
	}

	public IWirelessConductor getPair() {
		return this.pair;
	}

	public int getDimId() {
		return this.worldObj.provider.dimensionId;
	}

	public int getX() {
		return this.xCoord;
	}

	public int getY() {
		return this.yCoord;
	}

	public int getZ() {
		return this.zCoord;
	}

	public TileEntityWirelessConductor() {
		this.type = ConductorType.SOURCE;
	}

	@Override
	public void onChunkUnload() {
		WirelessConductorRegistry.instance.removeFromRegistry(this);
	}

	@Override
	public void invalidate() {
		WirelessConductorRegistry.instance.removeFromRegistry(this);
		super.invalidate();
	}

	@Override
	public void updateEntity() {
		if (!WirelessConductorRegistry.instance.isAddedToRegistry(this))
			WirelessConductorRegistry.instance.addConductorToRegistry(this,
					type);
		if (WirelessConductorRegistry.instance.getConductorType(this) != type)
			WirelessConductorRegistry.instance.setConductorType(this, type);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);

		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return SlotFrequencyCard.checkItemValidity(stack);
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	ItemStack[] inv;

	public String getInvName() {
		return INVENTORY_NAME;
	}

	public boolean isInvNameLocalized() {
		return true;
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		par1NBTTagCompound.setTag("Inventory", itemList);
	}

}
