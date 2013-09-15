package makmods.levelstorage.registry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

public class CraftingRecipesParser implements ISimpleRecipeParser {

	@Override
	public String getName() {
		return "Crafting recipes parser";
	}

	@Override
	public int parse() {
		return parseBasicRecipes();
	}

	private int parseBasicRecipes() {
		int parsed = 0;
		List<IRecipe> recipes = getRecipes();
		for (IRecipe recipe : recipes) {
			// Recipe is not worth getting into Registry
			boolean notFoundAnyGlobal = false;
			if (recipe == null)
				continue;
			List<ItemStack> inputs = getRecipeComponents(recipe);
			if (inputs.size() == 0)
				continue;
			ItemStack output = recipe.getRecipeOutput();
			int currValue = 0;
			ArrayList<Boolean> matches = Lists.newArrayList();
			for (ItemStack inputStack : inputs) {
				int valueHere = 0;
				if (inputStack == null)
					continue;
				for (XPStack stack : XPStackRegistry.instance.entries) {
					if (stack.stack.itemID == inputStack.itemID
							&& stack.stack.getItemDamage() == inputStack
									.getItemDamage()) {
						valueHere += stack.value;
					}
				}
				matches.add(valueHere > 0);
				currValue += valueHere;
			}
			notFoundAnyGlobal = matches.contains(false);
			if (notFoundAnyGlobal)
				continue;
			if (currValue > 0) {
				boolean foundAlready = false;
				for (XPStack stack : XPStackRegistry.instance.entries) {
					if (stack.stack.itemID == output.itemID
							&& stack.stack.itemID == output.itemID) {
						foundAlready = true;
					}
				}
				if (!foundAlready) {
					ItemStack toReg = output.copy();
					toReg.stackSize = 1;
					int v = (int) Math.floor(currValue / output.stackSize);
					if (v > 0) {
						XPStackRegistry.instance.pushToRegistry(new XPStack(
								toReg, v));
						parsed += 1;
					}
				}
			}
		}
		return parsed;
	}

	private List<IRecipe> getRecipes() {
		IRecipe[] recipes = (IRecipe[]) CraftingManager
				.getInstance()
				.getRecipeList()
				.toArray(
						new IRecipe[CraftingManager.getInstance()
								.getRecipeList().size()]);
		List<IRecipe> retRecipes = Lists.newArrayList();
		for (IRecipe recipe : recipes)
			retRecipes.add(recipe);
		return retRecipes;
	}

	private List<ItemStack> getRecipeComponents(IRecipe recipe) {
		List<ItemStack> list = Lists.newArrayList();
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes) recipe;
			for (ItemStack stack : r.recipeItems) {
				if (stack == null)
					continue;
				list.add(stack.copy());
			}
		} else if (recipe instanceof ShapelessRecipes) {
			ShapelessRecipes r = (ShapelessRecipes) recipe;
			for (Object stack : r.recipeItems) {
				if (stack instanceof ItemStack) {
					list.add(((ItemStack) stack).copy());
				}
			}
		} else if (recipe.getClass().getName().equals(ADV_RECIPE_CLASS)) {
			try {
				Class advRecipeClass = recipe.getClass();
				Field fIn = advRecipeClass.getField(INPUT_FIELD);
				Object[] inputs = (Object[]) fIn.get(recipe);
				for (Object obj : inputs)
					if (obj instanceof ItemStack) {
						// Simple itemstack
						list.add((ItemStack) obj);
					} else if (obj instanceof String) {
						// OREDICT
						List<ItemStack> ores = OreDictionary
								.getOres((String) obj);
						if (ores.size() > 0)
							list.add(ores.get(0));
					}
			} catch (Exception e) {
				LogHelper.severe("Failed to parse AdvRecipe");
				e.printStackTrace();
			}
		}
		return list;
	}

	public static final String ADV_RECIPE_CLASS = "ic2.core.AdvRecipe";
	public static final String OUTPUT_FIELD = "output";
	public static final String INPUT_FIELD = "input";

}
