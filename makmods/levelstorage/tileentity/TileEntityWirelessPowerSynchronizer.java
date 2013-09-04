package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.registry.SyncType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements
        IHasTextBoxes, IHasButtons, IEnergyTile, IEnergySink, IWrenchable,
        IEnergySource, IEnergyStorage {

	public SyncType deviceType = SyncType.RECEIVER;
	public int frequency;

	public int internalBuffer;

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
	        ForgeDirection direction) {
		if (this.deviceType == SyncType.RECEIVER)
			return true;
		else
			return false;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		if (this.deviceType == SyncType.RECEIVER)
			return false;
		else
			return true;
	}

	@Override
	public double getOfferedEnergy() {
		if (this.deviceType == SyncType.RECEIVER)
			return Math.min(512, internalBuffer);
		else
			return 0;
	}

	@Override
	public void drawEnergy(double amount) {
		internalBuffer -= amount;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	public void updateEntity() {
		super.updateEntity();
		if (!LevelStorage.isSimulating())
			return;
		System.out.println(this.internalBuffer);
	}

	@Override
	public short getFacing() {
		return 0;
	}

	@Override
	public void setFacing(short facing) {

	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 0.75F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockWlessPowerSync);
	}

	@Override
	public double demandedEnergyUnits() {
		if (this.deviceType == SyncType.RECEIVER)
			return 0;
		else {
			if (this.internalBuffer > getCapacity())
				return 0;
			return Math.min(2048, getCapacity() - this.internalBuffer);
		}
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		if (this.deviceType == SyncType.RECEIVER)
			return amount;
		if ((this.getCapacity() - this.getStored()) > amount) {
			this.addEnergy((int) amount);
			return 0;
		} else {
			int leftover = (int) amount
			        - (this.getCapacity() - this.getStored());
			this.setStored(getCapacity());
			return leftover;
		}
	}

	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void handleButtonClick(int buttonId) {
		this.deviceType = deviceType.getInverse();
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			frequency = Integer.parseInt(newText);
		} catch (Exception e) {
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		this.deviceType = par1nbtTagCompound.getBoolean("isTransmitter") ? SyncType.TRANSMITTER
		        : SyncType.RECEIVER;
		this.frequency = par1nbtTagCompound.getInteger("frequency");
		this.internalBuffer = par1nbtTagCompound.getInteger("stored");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setBoolean("isTransmitter",
		        this.deviceType == SyncType.TRANSMITTER);
		par1nbtTagCompound.setInteger("frequency", frequency);
		par1nbtTagCompound.setInteger("stored", internalBuffer);
	}

	@Override
	public int getStored() {
		return internalBuffer;
	}

	@Override
	public void setStored(int energy) {
		internalBuffer = energy;
	}

	@Override
	public int addEnergy(int amount) {
		internalBuffer += amount;
		return internalBuffer;
	}

	@Override
	public int getCapacity() {
		// max packet size + 1
		return 2049;
	}

	@Override
	public int getOutput() {
		return 0;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return false;
	}

	public void load() {
		if (!LevelStorage.isSimulating())
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	public void unload() {
		if (!LevelStorage.isSimulating())
			return;
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public void invalidate() {
		unload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unload();
		super.invalidate();
	}

	public void validate() {
		super.validate();
		load();
	}

}
