package makmods.levelstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerWirelessCharger extends Container {

	public ContainerWirelessCharger(int dimId, double x, double y, double z, int stackId) {
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
