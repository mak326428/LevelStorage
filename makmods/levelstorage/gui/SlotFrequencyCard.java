package makmods.levelstorage.gui;

import makmods.levelstorage.item.ItemFrequencyCard;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFrequencyCard extends SlotBook {
	public SlotFrequencyCard(IInventory par1IInventory, int par2, int par3,
			int par4) {
		super(par1IInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return checkItemValidity(stack);
	}

	public static boolean checkItemValidity(ItemStack stack) {
		boolean validForSlot = true;
		validForSlot = validForSlot && stack.getItem() instanceof ItemFrequencyCard;
		validForSlot = validForSlot && ItemFrequencyCard.isValid(stack);
		return validForSlot;
	}
}
