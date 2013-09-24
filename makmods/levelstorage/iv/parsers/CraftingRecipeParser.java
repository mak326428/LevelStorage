package makmods.levelstorage.iv.parsers;

import java.util.List;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import com.google.common.collect.Lists;

public class CraftingRecipeParser implements IRecipeParser {

	public List<StandartRecipeWrapper> recipes = null;

	public static class StandartRecipeWrapper {
		public ItemStack output;
		public ItemStack[] input;
	}

	@Override
	public String getName() {
		return "Crafting Recipes Parser";
	}

	public List<StandartRecipeWrapper> getRecipeList() {
		List l = CraftingManager.getInstance().getRecipeList();
		List<StandartRecipeWrapper> recipes = Lists.newArrayList();
		for (Object r : l) {
			if (r instanceof ShapedRecipes) {
				StandartRecipeWrapper srw = new StandartRecipeWrapper();
				srw.input = ((ShapedRecipes) r).recipeItems;
				srw.output = ((ShapedRecipes) r).getRecipeOutput();
				recipes.add(srw);
			} else if (r instanceof ShapelessRecipes) {
				StandartRecipeWrapper srw = new StandartRecipeWrapper();
				List objs = ((ShapelessRecipes) r).recipeItems;
				ItemStack[] inputs = new ItemStack[objs.size()];
				for (int i = 0; i < objs.size(); i++) {
					Object obj = objs.get(i);
					if (obj instanceof ItemStack)
						inputs[i] = (ItemStack)obj;
					else
						LogHelper
								.warning("Shapeless Recipe item is not ItemStack. That's a bug.");
				}
				srw.output = ((ShapelessRecipes) r).getRecipeOutput();
				srw.input = inputs;
				recipes.add(srw);
			}
		}
		return recipes;
	}

	@Override
	public int parse() {
		if (recipes == null)
			recipes = getRecipeList();
		int parsed = 0;
		for (StandartRecipeWrapper recipe : recipes) {
			if (recipe == null)
				continue;
			
			boolean hasUnknown = false;
			int value = 0;

			for (ItemStack obj : recipe.input) {
				if (obj == null)
					continue;
				int valueForObj = IVRegistry.instance.getValueFor(obj);
				if (valueForObj == IVRegistry.NOT_FOUND) {
					hasUnknown = true;
					break;
				}
				value += valueForObj;
			}
			if (!hasUnknown) {
				if (value > 0) {
					ItemStack outp = recipe.output.copy();
					if (IVRegistry.instance.getValueFor(outp) == IVRegistry.NOT_FOUND) {
						
						IVRegistry.instance.registerIS(outp, value
								/ outp.stackSize);
						parsed++;
					}
				}
			}
		}
		return parsed;
	}

}
