package makmods.levelstorage.gui.container.phantom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class PhantomSlot extends Slot {

	public PhantomSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }
    
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return false;
    }
    
    public abstract boolean isUnstackable();

}
