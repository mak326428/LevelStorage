package makmods.levelstorage.iv.parsers.recipe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.collect.Lists;

public class RecipeHelper {

	public static List<IRecipe> getAllRecipes() {
		return (List<IRecipe>) CraftingManager.getInstance().getRecipeList();
	}

	public static List<IRecipe> getRecipesFor(ItemStack is) {
		List<IRecipe> recs = Lists.newArrayList();

		List<IRecipe> allRecipes = getAllRecipes();
		for (IRecipe r : allRecipes) {
			ItemStack recOutput = r.getRecipeOutput();
			if (recOutput != null) {
				// System.out.println(recOutput);
				// System.out.println(is);

				if (recOutput.itemID == is.itemID
						&& (recOutput.getItemDamage() == is.getItemDamage() || is
								.getItemDamage() == OreDictionary.WILDCARD_VALUE))
					recs.add(r);
			}
		}

		return recs.size() > 0 ? recs : null;
	}

	public static String resolveOreDict(ItemStack stack) {
		if (stack == null)
			return null;
		for (Entry<ItemStack, String> entryIs : IVRegistry.itemStackToNameMap
				.entrySet()) {
			ItemStack st = entryIs.getKey();
			if (st.itemID == stack.itemID
					&& (st.getItemDamage() == stack.getItemDamage() || st
							.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
				return entryIs.getValue();
			}
		}
		return null;
	}

	public static Object[] examineAdvRecipe(Object obj) {
		Class<?> advRecipeClass = obj.getClass();
		try {
			Field inputField = advRecipeClass.getField("input");
			return (Object[]) inputField.get(obj);
		} catch (Exception e) {
			LogHelper
					.severe("Failed to examine Advanced Recipe. Either you're using a too old or a too new version of IC2 (most probably the latter");
			e.printStackTrace();
		}
		return null;
	}

	public static List<IWrappedRecipeCompound> getKnownCompounds(IRecipe recipe) {
		List<IWrappedRecipeCompound> ingrs = Lists.newArrayList();

		if (recipe instanceof ShapedRecipes)
			for (ItemStack stack : ((ShapedRecipes) recipe).recipeItems)
				ingrs.add(new ItemStackRecipeCompound(stack));
		else if (recipe instanceof ShapelessRecipes) {
			List<ItemStack> items = ((ShapelessRecipes) recipe).recipeItems;
			for (ItemStack stack : items)
				ingrs.add(new ItemStackRecipeCompound(stack));
		} else if (recipe instanceof ShapelessOreRecipe) {
			for (Object obj : ((ShapelessOreRecipe) recipe).getInput()) {
				if (obj instanceof ItemStack) {
					ingrs.add(new ItemStackRecipeCompound((ItemStack) obj));
				} else if (obj instanceof List) {
					List<ItemStack> list = (List<ItemStack>) obj;
					if (list.size() > 0) {
						String oreDictName = resolveOreDict(list.get(0));
						if (oreDictName != null)
							ingrs.add(new OreDictRecipeCompound(oreDictName));
					}

				} else {
					LogHelper
							.severe("RecipeHelper.java: getKnownCompounds(): ShapelessOreRecipe - input compound is unknown. ("
									+ obj.getClass().getCanonicalName() + ")");
				}
			}
		} else if (recipe instanceof ShapedOreRecipe) {
			for (Object obj : ((ShapedOreRecipe) recipe).getInput()) {
				if (obj instanceof ItemStack) {
					ingrs.add(new ItemStackRecipeCompound((ItemStack) obj));
				} else if (obj instanceof List) {
					List<ItemStack> list = (List<ItemStack>) obj;
					if (list.size() > 0) {
						String oreDictName = resolveOreDict(list.get(0));
						if (oreDictName != null)
							ingrs.add(new OreDictRecipeCompound(oreDictName));
					}

				} else {
					try {
						LogHelper
								.severe("RecipeHelper.java: getKnownCompounds(): ShapedOreRecipe - input compound is unknown. ("
										+ obj.getClass().getCanonicalName()
										+ ")");
					} catch (Exception e) {
					}
				}
			}
		} else if (recipe.getClass().getSimpleName().equals("AdvRecipe")
				|| recipe.getClass().getSimpleName()
						.equals("AdvShapelessRecipe")) {
			Object[] contents = examineAdvRecipe(recipe);
			if (contents == null) {
				LogHelper
						.severe("Something really bad and weird happened when tried to parse AdvRecipe. (contents = null)");
				return null;
			}
			for (Object obj : contents) {
				if (obj != null) {
					if (obj instanceof String) {
						ingrs.add(new OreDictRecipeCompound((String)obj));
					} else if (obj instanceof ItemStack) {
						ingrs.add(new ItemStackRecipeCompound((ItemStack)obj));
					} else {
						LogHelper
								.severe("RecipeHelper: AdvRecipe parsing: incorrect obj type - "
										+ obj.getClass().getSimpleName());
					}
				}
			}
		} else
			LogHelper.warning("Unknown recipe type: "
					+ recipe.getClass().getCanonicalName());
		// TODO: add parsing for the adv recipes... reflection is a bad way out,
		// but it works.
		return ingrs.size() > 0 ? ingrs : null;
	}
}
