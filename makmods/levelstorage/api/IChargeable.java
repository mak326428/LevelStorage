package makmods.levelstorage.api;

import net.minecraft.item.ItemStack;

/**
 * Interface for dealing with chargeables
 * @author mak326428
 *
 */
public interface IChargeable {
	
	public static final String CHARGE_NBT = "itemChargeMode";

	/**
	 * Gets charge for given ItemStack
	 * 
	 * @param stack
	 *            Stack
	 * @return Charge for given ItemStack
	 */
	public int getChargeFor(ItemStack stack);

	/**
	 * Sets charge for given ItemStack
	 * 
	 * @param stack
	 *            Stack to set charge to
	 * @param charge
	 *            New charge
	 */
	public void setChargeFor(ItemStack stack, int charge);

	/**
	 * Gets max charge for the item
	 * 
	 * @return Max charge
	 */
	public int getMaxCharge();
}
