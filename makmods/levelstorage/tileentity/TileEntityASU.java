package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.client.GUIASU;
import makmods.levelstorage.gui.container.ContainerASU;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.network.packet.PacketReRender;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityASU extends TileEntityInventory implements IWrenchable,
		IEnergySource, IEnergySink, IEnergyStorage, ITEHasGUI {

	public static final long EU_STORAGE = 2000000000L;
	public static final int EU_PER_TICK = 8192;
	public static final int TIER = 5;
	public LogicSlot chargeSlot;
	public LogicSlot dischargeSlot;
	public int oldFacing;
	private boolean addedToENet;

	public TileEntityASU() {
		super(2);
		chargeSlot = new LogicSlot(this, 0);
		dischargeSlot = new LogicSlot(this, 1);
	}

	public int facing;
	public long stored;

	@Override
	public String getInvName() {
		return "ASU";
	}

	public void charge(LogicSlot ls, boolean charge) {
		if (ls.get() == null)
			return;
		if (!(ls.get().getItem() instanceof IElectricItem))
			return;
		IElectricItem electricItem = (IElectricItem) ls.get().getItem();
		if (charge) {
			this.stored -= ElectricItem.manager.charge(ls.get(),
					(int) Math.min(Integer.MAX_VALUE, stored), TIER, false,
					false);
		} else {
			if (!electricItem.canProvideEnergy(ls.get()))
				return;
			this.stored += ElectricItem.manager.discharge(ls.get(),
					(int) Math.min(Integer.MAX_VALUE, EU_STORAGE - stored),
					TIER, false, false);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote)
			return;
		charge(chargeSlot, true);
		charge(dischargeSlot, false);
		if (oldFacing != facing) {
			PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
			PacketReRender.reRenderBlock(xCoord, yCoord, zCoord);
			oldFacing = facing;
		}
		if (!addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToENet = true;
		}
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
	public void invalidate() {
		unloadFromENet();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unloadFromENet();
		super.onChunkUnload();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.getItem() instanceof IElectricItem;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return facing != side;
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
		return 0.25F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockASU);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("facing", facing);
		par1NBTTagCompound.setLong("stored", stored);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		facing = par1NBTTagCompound.getInteger("facing");
		stored = par1NBTTagCompound.getLong("stored");
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return direction.ordinal() == facing;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return direction.ordinal() != facing;
	}

	@Override
	public int getStored() {
		return (int) Math.min(this.stored, (long) Integer.MAX_VALUE);
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
		return Integer.MAX_VALUE;
	}

	@Override
	public int getOutput() {
		return EU_PER_TICK;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return EU_PER_TICK;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return side.ordinal() == facing;
	}

	private void unloadFromENet() {
		if (LevelStorage.isSimulating())
			if (addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				addedToENet = false;
			}
	}

	@Override
	public double demandedEnergyUnits() {
		return EU_STORAGE - stored;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		if (this.stored > EU_STORAGE)
			return amount;
		this.stored += (long) amount;
		return 0;
	}

	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min((long) EU_PER_TICK, stored);
	}

	@Override
	public void drawEnergy(double amount) {
		stored -= amount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIASU(player.inventory, this);
	}

	public float getChargeLevel() {
		float ret = (float) stored / EU_STORAGE;
		if (ret > 1.0F)
			ret = 1.0F;

		return ret;
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerASU(player.inventory, this);
	}

}
