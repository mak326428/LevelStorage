package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;
import ic2.api.tile.IWrenchable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

// Used source from Advanced Solar Panels
public class TileEntityMulticoreSolarPanel extends TileEntity implements
        IEnergyTile, IWrenchable, IEnergySource, IInventory,
        INetworkClientTileEntityEventListener, INetworkDataProvider,
        INetworkUpdateListener {
	public static Random randomizer = new Random();
	public int ticker;
	public int generating;
	public int genDay;
	public int genNight;
	public boolean initialized;
	public boolean sunIsUp;
	public boolean skyIsVisible;
	private short facing;
	private boolean noSunWorld;
	private boolean wetBiome;
	public boolean addedToEnergyNet;
	private boolean created;
	private ItemStack[] chargeSlots;
	public int fuel;
	private int lastX;
	private int lastY;
	private int lastZ;
	public int storage;
	private int solarType;
	public String panelName;
	public int production;
	public int maxStorage;
	public boolean loaded = false;

	public TileEntityMulticoreSolarPanel() {
		created = false;
		this.facing = 2;
		genDay = 2048;
		genNight = 256;
		// super(1);
		storage = 0;
		panelName = "multicoreSolarPanel";
		sunIsUp = false;
		skyIsVisible = false;
		maxStorage = 20 * 1000 * 1000;
		chargeSlots = new ItemStack[4];
		initialized = false;
		production = 2048;
		ticker = randomizer.nextInt(tickRate());
		lastX = this.xCoord;
		lastY = this.yCoord;
		lastZ = this.zCoord;
	}

	@Override
	public void validate() {
		super.validate();
		onLoaded();
	}

	@Override
	public void invalidate() {
		if (this.loaded) {
			this.onUnloaded();
		}

		super.invalidate();
	}

	public void onLoaded() {
		if (worldObj.isRemote) {
			NetworkHelper.requestInitialData(this);
		} else {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}

		this.addedToEnergyNet = true;
		this.loaded = true;
	}

	@Override
	public void onChunkUnload() {
		if (this.loaded) {
			this.onUnloaded();
		}

		super.onChunkUnload();
	}

	public void onUnloaded() {
		if (this.addedToEnergyNet) {
			// System.out.println("ON UNLOADED");
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			// EnergyNet.getForWorld(this.worldObj).removeTileEntity(this);
			this.addedToEnergyNet = false;
		}

		this.loaded = false;
	}

	public void intialize() {
		wetBiome = worldObj.getWorldChunkManager()
		        .getBiomeGenAt(xCoord, zCoord).getIntRainfall() > 0;
		noSunWorld = worldObj.provider.hasNoSky;
		updateVisibility();
		initialized = true;

		if (!this.addedToEnergyNet) {
			onLoaded();
		}
	}

	public boolean canUpdate() {
		return true;
	}

	public void updateEntity() {
		super.updateEntity();

		if (!initialized) {
			intialize();
		}

		if ((lastX != this.xCoord) || (lastZ != this.zCoord)
		        || (lastY != this.yCoord)) {
			lastX = this.xCoord;
			lastY = this.yCoord;
			lastZ = this.zCoord;

			// System.out.println("POS CHANGED");

			onUnloaded();
			intialize();
		}

		gainFuel();

		if (generating > 0) {
			if ((storage + generating) <= maxStorage) {
				storage += generating;
			} else {
				storage = maxStorage;
			}
		}

		boolean needInvUpdate = false;
		int sentPacket = 0;

		for (int i = 0; i < chargeSlots.length; i++) {
			if ((chargeSlots[i] != null)
			        && (Item.itemsList[chargeSlots[i].itemID] instanceof IElectricItem)
			        && (storage > 0)) {
				sentPacket = ElectricItem.manager.charge(chargeSlots[i],
				        storage, solarType, false, false);

				if (sentPacket > 0) {
					needInvUpdate = true;
				}

				storage -= sentPacket;
				// this.onInventoryChanged()
			}
		}

		if (needInvUpdate) {
			this.onInventoryChanged();
		}

		// if ((storage - production) >= 0) {
		// storage -= (production - sendEnergy(production));
		// }
	}

	public int gainFuel() {
		if (ticker++ % tickRate() == 0) {
			updateVisibility();
		}

		if (sunIsUp && skyIsVisible) {
			generating = 0 + genDay;
			return generating;
		}

		if (skyIsVisible) {
			generating = 0 + genNight;
			return generating;
		} else {
			generating = 0;
			return generating;
		}
	}

	public void updateVisibility() {
		Boolean rainWeather = wetBiome
		        && (worldObj.isRaining() || worldObj.isThundering());

		if ((!worldObj.isDaytime()) || (rainWeather)) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}

		if (!worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)
		        || (noSunWorld)) {
			skyIsVisible = false;
		} else {
			skyIsVisible = true;
		}
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		storage = nbttagcompound.getInteger("storage");
		lastX = nbttagcompound.getInteger("lastX");
		lastY = nbttagcompound.getInteger("lastY");
		lastZ = nbttagcompound.getInteger("lastZ");

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		chargeSlots = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist
			        .tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;

			if (j >= 0 && j < chargeSlots.length) {
				chargeSlots[j] = ItemStack
				        .loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();
		nbttagcompound.setInteger("storage", storage);
		nbttagcompound.setInteger("lastX", lastX);
		nbttagcompound.setInteger("lastY", lastY);
		nbttagcompound.setInteger("lastZ", lastZ);

		for (int i = 0; i < chargeSlots.length; i++) {
			if (chargeSlots[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				chargeSlots[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return true;
	}

	public int getMaxEnergyOutput() {
		return this.production;
	}

	public int gaugeEnergyScaled(int i) {
		return (storage * i) / maxStorage;
	}

	public int gaugeFuelScaled(int i) {
		return i;
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}

		return entityplayer.getDistanceSq((double) xCoord + 0.5D,
		        (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
	}

	public int tickRate() {
		return 128;
	}

	public short getFacing() {
		return this.facing;
	}

	public void setFacing(short facing) {
		this.facing = facing;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityplayer, int i) {
		return false;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(this.worldObj.getBlockId(this.xCoord, this.yCoord,
		        this.zCoord), 1, this.worldObj.getBlockMetadata(this.xCoord,
		        this.yCoord, this.zCoord));
	}

	public ItemStack[] getContents() {
		return chargeSlots;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return chargeSlots[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (chargeSlots[i] != null) {
			if (chargeSlots[i].stackSize <= j) {
				ItemStack itemstack = chargeSlots[i];
				chargeSlots[i] = null;
				onInventoryChanged();
				return itemstack;
			}

			ItemStack itemstack1 = chargeSlots[i].splitStack(j);

			if (chargeSlots[i].stackSize == 0) {
				chargeSlots[i] = null;
			}

			onInventoryChanged();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		chargeSlots[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		onInventoryChanged();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	public String getInvName() {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		if (this.chargeSlots[var1] != null) {
			ItemStack var2 = this.chargeSlots[var1];
			this.chargeSlots[var1] = null;
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void onNetworkUpdate(String field) {
	}

	private static List<String> fields = Arrays.asList(new String[0]);

	@Override
	public List<String> getNetworkedFields() {
		return fields;
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) {
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min(2048, this.storage);
	}

	@Override
	public void drawEnergy(double amount) {
		storage -= amount;
	}
}
