package makmods.levelstorage.iv.parsers;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;

import java.util.Map.Entry;

import makmods.levelstorage.iv.IVRegistry;
import net.minecraft.item.ItemStack;

public class IC2MachineRecipeParser implements IRecipeParser {

	public final IMachineRecipeManager manager;

	public IC2MachineRecipeParser(IMachineRecipeManager man) {
		manager = man;
	}

	@Override
	public String getName() {
		return "IC2 Machine Recipes Parser";
	}

	@Override
	public int parse() {
		int parsed = 0;
		for (Entry<IRecipeInput, RecipeOutput> recipe : manager.getRecipes()
				.entrySet()) {
			IRecipeInput input = recipe.getKey();
			if (input instanceof RecipeInputItemStack) {
				RecipeInputItemStack riis = (RecipeInputItemStack) input;
				int valueV = IVRegistry.instance.getValueFor(riis.input);
				if (valueV > 0) {
					ItemStack output = recipe.getValue().items.get(0);
					if (IVRegistry.instance.getValueFor(output) == IVRegistry.NOT_FOUND) {
						IVRegistry.instance.registerIS(output, valueV / output.stackSize);
						parsed++;
					}
				}
			} else if (input instanceof RecipeInputOreDict) {
				RecipeInputOreDict riod = (RecipeInputOreDict)input;
				int valueV = IVRegistry.instance.getValueFor(riod.input);
				if (valueV > 0) {
					ItemStack output = recipe.getValue().items.get(0);
					if (IVRegistry.instance.getValueFor(output) == IVRegistry.NOT_FOUND) {
						IVRegistry.instance.registerIS(output, valueV / output.stackSize);
						parsed++;
					}
				}
			}
		}
		return parsed;
	}

}
