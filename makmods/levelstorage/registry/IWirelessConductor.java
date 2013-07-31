package makmods.levelstorage.registry;


/**
 * Basic interface for all the wireless conductors.
 * 
 * @author mak326428
 */
public interface IWirelessConductor {
	/**
	 * X coord for the current instance
	 */
	public int getX();

	/**
	 * Type for current instance
	 */
	public ConductorType getType();

	/**
	 * Y coord for the current instance
	 */
	public int getY();

	/**
	 * Z coord for the current instance
	 */
	public int getZ();

	/**
	 * Dimension id for the current instance
	 */
	public int getDimId();

	/**
	 * ConductorType.SINK only, called when some other paired conductor sends
	 * you energy
	 * 
	 * @param amount
	 *            Energy being sent
	 * @return Energy not being consumed
	 */
	public int receiveEnergy(int amount);

	/**
	 * Returns pair for the current instance
	 * 
	 * @return linked pair
	 */
	public IWirelessConductor getPair();
}
