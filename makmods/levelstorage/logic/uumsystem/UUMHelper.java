package makmods.levelstorage.logic.uumsystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UUMHelper {

	public static boolean canCraftWithUUM(ItemStack what) {
		if (!canCraft(what))
			return false;
		return false;
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
		return getRecipeInputsFor(what) != null;
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
		}
		if (rec instanceof ShapelessRecipes) {
			List l = ((ShapelessRecipes) rec).recipeItems;
			inputs = (ItemStack[]) l.toArray(new ItemStack[l.size()]);
		}
		if (rec instanceof ShapedOreRecipe) {
			Object[] currI = ((ShapedOreRecipe) rec).getInput();
			ItemStack[] rrec = new ItemStack[currI.length];
			for (int i = 0; i < currI.length; i++) {
				if (currI[i] instanceof ItemStack)
					rrec[i] = (ItemStack) currI[i];
			}
			inputs = rrec;
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
