package makmods.levelstorage.logic.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class GUIHelper extends Container {

	@Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
	    return false;
    }

}
