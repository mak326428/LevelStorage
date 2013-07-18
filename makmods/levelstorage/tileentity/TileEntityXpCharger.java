package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.gui.SlotBook;
import makmods.levelstorage.item.ItemLevelStorageBook;
import makmods.levelstorage.registry.XpStackRegistry;
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
import net.minecraftforge.common.MinecraftForge;

public class TileEntityXpCharger extends TileEntity implements IEnergyTile,
		IEnergySink, IEnergyStorage, IInventory, IWrenchable, ISidedInventory {
	public static final int INTERNAL_CAPACITOR = 4096;
	public static final int INVENTORY_SIZE = 1;
	public static final String INVENTORY_NAME = "XP Charger";
	public boolean isWorking = true;
	public int storedEnergy = 0;
	private boolean addedToENet = false;
	public static final int MAX_PACKET_SIZE = 512;

	public int demandsEnergy() {
		return this.getCapacity() - this.getStored();
	}

	public int injectEnergy(Direction directionFrom, int amount) {
		if (amount > MAX_PACKET_SIZE) {
			this.invalidate();
			worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
			worldObj.createExplosion(null, (double) this.xCoord,
					(double) this.yCoord, (double) this.zCoord, 2F, false);
		}
		if ((this.getCapacity() - this.getStored()) > amount) {
			this.addEnergy(amount);
			return 0;
		} else {
			int leftover = amount - (this.getCapacity() - this.getStored());
			this.setStored(INTERNAL_CAPACITOR);
			return leftover;
		}
	}

	public int getMaxSafeInput() {
		return MAX_PACKET_SIZE;
	}

	public TileEntityXpCharger() {
		inv = new ItemStack[INVENTORY_SIZE];
	}

	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0 };
	}

	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return SlotBook.checkItemValidity(itemstack);
	}

	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	public boolean acceptsEnergyFrom(TileEntity te, Direction d) {
		return true;
	}

	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(ModBlocks.instance.blockXpCharger);
	}

	public short getFacing() {
		return 0;
	}

	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	public float getWrenchDropRate() {
		return 0.75F;
	}

	public void setFacing(short f) {
		return;
	}

	public boolean wrenchCanSetFacing(EntityPlayer p, int side) {
		return false;
	}

	ItemStack[] inv;

	public String getInvName() {
		return INVENTORY_NAME;
	}

	public boolean isInvNameLocalized() {
		return true;
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
		readFromNBT(packet.customParam1);
	}

	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return SlotBook.checkItemValidity(stack);
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public void onChunkUnload() {
		if (!worldObj.isRemote) {
			super.onChunkUnload();
			if (addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				this.addedToENet = false;
			}
		}
	}

	public void invalidate() {
		if (addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
		super.invalidate();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	public boolean isTeleporterCompatible(Direction d) {
		return false;
	}

	public void setStored(int newStValue) {
		this.storedEnergy = newStValue;
	}

	public int getStored() {
		return storedEnergy;
	}

	public int getCapacity() {
		return INTERNAL_CAPACITOR;
	}

	@Override
	public int getOutput() {
		return MAX_PACKET_SIZE;
	}

	public int addEnergy(int amount) {
		this.storedEnergy += amount;
		return getStored();
	}

	public boolean isAddedToEnergyNet() {
		return addedToENet;
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("storedEnergy", storedEnergy);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		par1NBTTagCompound.setTag("Inventory", itemList);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.storedEnergy = par1NBTTagCompound.getInteger("storedEnergy");

		NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToENet = true;
			}
			isWorking = (!worldObj.isBlockIndirectlyGettingPowered(xCoord,
					yCoord, zCoord) && worldObj.getBlockPowerInput(xCoord,
					yCoord, zCoord) < 1);
			if (isWorking) {
				if (inv[0] != null) {
					if (inv[0].getItem() instanceof ItemLevelStorageBook) {
						if (this.getStored() > XpStackRegistry.XP_EU_CONVERSION
								.getValue()) {
							if ((LevelStorage.itemLevelStorageBookSpace - ItemLevelStorageBook
									.getStoredXP(inv[0])) > XpStackRegistry.XP_EU_CONVERSION
									.getKey()) {
								this.addEnergy(-XpStackRegistry.XP_EU_CONVERSION
										.getValue());
								ItemLevelStorageBook.increaseStoredXP(inv[0],
										XpStackRegistry.XP_EU_CONVERSION
												.getKey());
							}
						}
						inv[0].setItemDamage(ItemLevelStorageBook
								.calculateDurability(inv[0]));
					}
				}
			}
		}
	}
}
