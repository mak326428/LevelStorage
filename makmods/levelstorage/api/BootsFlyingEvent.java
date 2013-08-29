package makmods.levelstorage.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired every tick when player is flying using Levitation Boots
 */
public class BootsFlyingEvent extends PlayerEvent {
	/**
	 * Boots' ItemStack. If you want to do something <br />
	 * with it, access this field.
	 */
	public ItemStack levitationBoots;

	/**
	 * Initialize new instance of this class
	 * 
	 * @param player
	 *            Player
	 * @param boots
	 *            Boots' ItemStack
	 */
	public BootsFlyingEvent(EntityPlayer player, ItemStack boots) {
		super(player);
		this.levitationBoots = boots;
	}

}
