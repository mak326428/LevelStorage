package makmods.levelstorage.registry;

import java.util.ArrayList;

import makmods.levelstorage.item.ItemWirelessCharger;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Power syncs are the next tier of Wireless conductors While the old ones were
 * just for transporting energy from point A to point B, this one will use
 * frequencies (numbers) So, there might be 50 devices in one network and they
 * all will share power
 * 
 * @author mak326428
 */
public class WirelessPowerSynchronizerRegistry {

	public static class WChargerEntry {
		public EntityPlayer player;
		public ItemStack charger;
		public int frequency;

		public WChargerEntry(EntityPlayer pl, ItemStack st, int freq) {
			player = pl;
			charger = st;
			frequency = freq;
		}
	}

	// Basically, Map<Frequency, Device(s)>
	public ArrayList<IWirelessPowerSync> registry;
	public ArrayList<WChargerEntry> registryChargers;

	/**
	 * Static singleton instance for this class
	 */
	public static final WirelessPowerSynchronizerRegistry instance = new WirelessPowerSynchronizerRegistry();

	private WirelessPowerSynchronizerRegistry() {
		this.registry = new ArrayList<IWirelessPowerSync>();
		this.registryChargers = new ArrayList<WChargerEntry>();
	}

	public IWirelessPowerSync[] fromObjectToDeviceArray(Object[] array) {
		IWirelessPowerSync[] retArray = new IWirelessPowerSync[array.length];

		for (int i = 0; i < array.length; i++) {
			retArray[i] = (IWirelessPowerSync) array[i];
		}

		return retArray;
	}

	/**
	 * Gets all the devices for frequency
	 * 
	 * @param freq
	 *            Frequency
	 * @return All the devices inside registry for the given frequency
	 */
	public IWirelessPowerSync[] getDevicesForFreq(int freq) {
		ArrayList<IWirelessPowerSync> needed = new ArrayList<IWirelessPowerSync>();
		for (IWirelessPowerSync device : this.registry) {
			if (device.getFreq() == freq) {
				needed.add(device);
			}
		}
		return this.fromObjectToDeviceArray(needed.toArray());
	}

	/**
	 * Adds the device, if it's not already added
	 * 
	 * @param device
	 *            PowerSync to add
	 * @return whether or not device was added
	 */
	public boolean addDevice(IWirelessPowerSync device) {
		if (this.registry.contains(device))
			return false;
		this.registry.add(device);
		for (IWirelessPowerSync entry : this.registry) {
			entry.updateState();
		}
		return true;
	}

	/**
	 * Removes the device, if there's any
	 * 
	 * @param device
	 *            device to remove from registry
	 * @return whether or not the device was removed
	 */
	public boolean removeDevice(IWirelessPowerSync device) {
		if (!this.registry.contains(device))
			return false;
		this.registry.remove(device);
		for (IWirelessPowerSync entry : this.registry) {
			entry.updateState();
		}
		return true;
	}

	/**
	 * Checks if device is added to registry
	 * 
	 * @param device
	 *            Device to check
	 * @return Whether or not device exists in registry.
	 */
	public boolean isDeviceAdded(IWirelessPowerSync device) {
		return this.registry.contains(device);
	}

	/**
	 * Gets all devices inside registry maching given frequency and type
	 * 
	 * @param freq
	 *            Frequency to find
	 * @param type
	 *            Type to match
	 * @return All devices in registry matching given frequency and type
	 */
	public IWirelessPowerSync[] getDevicesForFreqAndType(int freq, SyncType type) {
		IWirelessPowerSync[] allDevices = this.getDevicesForFreq(freq);
		ArrayList<IWirelessPowerSync> needed = new ArrayList<IWirelessPowerSync>();

		for (IWirelessPowerSync device : allDevices) {
			if (device.getType() == type) {
				needed.add(device);
			}
		}

		return this.fromObjectToDeviceArray(needed.toArray());
	}

	// public boolean updateRegistryEntry(IWirelessPowerSync device, SyncType
	// newType) {
	// if (!registry.contains(device))
	// return false;
	// registry.set(registry.indexOf(device), device);
	// }

	public int onInjectEnergy(int amount, TileEntityWirelessPowerSynchronizer te) {
		return sendEnergyEqually(amount, te);
	}

	private int sendEnergyToDevices(ArrayList<IWirelessPowerSync> devs,
	        int amount, TileEntityWirelessPowerSynchronizer te) {
		int unused = 0;
		if (devs.size() > 0) {
			int forEach;
			if (devs.size() != 0) {
				forEach = amount / devs.size();
			} else {
				forEach = amount;
			}
			for (IWirelessPowerSync s : devs) {
				BlockLocation thisTe = new BlockLocation(
				        te.getWorld().provider.dimensionId, te.getX(),
				        te.getY(), te.getZ());
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

	public static boolean hasChargersOnFreq(int frequency) {
		for (WChargerEntry entry : WirelessPowerSynchronizerRegistry.instance.registryChargers) {
			if (entry.frequency == frequency) {
				return true;
			}
		}
		return false;
	}

	private int sendEnergyEqually(int amount,
	        TileEntityWirelessPowerSynchronizer te) {
		int energyNotUsed = 0;
		boolean usedAny = false;
		// TODO: if it doesn't work, try !world.isRemote on injectEnergy in
		// psyncs
		if (te.pairs.length > 0 || hasChargersOnFreq(te.frequency)) {

			ArrayList<Object> pairs = new ArrayList<Object>();

			for (IWirelessPowerSync sync : te.pairs) {
				pairs.add(sync);
			}

			for (WChargerEntry entry : registryChargers) {
				// System.out.println("Entry freq: " + entry.frequency);
				// System.out.println("TE freq: " + te.frequency);

				// System.out.println("TE X:" + te.xCoord);
				// System.out.println("TE Y:" + te.yCoord);
				// System.out.println("TE Z:" + te.zCoord);

				if (entry.frequency == te.frequency) {
					// System.out.println("adding to the list charger");
					pairs.add(entry);
				}
			}

			int forEach = amount;
			if (pairs.size() > 0) {
				forEach = amount / pairs.size();
			}

			for (Object s : pairs) {
				// System.out.println(s.getClass().getName());
				if (s instanceof IWirelessPowerSync) {
					IWirelessPowerSync entry = (IWirelessPowerSync) s;
					BlockLocation thisTe = new BlockLocation(
					        te.getWorld().provider.dimensionId, te.getX(),
					        te.getY(), te.getZ());
					BlockLocation pairTe = new BlockLocation(
					        entry.getWorld().provider.dimensionId,
					        entry.getX(), entry.getY(), entry.getZ());
					int forEachWithDisc = forEach
					        - BlockLocation.getEnergyDiscount(forEach,
					                thisTe.getDistance(pairTe));
					int leftover = entry.receiveEnergy(forEachWithDisc);
					if (leftover == forEachWithDisc) {
						ArrayList<IWirelessPowerSync> par5 = new ArrayList<IWirelessPowerSync>();
						for (IWirelessPowerSync par6 : te.pairs) {
							if (par6 != s) {
								par5.add(par6);
							}
						}
						int par7 = this.sendEnergyToDevices(par5, leftover, te);
						energyNotUsed += par7;
					} else {
						energyNotUsed += leftover;
					}
				}
				if (s instanceof WChargerEntry) {
					energyNotUsed += ItemWirelessCharger.acceptEnergy(forEach,
					        ((WChargerEntry) s).player,
					        ((WChargerEntry) s).charger);
				}
			}
			usedAny = true;
		}
		return usedAny ? energyNotUsed : amount;
	}

}
