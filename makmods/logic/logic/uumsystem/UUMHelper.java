package makmods.logic.logic.uumsystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import makmods.levelstorage.logic.Helper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class UUMHelper {

	public static boolean canCraftWithUUM(ItemStack what) {
		if (!canCraft(what))
			return false;
		return false;
	}

	public static ItemStack[] recursivelyGetIngredients(ItemStack forWhat) {
		ArrayList<ItemStack> ingredients = new ArrayList();
		if (canCraft(forWhat)) {
			
		} else{
			ingredients.add(forWhat);
		}
		return (ItemStack[]) ingredients.toArray(new ItemStack[ingredients
				.size()]);
	}

	public static ItemStack[] removeDuplicates(ItemStack[] a) {
		HashSet<ItemStack> keys = new HashSet<ItemStack>();
		ItemStack[] result = new ItemStack[a.length];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (keys.add(a[i])) {
				result[j] = a[i];
				j++;
			}
		}
		return Arrays.copyOf(result, j);
	}

	public static boolean canCraft(ItemStack what) {
		return getRecipeFor(what) != null;
	}
	
	public static <T> ArrayList<T> convertArrayIntoArrList(T[] arr) {
		ArrayList<T> arrList = new ArrayList<T>();
		for (T entry : arr) {
			arrList.add(entry);
		}
		return arrList;
	}

	public static ArrayList<ItemStack> getRecipeInputs(IRecipe rec) {
		ArrayList<ItemStack> inputs = null;
		if (rec instanceof ShapedRecipes) {
			inputs = convertArrayIntoArrList(((ShapedRecipes) rec).recipeItems);
		}
		if (rec instanceof ShapelessRecipes) {
			List l = ((ShapelessRecipes) rec).recipeItems;
			inputs = convertArrayIntoArrList((ItemStack[]) l.toArray(new ItemStack[l.size()]));
		}
		if (rec.getClass().getName() == UUMRecipeParser.ADV_RECIPE_CLASS) {
			try {
				Field inputs_field = rec.getClass().getField(
						UUMRecipeParser.INPUT_FIELD);
				Object[] input_recipe = (Object[]) inputs_field.get(rec);
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				for (Object obj : input_recipe) {
					if (obj instanceof ItemStack)
						stacks.add((ItemStack) obj);
				}
				return stacks;
			} catch (Exception e) {
			}
		}
		for (ItemStack inp1 : inputs) {
			for (ItemStack inp2 : inputs) {
				if (Helper.compareStacksGenerally(inp1, inp2)) {
					
				}
			}
		}
		return inputs;
	}

	public static IRecipe getRecipeFor(ItemStack forWhat) {
		for (Object rec : CraftingManager.getInstance().getRecipeList()) {
			if (Helper.compareStacksGenerallyNoStackSize(forWhat,
					((IRecipe) rec).getRecipeOutput())) {
				return (IRecipe) rec;
			}
		}
		return null;
	}
}
