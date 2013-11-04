package makmods.levelstorage.iv.parsers.recipe;

import makmods.levelstorage.logic.util.CommonHelper;
import net.minecraft.item.ItemStack;

public class ItemStackRecipeCompound extends IWrappedRecipeCompound {
	
	private final ItemStack stack;

	public ItemStackRecipeCompound(ItemStack is) {
		this.stack = is;
	}
	
	public ItemStack getStack() {
		return stack;
	}
	
	@Override
	public String toString() {
		return "ItemStackRC: " + CommonHelper.getNiceStackName(stack);
	}
}
