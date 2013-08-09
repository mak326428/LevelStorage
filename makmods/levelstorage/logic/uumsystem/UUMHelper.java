package makmods.levelstorage.logic.uumsystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	public static ArrayList<ItemStack> recursivelyGetIngredients(
			ItemStack forWhat) {
		System.out.println("Crafting - " + Helper.getNiceStackName(forWhat));
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		ItemStack[] st = getRecipeInputsFor(forWhat);
		if (st != null) {
			for (ItemStack stack1 : st) {
				if (stack1 != null) {
					if (!canCraft(stack1))
						stacks.add(stack1);
					if (canCraft(stack1)) {

						// ArrayList<ItemStack> st4 =
						// recursivelyGetIngredients(stack1);
						System.out.println("Can craft "
								+ Helper.getNiceStackName(stack1));
						ItemStack[] st4 = getRecipeInputs(getRecipeFor(stack1));
						System.out.println("st4: " + getRecipeFor(stack1));
						if (st4 != null) {
							System.out.println("st4 lenght" + st4.length);
							for (ItemStack st3 : st4)
								stacks.add(st3);
						} else {
							stacks.add(stack1);
						}
					}

				}
			}
		} else {
			stacks.add(forWhat);
		}
		return stacks;
	}

	public static ItemStack[] getRecipeInputsFor(ItemStack stack) {
		return getRecipeInputs(getRecipeFor(stack));
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
		return getRecipeInputsFor(what) == null;
	}

	public static <T> ArrayList<T> convertArrayIntoArrList(T[] arr) {
		ArrayList<T> arrList = new ArrayList<T>();
		for (T entry : arr) {
			arrList.add(entry);
		}
		return arrList;
	}

	public static ItemStack[] getRecipeInputs(IRecipe rec) {
		System.out.println(rec);
		if (rec == null)
			return null;
		ItemStack[] inputs = null;
		if (rec instanceof ShapedRecipes) {
			inputs = ((ShapedRecipes) rec).recipeItems;
			System.out.println("ShapedRecipes set.");
		}
		if (rec instanceof ShapelessRecipes) {
			List l = ((ShapelessRecipes) rec).recipeItems;
			inputs = (ItemStack[]) l.toArray(new ItemStack[l.size()]);
			System.out.println("ShapelessRecipes set.");
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
				inputs = (ItemStack[]) stacks.toArray(new ItemStack[stacks
						.size()]);
				System.out.println("AdvRecipes set.");
			} catch (Exception e) {
			}
		}
		// for (ItemStack is : inputs) {
		// if (is == null)
		// inputs.
		// }
		return inputs;
	}

	public static IRecipe getRecipeFor(ItemStack forWhat) {
		for (Object rec : CraftingManager.getInstance().getRecipeList()) {
			ItemStack outp = ((IRecipe) rec).getRecipeOutput();
			if (forWhat != null && outp != null) {
				if (forWhat.itemID == outp.itemID) {
					return (IRecipe) rec;
				}
			}
		}
		return null;
	}
}
