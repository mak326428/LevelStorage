package makmods.levelstorage.api;

import net.minecraft.item.ItemStack;

/**
 * ArmorTicker checks player's armor every tick to make sure he's flying
 * legitemetely if your mod provides flying armor too, implement this interface
 * and you'll be friends with ArmorTicker.
 * 
 * @author mak326428
 */
public interface IFlyArmor {
	/**
	 * 
	 * @return
	 */
	public boolean isFlyArmor(ItemStack is);
}
