package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.Items;
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
	public static final int INVENTORY_SIZE = 2;
	public static final String INVENTORY_NAME = "XP Charger";
	public static final int ENERGY_COST_MULTIPLIER = 10;
	public boolean isWorking = true;
	public int storedEnergy = 0;
	private boolean addedToENet = false;
	private int progress = 1;
	public static final int MAX_PACKET_SIZE = 512;

	public int uumPoints = 0;

	@Override
	public int demandsEnergy() {
		if (!LevelStorage.chargerOnlyUUM)
			return this.getCapacity() - this.getStored();
		else
			return 0;
	}

	public int getProgress() {
		return this.progress;
	}

	public void setProgress(int p) {
		this.progress = p;
	}

	public void syncUUMProgress() {
		float percent = ((this.uumPoints * 100.0f) / XpStackRegistry.UUM_XP_CONVERSION
				.getValue()) / 100;
		this.progress = (int) (36 * percent);
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {
		if (!LevelStorage.chargerOnlyUUM) {
			if (amount > MAX_PACKET_SIZE) {
				this.invalidate();
				this.worldObj.setBlockToAir(this.xCoord, this.yCoord,
						this.zCoord);
				this.worldObj.createExplosion(null, this.xCoord, this.yCoord,
						this.zCoord, 2F, false);
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
		return amount;
	}

	@Override
	public int getMaxSafeInput() {
		return MAX_PACKET_SIZE;
	}

	public TileEntityXpCharger() {
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
	public boolean acceptsEnergyFrom(TileEntity te, Direction d) {
		return true;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer p) {
		return new ItemStack(ModBlocks.instance.blockXpCharger);
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
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		this.readFromNBT(packet.customParam1);
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
	public void onChunkUnload() {
		if (!this.worldObj.isRemote) {
			super.onChunkUnload();
			if (!LevelStorage.chargerOnlyUUM) {
				if (this.addedToENet) {
					MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(
							this));
					this.addedToENet = false;
				}
			}
		}
	}

	@Override
	public void invalidate() {
		if (!LevelStorage.chargerOnlyUUM) {
			if (this.addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				this.addedToENet = false;
			}
		}
		super.invalidate();
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
	public boolean isTeleporterCompatible(Direction d) {
		return false;
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
		return MAX_PACKET_SIZE;
	}

	@Override
	public int addEnergy(int amount) {
		this.storedEnergy += amount;
		return this.getStored();
	}

	@Override
	public boolean isAddedToEnergyNet() {
		return this.addedToENet;
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("storedEnergy", this.storedEnergy);
		par1NBTTagCompound.setInteger("progress", this.progress);
		par1NBTTagCompound.setInteger("uumPoints", this.uumPoints);

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
		this.uumPoints = par1NBTTagCompound.getInteger("uumPoints");

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
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (!LevelStorage.chargerOnlyUUM) {
				if (!this.addedToENet) {
					MinecraftForge.EVENT_BUS
							.post(new EnergyTileLoadEvent(this));
					this.addedToENet = true;
				}
			}
			this.isWorking = (!this.worldObj.isBlockIndirectlyGettingPowered(
					this.xCoord, this.yCoord, this.zCoord) && this.worldObj
					.getBlockPowerInput(this.xCoord, this.yCoord, this.zCoord) < 1);
			if (this.isWorking) {

				if (!LevelStorage.chargerOnlyUUM) {
					if (this.inv[0] != null) {
						if (this.inv[0].getItem() instanceof ItemLevelStorageBook) {
							if (this.getStored() > XpStackRegistry.XP_EU_CONVERSION
									.getValue() * ENERGY_COST_MULTIPLIER) {
								if ((LevelStorage.itemLevelStorageBookSpace - ItemLevelStorageBook
										.getStoredXP(this.inv[0])) > XpStackRegistry.XP_EU_CONVERSION
										.getKey()) {
									this.addEnergy(-(XpStackRegistry.XP_EU_CONVERSION
											.getValue() * ENERGY_COST_MULTIPLIER));
									ItemLevelStorageBook.increaseStoredXP(
											this.inv[0],
											XpStackRegistry.XP_EU_CONVERSION
													.getKey());
								}
							}
							this.inv[0].setItemDamage(ItemLevelStorageBook
									.calculateDurability(this.inv[0]));
						}
					}
				}
				// } else {
				// WTF? It didn't work until i set == true. That is weird.
				if (LevelStorage.chargerOnlyUUM == true) {
					if (this.uumPoints <= 0) {
						if (this.inv[1] != null) {
							if (this.inv[1].stackSize >= XpStackRegistry.UUM_XP_CONVERSION
									.getKey()) {
								if (this.inv[1].getItem() == Items.getItem(
										"matter").getItem()) {
									this.decrStackSize(1,
											XpStackRegistry.UUM_XP_CONVERSION
													.getKey());
									this.uumPoints += XpStackRegistry.UUM_XP_CONVERSION
											.getValue();
								}
							}
						}
					}
					if (this.uumPoints > 0) {
						if (this.inv[0] != null) {
							if ((LevelStorage.itemLevelStorageBookSpace - ItemLevelStorageBook
									.getStoredXP(this.inv[0])) > 1) {
								ItemLevelStorageBook.increaseStoredXP(
										this.inv[0], 1);
								this.uumPoints--;
								this.inv[0].setItemDamage(ItemLevelStorageBook
										.calculateDurability(this.inv[0]));
							}
						}
						this.syncUUMProgress();
					}
					// }}
				}
			}
		}
	}
}