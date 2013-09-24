package makmods.levelstorage.iv.parsers;

import java.util.List;

import makmods.levelstorage.iv.IVEntry;
import makmods.levelstorage.iv.IVItemStackEntry;
import makmods.levelstorage.iv.IVRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SmeltingRecipeParser implements IRecipeParser {

	@Override
	public String getName() {
		return "Furnace Recipes Parser";
	}

	@Override
	public int parse() {
		int parsed = 0;
		FurnaceRecipes fr = FurnaceRecipes.smelting();
		List<IVEntry> registry = IVRegistry.instance.copyRegistry();
		for (IVEntry entry : registry) {
			if (entry instanceof IVItemStackEntry) {
				IVItemStackEntry ise = (IVItemStackEntry) entry;
				ItemStack outputForEntry = fr.getSmeltingResult(ise.getStack());
				if (outputForEntry != null) {
					int xpForRec = ise.getValue();
					if (xpForRec > 0) {
						if (IVRegistry.instance.getValueFor(outputForEntry) == IVRegistry.NOT_FOUND) {
							IVRegistry.instance.registerIS(outputForEntry,
									xpForRec / outputForEntry.stackSize);
							parsed++;
						}
					}
				}
			}
		}
		return parsed;
	}

}
