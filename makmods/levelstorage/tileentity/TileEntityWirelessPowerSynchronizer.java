package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IWrenchable;

import java.util.ArrayList;

import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.logic.BlockLocation;
import makmods.levelstorage.logic.Helper;
import makmods.levelstorage.registry.IWirelessPowerSync;
import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements
		IHasTextBoxes, IHasButtons, IEnergyTile, IEnergySink, IWrenchable,
		IEnergySource, IWirelessPowerSync {

	public int frequency;
	public SyncType type;
	public static final int MAX_PACKET_SIZE = 2048;
	public boolean addedToENet = false;
	public static final String NBT_FREQUENCY = "freq";
	public static final String NBT_MODE = "mode";

	public int timeForUpdate = 0;

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

	@Override
	public World getWorld() {
		return this.worldObj;
	}

	@Override
	public int getFreq() {
		return this.frequency;
	}

	@Override
	public SyncType getType() {
		return this.type;
	}

	@Override
	public void onChunkUnload() {
		this.unloadEverything();
		super.onChunkUnload();
	}

	public void loadEverything() {
		if (!WirelessPowerSynchronizerRegistry.instance.isDeviceAdded(this)) {
			WirelessPowerSynchronizerRegistry.instance.addDevice(this);
		}
		if (!this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToENet = true;
		}
	}

	public void unloadEverything() {
		WirelessPowerSynchronizerRegistry.instance.removeDevice(this);
		if (this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
	}

	@Override
	public void invalidate() {
		this.unloadEverything();
		super.invalidate();
	}

	@Override
	public int receiveEnergy(int amount) {
		if (this.type == SyncType.TRANSMITTER)
			return amount;
		else {
			EnergyTileSourceEvent sourceEvent = new EnergyTileSourceEvent(this,
					amount);
			MinecraftForge.EVENT_BUS.post(sourceEvent);
			return sourceEvent.amount;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.frequency = par1NBTTagCompound.getInteger(NBT_FREQUENCY);
		this.type = SyncType.values()[par1NBTTagCompound.getInteger(NBT_MODE)];
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger(NBT_FREQUENCY, this.frequency);
		par1NBTTagCompound.setInteger(NBT_MODE, this.type.ordinal());

	}

	@Override
	public boolean doesNeedEnergy() {
		return this.getType() == SyncType.RECEIVER;
	}

	public IWirelessPowerSync[] pairs;

	public TileEntityWirelessPowerSynchronizer() {
		this.type = SyncType.RECEIVER;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(ModBlocks.instance.blockWlessPowerSync);
	}

	@Override
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
		return this.addedToENet;
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
	public int demandsEnergy() {
		if (this.type == SyncType.TRANSMITTER) {
			if (this.pairs != null) {
				if (this.pairs.length > 0) {
					for (IWirelessPowerSync ent : this.pairs) {
						if (ent.doesNeedEnergy())
							return MAX_PACKET_SIZE;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public void updateState() {
		this.pairs = WirelessPowerSynchronizerRegistry.instance
				.getDevicesForFreqAndType(this.frequency,
						Helper.invertType(this.type));
	}

	public int lastX;
	public int lastY;
	public int lastZ;

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			// WirelessPowerSynchronizerRegistry.instance.registry.clear();
			if (this.lastX != this.xCoord || this.lastY != this.yCoord
					|| this.lastZ != this.zCoord) {
				this.unloadEverything();
				this.loadEverything();
			}
			this.loadEverything();
			// timeForUpdate++;
			// if (timeForUpdate > 40) {
			this.updateState();
			// }
			this.lastX = this.xCoord;
			this.lastY = this.yCoord;
			this.lastZ = this.zCoord;

		}
	}

	public int sendEnergyToDevices(ArrayList<IWirelessPowerSync> devs,
			int amount) {
		int unused = 0;
		if (devs.size() > 0) {
			int forEach;
			if (devs.size() != 0)
				forEach = amount / devs.size();
			else
				forEach = amount;
			for (IWirelessPowerSync s : devs) {
				BlockLocation thisTe = new BlockLocation(
						this.getWorld().provider.dimensionId, this.getX(),
						this.getY(), this.getZ());
				BlockLocation pairTe = new BlockLocation(
						s.getWorld().provider.dimensionId, s.getX(), s.getY(),
						s.getZ());
				unused += s
						.receiveEnergy(forEach -= BlockLocation
								.getEnergyDiscount(forEach,
										thisTe.getDistance(pairTe)));
			}
			return unused;
		}
		return amount;
	}

	public int sendEnergyEqually(int amount) {
		if (this.pairs.length > 0) {
			int energyNotUsed = 0;

			int forEach = amount;
			if (this.pairs.length > 0) {
				forEach = amount / this.pairs.length;
			}

			for (IWirelessPowerSync s : this.pairs) {
				BlockLocation thisTe = new BlockLocation(
						this.getWorld().provider.dimensionId, this.getX(),
						this.getY(), this.getZ());
				BlockLocation pairTe = new BlockLocation(
						s.getWorld().provider.dimensionId, s.getX(), s.getY(),
						s.getZ());
				int forEachWithDisc = forEach
						- BlockLocation.getEnergyDiscount(forEach,
								thisTe.getDistance(pairTe));
				int leftover = s.receiveEnergy(forEachWithDisc);
				if (leftover == forEachWithDisc) {
					ArrayList<IWirelessPowerSync> par5 = new ArrayList<IWirelessPowerSync>();
					for (IWirelessPowerSync par6 : pairs) {
						if (par6 != s)
							par5.add(par6);
					}
					int par7 = sendEnergyToDevices(par5, leftover);
					energyNotUsed += par7;
				} else {
					energyNotUsed += leftover;
				}
			}
			return energyNotUsed;
		}
		return amount;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {

		if (this.type == SyncType.TRANSMITTER)
			return this.sendEnergyEqually(amount);
		return amount;
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			this.frequency = Integer.parseInt(newText);
			for (IWirelessPowerSync entry : WirelessPowerSynchronizerRegistry.instance.registry) {
				entry.updateState();
			}
		} catch (NumberFormatException e) {
		}
	}

	@Override
	public void handleButtonClick(int buttonId) {
		if (buttonId == 1) {
			this.type = Helper.invertType(this.type);
			for (IWirelessPowerSync entry : WirelessPowerSynchronizerRegistry.instance.registry) {
				entry.updateState();
			}
		}
	}

}
