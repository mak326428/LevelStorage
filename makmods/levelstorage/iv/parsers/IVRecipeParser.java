package makmods.levelstorage.iv.parsers;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.iv.IVEntry;
import makmods.levelstorage.iv.IVItemStackEntry;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.iv.parsers.recipe.IWrappedRecipeCompound;
import makmods.levelstorage.iv.parsers.recipe.ItemStackRecipeCompound;
import makmods.levelstorage.iv.parsers.recipe.OreDictRecipeCompound;
import makmods.levelstorage.iv.parsers.recipe.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;

import com.google.common.collect.Lists;

public class IVRecipeParser implements IRecipeParser {

	// TODO: if entry already exists and new entry's IV is less than old entries
	// IV, delete old entry and add a new one

	public static int PASSES = LevelStorage.configuration
			.get(IVRegistry.IV_CATEGORY,
					"dynamicAssignmentPasses",
					2,
					"Determines how many passes (\"attempts\") will be made. Basically, the lower value, the faster minecraft will start, the higher value, the more items will be assigned")
			.getInt();

	public int assignCrafting(ItemStack is) {
		List<IRecipe> recipesFor = RecipeHelper.getRecipesFor(is);
		if (recipesFor.size() == 0)
			return IVRegistry.NOT_FOUND;
		List<Integer> candidates = Lists.newArrayList();
		for (IRecipe recipe : recipesFor) {
			int fIV = 0;
			List<IWrappedRecipeCompound> iss = RecipeHelper
					.getKnownCompounds(recipe);
			if (iss == null)
				continue;
			boolean hasUnknown = false;
			for (IWrappedRecipeCompound rc : iss) {
				if (rc instanceof ItemStackRecipeCompound) {
					ItemStackRecipeCompound isrc = (ItemStackRecipeCompound) rc;
					int value = IVRegistry.getValue(isrc.getStack());
					if (value != IVRegistry.NOT_FOUND)
						fIV += value;
					else
						hasUnknown = true;
				} else {
					OreDictRecipeCompound odrc = (OreDictRecipeCompound) rc;
					int value = IVRegistry.getValue(odrc.getName());
					if (value != IVRegistry.NOT_FOUND)
						fIV += value;
					else
						hasUnknown = true;
				}
			}
			if (recipe.getRecipeOutput().stackSize > 0)
				fIV /= recipe.getRecipeOutput().stackSize;
			if (fIV > 0 && !hasUnknown)
				candidates.add(fIV);
		}
		int toReleased = Integer.MAX_VALUE;
		for (Integer candidate : candidates)
			if (candidate < toReleased && (candidate != IVRegistry.NOT_FOUND))
				toReleased = candidate;
		if (toReleased == Integer.MAX_VALUE)
			return IVRegistry.NOT_FOUND;
		else {
			String oreDict = RecipeHelper.resolveOreDict(is);
			if (oreDict != null)
				if (!IVRegistry.hasValue(oreDict))
					IVRegistry.instance.assignOreDict_dynamic(oreDict,
							toReleased);
			if (!IVRegistry.hasValue(is))
				IVRegistry.instance.assignItemStack_dynamic(is, toReleased);
			return toReleased;
		}
	}

	public void parseWithPrint(ItemStack is) {
		assignCrafting(is);
	}

	// public int assignFurnace(ItemStack is) {
	//
	// }

	public void assignIC2Machine(IMachineRecipeManager manager) {
		Map<IRecipeInput, RecipeOutput> recipes = manager.getRecipes();
		for (Entry<IRecipeInput, RecipeOutput> entry : recipes.entrySet()) {
			IRecipeInput input = entry.getKey();
			RecipeOutput output = entry.getValue();

			int inputValue = input instanceof RecipeInputItemStack ? IVRegistry
					.getValue(((RecipeInputItemStack) input).input)
					: IVRegistry.getValue(((RecipeInputOreDict) input).input);
			if (inputValue == IVRegistry.NOT_FOUND)
				continue;
			int iv = inputValue / output.items.get(0).stackSize;
			if (iv > 0 && !IVRegistry.hasValue(output.items.get(0)))
				IVRegistry.instance.assignItemStack_dynamic(
						output.items.get(0), iv);
		}
	}

	@Override
	public void parse() {
		// #CRAFTING
		List<IRecipe> recipes = RecipeHelper.getAllRecipes();
		for (int i = 0; i < PASSES; i++) {
			List<IVEntry> copied = IVRegistry.instance.copyRegistry();

			for (IVEntry entry : copied) {
				if (!(entry instanceof IVItemStackEntry))
					continue;
				IVItemStackEntry e = (IVItemStackEntry) entry;

				ItemStack outputSmelting = FurnaceRecipes.smelting()
						.getSmeltingResult(e.getStack());
				if (outputSmelting != null) {
					int iv = entry.getValue() / outputSmelting.stackSize;
					if (!IVRegistry.hasValue(outputSmelting))
						IVRegistry.instance.assignItemStack_dynamic(
								outputSmelting.copy(), iv);
				}
			}

			assignIC2Machine(Recipes.macerator);
			assignIC2Machine(Recipes.extractor);
			assignIC2Machine(Recipes.compressor);
			assignIC2Machine(Recipes.metalformerRolling);
			assignIC2Machine(Recipes.metalformerExtruding);
			assignIC2Machine(Recipes.metalformerCutting);

			for (IRecipe recipe : recipes) {
				ItemStack result = recipe.getRecipeOutput();
				if (result != null)
					parseWithPrint(result);
			}
		}
	}

}
