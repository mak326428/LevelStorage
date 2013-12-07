package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.gui.SlotBook;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.item.ItemXPTome;
import makmods.levelstorage.registry.XPStackRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityXpGenerator extends TileEntity implements IEnergyTile,
		IEnergySource, IEnergyStorage, IInventory, IWrenchable, ISidedInventory {
	public static final int INTERNAL_CAPACITOR = 65536;
	public static final int INVENTORY_SIZE = 1;
	public static final String INVENTORY_NAME = "XP Generator";
	public boolean isWorking = true;
	public int storedEnergy = 0;
	private boolean addedToENet = false;
	public static final int PACKET_SIZE = 512;

	public int progress;
	public int internalEnergyBuffer;

	public TileEntityXpGenerator() {
		this.inv = new ItemStack[INVENTORY_SIZE];
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return SlotBook.checkItemValidity(itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(LSBlockItemList.blockXpGen);
	}

	@Override
	public short getFacing() {
		return 0;
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
	public void setFacing(short f) {
		return;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer p, int side) {
		return false;
	}

	// This is going to be a special inventory
	// It won't be ItemStack[] inv = new ItemStack[9], it will contain only one
	// stack
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
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return SlotBook.checkItemValidity(stack);
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
	public void setStored(int newStValue) {
		this.storedEnergy = newStValue;
	}

	@Override
	public int getStored() {
		return this.storedEnergy;
	}

	@Override
	public int getCapacity() {
		return INTERNAL_CAPACITOR;
	}

	@Override
	public int getOutput() {
		return PACKET_SIZE;
	}

	@Override
	public int addEnergy(int amount) {
		this.storedEnergy += amount;
		return this.getStored();
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("storedEnergy", this.storedEnergy);
		par1NBTTagCompound.setInteger("progress", this.progress);
		par1NBTTagCompound.setInteger("currentItemBuffer",
				this.internalEnergyBuffer);
		par1NBTTagCompound.setInteger("maxProgress", this.maxProgress);
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
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.storedEnergy = par1NBTTagCompound.getInteger("storedEnergy");
		this.progress = par1NBTTagCompound.getInteger("progress");
		this.internalEnergyBuffer = par1NBTTagCompound
				.getInteger("currentItemBuffer");
		this.maxProgress = par1NBTTagCompound.getInteger("maxProgress");
		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public LogicSlot input = new LogicSlot(this, 0);
	public int maxProgress = 0;

	public void compensateInternalBuffer() {
		if (input.get() == null)
			return;
		ItemStack stack = input.get();
		if (stack.getItem() instanceof ItemXPTome) {
			// Book inside
			if (ItemXPTome.getStoredXP(this.inv[0]) > XPStackRegistry.XP_EU_CONVERSION
					.getKey()) {
				this.internalEnergyBuffer += XPStackRegistry.XP_EU_CONVERSION
						.getValue();
				ItemXPTome.increaseStoredXP(this.inv[0],
						-XPStackRegistry.XP_EU_CONVERSION.getKey());
				maxProgress = 1 * XPStackRegistry.XP_EU_CONVERSION
						.getValue();
			}
			this.inv[0].setItemDamage(ItemXPTome
					.calculateDurability(this.inv[0]));
		} else {
			// Item inside
			int value = XPStackRegistry.instance.getStackValue(stack);
			if (value == 0)
				return;
			this.internalEnergyBuffer = XPStackRegistry.XP_EU_CONVERSION
					.getValue() * value;
			maxProgress = XPStackRegistry.XP_EU_CONVERSION.getValue() * value;
			input.consume(1);
		}
	}
	
	public static final int MAX_BUFFER = 10000;

	public void generateEnergyFromInternalBuffer() {
		// TODO: add fixed EU/t, etc. Would be quite easy to do that way
		if ((this.storedEnergy + this.internalEnergyBuffer) <= MAX_BUFFER) {
			this.storedEnergy += this.internalEnergyBuffer;
			this.internalEnergyBuffer = 0;
		}
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (!this.addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToENet = true;
			}
			this.isWorking = (!this.worldObj.isBlockIndirectlyGettingPowered(
					this.xCoord, this.yCoord, this.zCoord) && this.worldObj
					.getBlockPowerInput(this.xCoord, this.yCoord, this.zCoord) < 1);
			if (this.isWorking) {
				if (this.internalEnergyBuffer == 0)
					compensateInternalBuffer();
				if (this.internalEnergyBuffer > 0)
					generateEnergyFromInternalBuffer();
			}
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		this.readFromNBT(packet.data);
	}

	@Override
	public void onChunkUnload() {
		if (!this.worldObj.isRemote) {
			super.onChunkUnload();
			if (this.addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				this.addedToENet = false;
			}
		}
	}

	@Override
	public void invalidate() {
		if (this.addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
		super.invalidate();
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
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
		return Math.min(this.storedEnergy, PACKET_SIZE);
	}

	@Override
	public void drawEnergy(double amount) {
		this.storedEnergy -= amount;
	}
}
