package makmods.levelstorage.tileentity;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;

import java.util.Arrays;
import java.util.List;

import makmods.levelstorage.gui.SlotFrequencyCard;
import makmods.levelstorage.item.ItemFrequencyCard;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.registry.WirelessConductorRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class TileEntityWirelessConductor extends TileEntity implements
		IWirelessConductor, IInventory, INetworkClientTileEntityEventListener,
		INetworkDataProvider, INetworkUpdateListener {

	public static final String INVENTORY_NAME = "Wireless Conductor";

	public ConductorType type;

	// This will be changed when a new valid (!!!) card is inserted.
	public IWirelessConductor pair = null;

	@Override
	public int receiveEnergy(int amount) {
		// What the heck are you asking me, i am source!
		if (this.type == ConductorType.SOURCE)
			return Integer.MAX_VALUE;
		return 0;
	}

	@Override
	public IWirelessConductor getPair() {
		return this.pair;
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) {
		switch (event) {
		case 1: {
			// Just simply invert our type
			type = type == ConductorType.SINK ? ConductorType.SOURCE : ConductorType.SINK;
			break;
		}
		}
	}

	@Override
	public int getDimId() {
		return this.worldObj.provider.dimensionId;
	}

	@Override
	public void onNetworkUpdate(String field) {
		// TODO Auto-generated method stub
	}

	private static List<String> fields = Arrays.asList(new String[0]);

	@Override
	public List<String> getNetworkedFields() {
		return fields;
	}

	@Override
	public int getX() {
		return this.xCoord;
	}

	@Override
	public int getY() {
		return this.yCoord;
	}

	@Override
	public int getZ() {
		return this.zCoord;
	}

	public TileEntityWirelessConductor() {
		this.type = ConductorType.SOURCE;
		this.inv = new ItemStack[1];
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
	
	public ConductorType getType() {
		return this.type;
	}
	
	public IWirelessConductor getSafePair() {
		ItemStack stack = inv[0];
		if (stack != null) {
			if (ItemFrequencyCard.isValid(stack)) {
				int x = stack.stackTagCompound.getInteger(ItemFrequencyCard.NBT_X_POS);
				int y = stack.stackTagCompound.getInteger(ItemFrequencyCard.NBT_Y_POS);
				int z = stack.stackTagCompound.getInteger(ItemFrequencyCard.NBT_Z_POS);
				int dimId = stack.stackTagCompound.getInteger(ItemFrequencyCard.NBT_DIM_ID);
				WorldServer world = DimensionManager.getWorld(dimId);
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (te != null) {
					if (te instanceof IWirelessConductor) {
						if (te != this) {
							IWirelessConductor conductor = (IWirelessConductor)te;
							ConductorType pairType = conductor.getType();
							if (this.getType() != pairType) {
								return conductor;
							}
							//ConductorType oppositePairType = pairType == ConductorType.SINK ? ConductorType.SOURCE : ConductorType.SINK;
							
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void updateEntity() {
		if (!WirelessConductorRegistry.instance.isAddedToRegistry(this)) {
			WirelessConductorRegistry.instance.addConductorToRegistry(this,
					this.type);
		}
		if (WirelessConductorRegistry.instance.getConductorType(this) != this.type) {
			WirelessConductorRegistry.instance
					.setConductorType(this, this.type);
		}
		
		IWirelessConductor safePair = getSafePair();
		
		if (safePair != null) {
			//System.out.println("Safe pair established");
		}
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
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord,
				this.zCoord) == this
				&& player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5,
						this.zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return SlotFrequencyCard.checkItemValidity(stack);
	}

	@Override
	public int getSizeInventory() {
		return this.inv.length;
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

	ItemStack[] inv;

	@Override
	public String getInvName() {
		return INVENTORY_NAME;
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
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

}
