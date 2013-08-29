package makmods.levelstorage.logic.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GUIHelper extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return false;
	}

}
