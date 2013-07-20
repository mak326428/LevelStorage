package makmods.levelstorage.gui;

import makmods.levelstorage.item.ItemFrequencyCard;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFrequencyCard extends SlotBook {
	public SlotFrequencyCard(IInventory par1IInventory, int par2, int par3, int par4) {
		super(par1IInventory, par2, par3, par4);
	}
	
	public boolean isItemValid(ItemStack stack) {
		return checkItemValidity(stack);
	}
	
	public static boolean checkItemValidity(ItemStack stack) {
		return stack.getItem() instanceof ItemFrequencyCard;
	}
}
