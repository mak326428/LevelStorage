package makmods.levelstorage.registry;

import makmods.levelstorage.api.XPStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class FurnaceRecipesParser implements ISimpleRecipeParser {

	@Override
	public String getName() {
		return "Furnace Recipes Parser";
	}

	@Override
	public int parse() {
		int parsed = 0;
		FurnaceRecipes recs = FurnaceRecipes.smelting();
		XPStack[] stacks = (XPStack[]) XPStackRegistry.instance.entries
		        .toArray(new XPStack[XPStackRegistry.instance.entries.size()]);
		for (XPStack xps : stacks) {
			ItemStack outFor = recs.getSmeltingResult(xps.stack);
			if (outFor != null) {
				if (XPStackRegistry.instance.containsStack(outFor))
					continue;
				int xpForRec = xps.value;
				xpForRec += (int) recs.getExperience(xps.stack);
				XPStackRegistry.instance.pushToRegistry(new XPStack(outFor,
				        xpForRec / outFor.stackSize));
			}
		}
		return parsed;
	}

}
