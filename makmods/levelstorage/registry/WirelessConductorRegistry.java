package makmods.levelstorage.registry;

import java.util.HashMap;
import java.util.Map;

import makmods.levelstorage.tileentity.IWirelessConductor;

public class WirelessConductorRegistry {
	public static final WirelessConductorRegistry instance = new WirelessConductorRegistry();

	private Map<IWirelessConductor, ConductorType> conductors;

	private WirelessConductorRegistry() {
		this.conductors = new HashMap<IWirelessConductor, ConductorType>();
	}

	/**
	 * Adds conductor to registry
	 * 
	 * @param conductor
	 *            TileEntity to add
	 * @param type
	 *            Type of conductor (receiver or emitter)
	 * @return whether conductor was added or not
	 */
	public boolean addConductorToRegistry(IWirelessConductor conductor,
			ConductorType type) {
		if (this.conductors.containsKey(conductor))
			return false;
		this.conductors.put(conductor, type);
		return true;
	}

	/**
	 * Removes conductor from registry. You should call this when conductor
	 * cannot act, e.g. on chunk unload
	 * 
	 * @param conductor
	 *            conductor to delete
	 */
	public void removeFromRegistry(IWirelessConductor conductor) {
		if (this.isAddedToRegistry(conductor)) {
			this.conductors.remove(conductor);
		}
	}

	/**
	 * Gets the type of the conductor inside the registry
	 * 
	 * @param conductor
	 * @return Type of the conductor
	 */
	public ConductorType getConductorType(IWirelessConductor conductor) {
		if (this.isAddedToRegistry(conductor))
			return this.conductors.get(conductor);
		return null;
	}

	/**
	 * Checks whether or not conductors exists in the current registry
	 * 
	 * @param conductor
	 * @return whether or not the conductor exists in the registry
	 */
	public boolean isAddedToRegistry(IWirelessConductor conductor) {
		return this.conductors.containsKey(conductor);
	}

	/**
	 * Sets the conductor type (notice that you need to add it to registry
	 * first)
	 * 
	 * @param conductor
	 *            conductor to set type on
	 * @param type
	 *            new type for the conductor
	 * @return whether or not type was set
	 */
	public boolean setConductorType(IWirelessConductor conductor,
			ConductorType type) {
		if (!this.conductors.containsKey(conductor))
			return false;
		this.conductors.remove(conductor);
		this.conductors.put(conductor, type);

		return true;
	}
}
