package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.logic.Helper;
import makmods.levelstorage.registry.IWirelessPowerSync;
import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	
	public int getX() {
		return this.xCoord;
	}
	
	public int getY() {
		return this.yCoord;
	}
	
	public int getZ() {
		return this.zCoord;
	}
	
	public World getWorld() {
		return this.worldObj;
	}
	
	public int getFreq() {
		return this.frequency;
	}
	
	public SyncType getType() {
		return this.type;
	}
	
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
	
	public boolean doesNeedEnergy() {
		return this.getType() == SyncType.RECEIVER;
	}

	public IWirelessPowerSync[] pairs;

	public TileEntityWirelessPowerSynchronizer() {
		type = SyncType.RECEIVER;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(ModBlocks.instance.blockWlessPowerSync);
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
					for (IWirelessPowerSync ent : pairs) {
						if (ent.doesNeedEnergy()) {
							return MAX_PACKET_SIZE;
						}
					}
				}
			}
		}
		return 0;
	}

	public void updateState() {
		pairs = WirelessPowerSynchronizerRegistry.instance
				.getDevicesForFreqAndType(this.frequency,
						Helper.invertType(type));
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (!WirelessPowerSynchronizerRegistry.instance.isDeviceAdded(this))
				WirelessPowerSynchronizerRegistry.instance.addDevice(this);
			if (!this.addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToENet = true;
			}
		}
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {
		
		if (this.type == SyncType.TRANSMITTER) {
			int unusedAmount = amount;
			
			int energyForEach;
			if (pairs.length > 0) 
				energyForEach = unusedAmount / pairs.length;
			else
				energyForEach = unusedAmount / 1;
			
			for (IWirelessPowerSync s : pairs) {
				unusedAmount -= energyForEach - s.receiveEnergy(energyForEach);
			}
			
			return unusedAmount;
		}
		return amount;
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			frequency = Integer.parseInt(newText);
			for (IWirelessPowerSync entry : WirelessPowerSynchronizerRegistry.instance.registry) {
				entry.updateState();
			}
		} catch (NumberFormatException e) {
		}
	}

	@Override
	public void handleButtonClick(int buttonId) {

		if (buttonId == 1) {
			for (IWirelessPowerSync entry : WirelessPowerSynchronizerRegistry.instance.registry) {
				entry.updateState();
			}
			this.type = Helper.invertType(this.type);
		}

	}

}
