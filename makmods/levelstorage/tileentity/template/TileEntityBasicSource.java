package makmods.levelstorage.tileentity.template;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LevelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

/**
 * 
 * @author mak326428
 * 
 */
public abstract class TileEntityBasicSource extends TileEntity implements
        IEnergyTile, IEnergySource, IWrenchable, IEnergyStorage {

	private boolean addedToENet = false;
	private int stored;
	public int maxOutput;
	public static final String NBT_STORED = "stored";
	
	public TileEntityBasicSource(int maxOutput) {
		this.maxOutput = maxOutput;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		stored = par1NBTTagCompound.getInteger(NBT_STORED);

	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger(NBT_STORED, stored);

	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
		        this.zCoord, 5, tagCompound);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		this.readFromNBT(pkt.data);
	}

	@Override
	public void setStored(int energy) {
		this.stored = energy;

	}

	// IWrenchable stuff

	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	public short getFacing() {
		return (short) ForgeDirection.UNKNOWN.flag;
	}

	public void setFacing(short facing) {
		;
		// Do nothing here
	}

	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	public float getWrenchDropRate() {
		return 0.5f;
	}

	// End of IWrenchable

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (LevelStorage.isSimulating())
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				addedToENet = true;
			}
	}

	private void unloadFromENet() {
		if (LevelStorage.isSimulating())
			if (addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				addedToENet = false;
			}
	}

	public abstract void onUnloaded();

	@Override
	public void invalidate() {
		onUnloaded();
		unloadFromENet();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unloadFromENet();
		super.onChunkUnload();
	}

	public boolean isAddedToEnergyNet() {
		return addedToENet;
	}

	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		return true;
	}

	@Override
	public int addEnergy(int amount) {

		this.stored += amount;
		return stored;
	}

	public abstract void onLoaded();

	public void validate() {
		super.validate();
		onLoaded();
	}

	public int getStored() {
		return this.stored;
	}

	public boolean isTeleporterCompatible(Direction side) {
		return false;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return false;
	}

	@Override
	public int getOutput() {
		return 0;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}
	
	@Override
	public double getOfferedEnergy() {
		return Math.min(maxOutput, stored);
	}

	@Override
	public void drawEnergy(double amount) {
		stored -= amount;
	}
}
