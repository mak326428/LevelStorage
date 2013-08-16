package makmods.levelstorage.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
/**
 * Fired every tick when player is flying using Levitation Boots
 */
public class BootsFlyingEvent extends PlayerEvent {
	
	public ItemStack levitationBoots;

	public BootsFlyingEvent(EntityPlayer player, ItemStack boots) {
	    super(player);
	    this.levitationBoots = boots;
    }

}
