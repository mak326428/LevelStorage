package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.SlotFrequencyCard;
import makmods.levelstorage.item.ItemFrequencyCard;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.registry.IWirelessConductor;
import makmods.levelstorage.registry.WirelessConductorRegistry;
import makmods.levelstorage.tileentity.template.IHasButtons;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

public class TileEntityWirelessConductor extends TileEntity implements
		IWirelessConductor, IInventory, IEnergyTile, IEnergySink, IWrenchable,
		IEnergySource, IHasButtons {

	public static final String INVENTORY_NAME = "Wireless Conductor";
	public static final int MAX_PACKET_SIZE = 2048;
	public static final String NBT_TYPE = "type";
	public boolean addedToENet = false;
	public ConductorType type;
	public static boolean ENABLE_PARTICLES;
	public static boolean ENABLE_LIGHTNINGS;

	public static void getConfig() {
		Property p = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL, "conductorsSpawnLightnings",
				true);
		p.comment = "If set to false, wireless conductors will stop spawning lightnings";
		ENABLE_LIGHTNINGS = p.getBoolean(true);
		Property p2 = LevelStorage.configuration
				.get(Configuration.CATEGORY_GENERAL, "conducorsSpawnParticles",
						true);
		p2.comment = "If set to false, conductors will stop spawning particles (useful on servers, because every 40 ticks server will send 180 packets to all the clients)";
		ENABLE_PARTICLES = p2.getBoolean(true);
	}

	public boolean canReceive(int amount) {
		if (this.energyToSend + amount <= MAX_ENERGY_INTERNAL)
			return true;
		return false;
	}

	// This will be changed when a new valid (!!!) card is inserted.
	public IWirelessConductor pair = null;

	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(LSBlockItemList.blockWlessConductor);
	}

	@Override
	public void handleButtonClick(int id) {
		switch (id) {
		case 1: {
			// Just simply invert our type
			this.type = this.type == ConductorType.SINK ? ConductorType.SOURCE
					: ConductorType.SINK;
			break;
		}
		}
	}

	@Override
	public void setFacing(short f) {

	}

	@Override
	public float getWrenchDropRate() {
		return 0.75F;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity te, ForgeDirection dir) {
		return true;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer p) {
		return true;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity te, ForgeDirection dir) {
		return true;
	}

	@Override
	public int getMaxSafeInput() {
		return MAX_PACKET_SIZE;
	}

	@Override
	public short getFacing() {
		return (short) ForgeDirection.NORTH.ordinal();
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer p, int s) {
		return false;
	}

	@Override
	public double demandedEnergyUnits() {
		if (this.type == ConductorType.SOURCE) {
			if (this.safePair != null) {
				if (this.safePair.canReceive(MAX_PACKET_SIZE))
					return MAX_PACKET_SIZE;
			}
		}
		return 0;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		if (this.type == ConductorType.SOURCE) {
			if (this.safePair != null) {
				BlockLocation thisTe = new BlockLocation(this.getDimId(),
						this.getX(), this.getY(), this.getZ());
				BlockLocation pairTe = new BlockLocation(
						this.safePair.getDimId(), this.safePair.getX(),
						this.safePair.getY(), this.safePair.getZ());
				int amtWithDisc = (int) amount
						- BlockLocation.getEnergyDiscount((int) amount,
								thisTe.getDistance(pairTe));
				return this.safePair.receiveEnergy(amtWithDisc, this);
			}
		}
		return amount;
	}

	public int elapsedReceives = 0;
	int energyToSend = 0;
	public static final int MAX_ENERGY_INTERNAL = 2048;

	@Override
	public int receiveEnergy(int amount, IWirelessConductor transmitter) {

		if (this.type == ConductorType.SOURCE)
			return amount;
		else {
			this.elapsedReceives++;
			if (this.elapsedReceives > 600 && amount > 128) {

				if (ENABLE_LIGHTNINGS) {
					CommonHelper.spawnLightning(this.worldObj, this.xCoord,
							this.yCoord, this.zCoord, false);
				}
				this.elapsedReceives = 0;
			}
			// EntityPlayer ep = this.worldObj.getClosestPlayer(this.xCoord,
			// this.yCoord, this.zCoord, 8);
			// if (ep != null) {
			// ep.attackEntityFrom(LSDamageSource.energyField, 1);
			// }
			if (energyToSend + amount > MAX_ENERGY_INTERNAL)
				return MAX_ENERGY_INTERNAL - energyToSend;
			energyToSend += amount;
			return MAX_ENERGY_INTERNAL - energyToSend;
		}
	}

	public double getOfferedEnergy() {
		return energyToSend;
	}

	public void drawEnergy(double amount) {
		energyToSend -= amount;
	}

	@Override
	public IWirelessConductor getPair() {
		return this.pair;
	}

	@Override
	public int getDimId() {
		return this.worldObj.provider.dimensionId;
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
		if (this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
		super.onChunkUnload();
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

	@Override
	public ConductorType getType() {
		return this.type;
	}

	public IWirelessConductor getSafePair() {
		ItemStack stack = this.inv[0];
		if (stack != null) {
			if (ItemFrequencyCard.isValid(stack)) {
				BlockLocation devLocation = BlockLocation
						.readFromNBT(stack.stackTagCompound);
				WorldServer world = DimensionManager.getWorld(devLocation
						.getDimId());
				TileEntity te = world.getBlockTileEntity(devLocation.getX(),
						devLocation.getY(), devLocation.getZ());
				if (te != null) {
					if (te instanceof IWirelessConductor) {
						if (te != this) {
							IWirelessConductor conductor = (IWirelessConductor) te;
							if (WirelessConductorRegistry.instance
									.isAddedToRegistry((IWirelessConductor) te)) {
								ConductorType pairType = conductor.getType();
								if (this.getType() != pairType)
									return conductor;
							}
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
		this.particleTime++;
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

		this.safePair = this.getSafePair();

		if (this.safePair != null) {
			// System.out.println("Safe pair established");
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.type = par1NBTTagCompound.getInteger(NBT_TYPE) == 0 ? ConductorType.SINK
				: ConductorType.SOURCE;
		this.energyToSend = par1NBTTagCompound.getInteger("toSend");
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
		par1NBTTagCompound.setInteger(NBT_TYPE,
				(this.type == ConductorType.SINK ? 0 : 1));
		par1NBTTagCompound.setInteger("toSend", energyToSend);
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

	public int particleTime = 0;

	@Override
	public String getInvName() {
		return INVENTORY_NAME;
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}
}
