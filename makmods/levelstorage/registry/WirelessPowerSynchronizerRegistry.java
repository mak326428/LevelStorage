package makmods.levelstorage.registry;

import java.util.ArrayList;

/**
 * Power syncs are the next tier of Wireless conductors While the old ones were
 * just for transporting energy from point A to point B, this one will use
 * frequencies (nubmers) So, there might be 50 devices in one network and they
 * all will share power
 * 
 * @author mak326428
 */
public class WirelessPowerSynchronizerRegistry {
	// Basically, Map<Frequency, Device(s)>
	private ArrayList<IWirelessPowerSync> registry;

	/**
	 * Static singleton instance for this class
	 */
	public static final WirelessPowerSynchronizerRegistry instance = new WirelessPowerSynchronizerRegistry();

	private WirelessPowerSynchronizerRegistry() {
		this.registry = new ArrayList<IWirelessPowerSync>();
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
		for (IWirelessPowerSync device : registry) {
			if (device.getFreq() == freq)
				needed.add(device);
		}
		return (IWirelessPowerSync[]) needed.toArray();
	}

	/**
	 * Adds the device, if it's not already added
	 * 
	 * @param device
	 *            PowerSync to add
	 * @return whether or not device was added
	 */
	public boolean addDevice(IWirelessPowerSync device) {
		if (registry.contains(device))
			return false;
		registry.add(device);
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
		if (!registry.contains(device))
			return false;
		registry.remove(device);
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
		return registry.contains(device);
	}
	
	/**
	 * Gets all devices inside registry maching given frequency and type
	 * @param freq Frequency to find
	 * @param type Type to match
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

		return (IWirelessPowerSync[]) needed.toArray();
	}

}
