package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;

import java.util.Arrays;
import java.util.List;

import makmods.levelstorage.ModBlocks;
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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityWirelessConductor extends TileEntity implements
		IWirelessConductor, IInventory, INetworkClientTileEntityEventListener,
		INetworkDataProvider, INetworkUpdateListener, IEnergyTile, IEnergySink,
		IWrenchable, IEnergySource {

	public static final String INVENTORY_NAME = "Wireless Conductor";
	public static final int MAX_PACKET_SIZE = 2048;
	public boolean addedToENet = false;
	public ConductorType type;

	// This will be changed when a new valid (!!!) card is inserted.
	public IWirelessConductor pair = null;
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(ModBlocks.instance.blockWlessConductor);
	}
	
	public int getMaxEnergyOutput() {
		return MAX_PACKET_SIZE;
	}
	
	@Override
	public void setFacing(short f) {
		
	}
	
	@Override
	public float getWrenchDropRate() {
		return 0.75F;
	}
	
	@Override
	public boolean emitsEnergyTo(TileEntity te, Direction dir) {
		return true;
	}
	
	@Override
	public boolean wrenchCanRemove(EntityPlayer p) {
		return true;
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity te, Direction dir) {
		return true;
	}
	
	@Override
	public int getMaxSafeInput() {
		return MAX_PACKET_SIZE;
	}
	
	@Override
	public boolean isAddedToEnergyNet() {
		return addedToENet;
	}
	
	@Override
	public short getFacing() {
		return (short)ForgeDirection.NORTH.ordinal();
	}
	
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer p, int s) {
		return false;
	}
	
	@Override
	public int demandsEnergy() {
		if (this.type == ConductorType.SOURCE) {
			if (safePair != null) {
				return MAX_PACKET_SIZE;
			}
		}
		return 0;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {
		if (this.type == ConductorType.SOURCE) {
			if (safePair != null) {
				return safePair.receiveEnergy(amount);
			}
		}
		return amount;
	}

	@Override
	public int receiveEnergy(int amount) {
		// What the heck are you asking me, i am source!
		if (this.type == ConductorType.SOURCE)
			return amount;
		else {
			EnergyTileSourceEvent sourceEvent = new EnergyTileSourceEvent(this,
					amount);
			MinecraftForge.EVENT_BUS.post(sourceEvent);
			return sourceEvent.amount;
		}
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
			type = type == ConductorType.SINK ? ConductorType.SOURCE
					: ConductorType.SINK;
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
		super.onChunkUnload();
		WirelessConductorRegistry.instance.removeFromRegistry(this);
		if (this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}

	}

	@Override
	public void invalidate() {
		WirelessConductorRegistry.instance.removeFromRegistry(this);
		if (this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
		super.invalidate();
	}

	public ConductorType getType() {
		return this.type;
	}

	public IWirelessConductor getSafePair() {
		ItemStack stack = inv[0];
		if (stack != null) {
			if (ItemFrequencyCard.isValid(stack)) {
				int x = stack.stackTagCompound
						.getInteger(ItemFrequencyCard.NBT_X_POS);
				int y = stack.stackTagCompound
						.getInteger(ItemFrequencyCard.NBT_Y_POS);
				int z = stack.stackTagCompound
						.getInteger(ItemFrequencyCard.NBT_Z_POS);
				int dimId = stack.stackTagCompound
						.getInteger(ItemFrequencyCard.NBT_DIM_ID);
				WorldServer world = DimensionManager.getWorld(dimId);
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (te != null) {
					if (te instanceof IWirelessConductor) {
						if (te != this) {
							IWirelessConductor conductor = (IWirelessConductor) te;
							ConductorType pairType = conductor.getType();
							if (this.getType() != pairType) {
								return conductor;
							}
							// ConductorType oppositePairType = pairType ==
							// ConductorType.SINK ? ConductorType.SOURCE :
							// ConductorType.SINK;

						}
					}
				}
			}
		}
		return null;
	}

	public IWirelessConductor safePair;

	@Override
	public void updateEntity() {
		if (!this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToENet = true;
		}
		if (!WirelessConductorRegistry.instance.isAddedToRegistry(this)) {
			WirelessConductorRegistry.instance.addConductorToRegistry(this,
					this.type);
		}
		if (WirelessConductorRegistry.instance.getConductorType(this) != this.type) {
			WirelessConductorRegistry.instance
					.setConductorType(this, this.type);
		}

		safePair = getSafePair();

		if (safePair != null) {
			// System.out.println("Safe pair established");
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
