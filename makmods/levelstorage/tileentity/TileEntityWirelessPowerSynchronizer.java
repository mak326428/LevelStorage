package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;

import java.util.EnumSet;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.item.ItemWirelessCharger;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer.WChargerRegistry.WChargerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements
		IHasTextBoxes, IHasButtons, IEnergyTile, IEnergySink, IWrenchable,
		IEnergySource, IEnergyStorage {

	public static final int MAX_PACKET_SIZE = 2048;

	// TODO: fix chargeradding code. The current solution is really weak and
	// unreliable
	public static class WChargerRegistry {

		public static class WChargerEntry {
			private EntityPlayer player;
			private int frequency;
			private ItemStack wirelessCharger;

			public WChargerEntry(EntityPlayer player, ItemStack charger,
					int freq) {
				this.player = player;
				this.wirelessCharger = charger;
				this.frequency = freq;
			}

			public EntityPlayer getPlayer() {
				return player;
			}

			public void setPlayer(EntityPlayer player) {
				this.player = player;
			}

			public int getFrequency() {
				return frequency;
			}

			public void setFrequency(int frequency) {
				this.frequency = frequency;
			}

			public ItemStack getWirelessCharger() {
				return wirelessCharger;
			}

			public void setWirelessCharger(ItemStack wirelessCharger) {
				this.wirelessCharger = wirelessCharger;
			}
		}

		public static WChargerRegistry instance;
		public List<WChargerEntry> chargers = Lists.newArrayList();

		public WChargerRegistry() {

		}
	}

	public static class PowerSyncRegistry implements ITickHandler {
		public static PowerSyncRegistry instance;

		public List<TileEntityWirelessPowerSynchronizer> registry = Lists
				.newArrayList();

		public PowerSyncRegistry() {
			TickRegistry.registerTickHandler(this, Side.SERVER);
		}

		@Override
		public void tickStart(EnumSet<TickType> type, Object... tickData) {
			registry.clear();

		}

		@Override
		public void tickEnd(EnumSet<TickType> type, Object... tickData) {
			// WChargerRegistry.instance.chargers.clear();
		}

		@Override
		public EnumSet<TickType> ticks() {
			return EnumSet.of(TickType.SERVER);
		}

		@Override
		public String getLabel() {
			return "PowerSyncTETickHandler";
		}
	}

	public SyncType deviceType = SyncType.RECEIVER;
	public int frequency = 0;

	public int internalBuffer = 0;

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		if (this.deviceType == SyncType.RECEIVER)
			return Math.min(MAX_PACKET_SIZE, internalBuffer);
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
		PowerSyncRegistry.instance.registry.add(this);
		if (this.deviceType.equals(SyncType.RECEIVER))
			return;
		WChargerRegistry.instance.chargers.clear();
		for (Object obj : this.worldObj.playerEntities) {
			if (!(obj instanceof EntityPlayer))
				continue;
			EntityPlayer player = (EntityPlayer) obj;
			for (ItemStack stack : player.inventory.mainInventory) {
				if (stack == null)
					continue;
				if (stack.getItem() instanceof ItemWirelessCharger) {
					NBTHelper.checkNBT(stack);
					boolean alreadyExists = false;
					for (WChargerEntry entryCurrIt : WChargerRegistry.instance.chargers) {
						if (entryCurrIt.player.username.equals(player.username)
								&& entryCurrIt.frequency == NBTHelper
										.getInteger(
												stack,
												ItemWirelessCharger.FREQUENCY_NBT)) {
							alreadyExists = true;
							break;
						}
					}
					if (!alreadyExists) {

						WChargerRegistry.instance.chargers
								.add(new WChargerEntry(
										player,
										stack,
										NBTHelper
												.getInteger(
														stack,
														ItemWirelessCharger.FREQUENCY_NBT)));
					}
				}
			}
		}
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
		if (this.deviceType.equals(SyncType.RECEIVER)) {
			return 0;
		} else {
			int requiredForPairs = 0;
			List objS = Lists.newArrayList();
			for (TileEntityWirelessPowerSynchronizer pSync : PowerSyncRegistry.instance.registry) {
				if (pSync.frequency == this.frequency
						&& pSync.deviceType.equals(SyncType.RECEIVER)) {
					objS.add(pSync);
				}
			}
			for (WChargerEntry charger : WChargerRegistry.instance.chargers) {
				if (charger.frequency == this.frequency) {
					objS.add(charger);
				}
			}
			for (Object obj : objS) {
				if (obj instanceof TileEntityWirelessPowerSynchronizer) {
					TileEntityWirelessPowerSynchronizer te = (TileEntityWirelessPowerSynchronizer) obj;
					requiredForPairs += te.getCapacity() - te.getStored();
				} else if (obj instanceof WChargerEntry) {
					WChargerEntry charger = (WChargerEntry) obj;
					requiredForPairs += ItemWirelessCharger
							.getRequiredEnergy(charger.player);
				}
			}
			return requiredForPairs;
		}
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		System.out.println(amount);
		if (this.deviceType.equals(SyncType.RECEIVER))
			return amount;
		List<Object> mutableRightSyncList = Lists.newArrayList();
		for (TileEntityWirelessPowerSynchronizer pSync : PowerSyncRegistry.instance.registry) {
			if (pSync.frequency == this.frequency
					&& pSync.deviceType.equals(SyncType.RECEIVER)
					&& (pSync.getCapacity() - pSync.getStored() > 0)) {
				mutableRightSyncList.add(pSync);
			}
		}
		for (WChargerEntry charger : WChargerRegistry.instance.chargers) {
			if (charger.getFrequency() == this.frequency
					&& (ItemWirelessCharger.getRequiredEnergy(charger
							.getPlayer()) > 0)) {
				mutableRightSyncList.add(charger);
			}
		}
		if (mutableRightSyncList.size() == 0)
			return amount;
		int forEach = (int) (amount / mutableRightSyncList.size());
		int notUsedUp = (int) amount;
		for (Object pSyncValid : mutableRightSyncList) {
			if (pSyncValid instanceof TileEntityWirelessPowerSynchronizer)
				((TileEntityWirelessPowerSynchronizer) pSyncValid)
						.addEnergy(forEach);
			else if (pSyncValid instanceof WChargerEntry) {
				WChargerEntry charger = (WChargerEntry) pSyncValid;
				ItemWirelessCharger.acceptEnergy(forEach, charger.getPlayer(),
						charger.getWirelessCharger(), false);
			} else
				throw new RuntimeException(
						Reference.MOD_ID
								+ ": pSyncValid is neither charger nor powersync. This is a bug, tell author about it!");
			notUsedUp -= forEach;
		}
		return notUsedUp;
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
		return 2048 + 1;
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
