package makmods.levelstorage.tileentity.template;

import cpw.mods.fml.common.network.PacketDispatcher;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.network.packet.PacketReRender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityAdvanced extends TileEntity implements IEnergySource,
		IEnergyStorage, IEnergySink, IWrenchable, IInventory, IFluidHandler {

	public ItemStack[] inv;
	public int facing;
	public boolean addedToENet;

	public final boolean acceptsEnergy;
	public final boolean emitsEnergy;
	public final int maxStorage;
	public final int eut;
	public int stored;
	public final boolean energyNetTE;
	public final int tankVolume;
	public final boolean fluidTE;

	public FluidTank tank;

	public TileEntityAdvanced(int inventorySize, boolean energyNetTE,
			boolean acceptsEnergy, boolean emitsEnergy, int maxStorage,
			int eut, int tankVolume, boolean fluidTE) {
		super();
		this.inv = new ItemStack[inventorySize];
		this.acceptsEnergy = acceptsEnergy;
		this.emitsEnergy = emitsEnergy;
		this.maxStorage = maxStorage;
		this.eut = eut;
		this.energyNetTE = energyNetTE;
		this.tankVolume = tankVolume;
		this.fluidTE = fluidTE;
		tank = new FluidTank(tankVolume);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
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

	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote)
			return;

		if (oldFacing != facing) {
			PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
			PacketReRender.reRenderBlock(xCoord, yCoord, zCoord);
			oldFacing = facing;
		}
		if (energyNetTE)
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				addedToENet = true;
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
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
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
		par1NBTTagCompound.setInteger("facing", facing);
		par1NBTTagCompound.setInteger("stored", stored);

		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.tank.writeToNBT(fluidTankTag);
		par1NBTTagCompound.setTag("fluidTank", fluidTankTag);
	}

	@Override
	public void invalidate() {
		unloadFromENet();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unloadFromENet();
		super.onChunkUnload();
	}

	private void unloadFromENet() {
		if (energyNetTE)
			if (LevelStorage.isSimulating())
				if (addedToENet) {
					MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(
							this));
					addedToENet = false;
				}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
				this.zCoord, 5, tagCompound);
	}

	public int oldFacing;

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		this.readFromNBT(pkt.data);
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

		facing = par1NBTTagCompound.getInteger("facing");
		stored = par1NBTTagCompound.getInteger("stored");
		tank.readFromNBT(par1NBTTagCompound.getCompoundTag("fluidTank"));
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return emitsEnergy;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return acceptsEnergy;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	public FluidTank getFluidTank() {
		return tank;
	}

	public int gaugeLiquidScaled(int i) {
		if (getFluidTank().getFluidAmount() <= 0)
			return 0;

		return getFluidTank().getFluidAmount() * i
				/ getFluidTank().getCapacity();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (!fluidTE)
			return null;
		else
			return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return side != facing;
	}

	@Override
	public short getFacing() {
		return (short) facing;
	}

	@Override
	public void setFacing(short facing) {
		this.facing = facing;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 0.5F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(worldObj.getBlockId(xCoord, yCoord, zCoord), 1,
				worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	@Override
	public double demandedEnergyUnits() {
		return Math.min(eut, maxStorage - stored);
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		if (stored + amount > maxStorage)
			return amount;
		this.stored += (int) amount;
		return 0;
	}

	@Override
	public int getMaxSafeInput() {
		return eut;
	}

	@Override
	public int getStored() {
		return stored;
	}

	@Override
	public void setStored(int energy) {
		this.stored = energy;
	}

	@Override
	public int addEnergy(int amount) {
		this.stored += amount;
		return getStored();
	}

	@Override
	public int getCapacity() {
		return maxStorage;
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

	@Override
	public double getOfferedEnergy() {
		if (!emitsEnergy)
			return 0;
		else
			return Math.min(stored, eut);
	}

	@Override
	public void drawEnergy(double amount) {
		this.stored -= amount;
	}
}
