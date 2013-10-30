package makmods.levelstorage.gui.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import makmods.levelstorage.gui.container.phantom.PhantomSlot;
import makmods.levelstorage.iv.IVRegistry;

public class IVSampleSlot extends PhantomSlot {

	public IVSampleSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public boolean isItemValid(ItemStack par1ItemStack) {
		return IVRegistry.hasValue(par1ItemStack);
	}

	@Override
	public boolean isUnstackable() {
		return true;
	}
}
