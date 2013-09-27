package makmods.levelstorage.iv.parsers;

import ic2.api.item.Items;

import java.util.List;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class AdvRecipeParser implements IRecipeParser {

	public List<AdvRecipeWrapper> advRecipes = null;

	@Override
	public String getName() {
		return "IC2's Advanced Recipes Parser";
	}

	@Override
	public int parse() {
		if (advRecipes == null)
			advRecipes = getAdvRecipes();
		int parsed = 0;
		for (AdvRecipeWrapper recipe : advRecipes) {
			if (recipe == null)
				continue;

			boolean hasUnknown = false;
			int value = 0;

			for (Object obj : recipe.getInputs()) {
				if (obj == null)
					continue;
				int valueForObj = IVRegistry.instance.getValueFor(obj);
				if (valueForObj == IVRegistry.NOT_FOUND) {
					hasUnknown = true;
					break;
				}
				value += valueForObj;
			}
			// TODO: investigate why insulated cable recipes are missing
			// if (recipe.getOutput().getItem().getClass().getSimpleName()
			// .toLowerCase().contains("cable")) {
			// System.out.println(LanguageRegistry.instance()
			// .getStringLocalization(
			// recipe.getOutput().getDisplayName()));
			// System.out.println("hasUnknown: " + hasUnknown);
			// System.out.println("value: " + value);
			//
			// }
			if (!hasUnknown) {
				if (value > 0) {
					ItemStack outp = recipe.getOutput().copy();
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

	// GETTING ZONE
	public static class AdvRecipeWrapper {
		public Object[] getInputs() {
			return inputs;
		}

		public void setInputs(Object[] inputs) {
			this.inputs = inputs;
		}

		public ItemStack getOutput() {
			return output;
		}

		public void setOutput(ItemStack output) {
			this.output = output;
		}

		private Object[] inputs;
		private ItemStack output;

		public AdvRecipeWrapper(Object[] input, ItemStack out) {
			inputs = input;
			output = out;
		}
	}

	public static final String ADV_RECIPE_CLASSNAME = "ic2.core.AdvRecipe";
	public static final String ADV_SHAPELESS_RECIPE_CLASSNAME = "ic2.core.AdvShapelessRecipe";
	public static final String OUTPUT_FIELD = "output";
	public static final String INPUT_FIELD = "input";

	public List<AdvRecipeWrapper> getAdvRecipes() {
		List<AdvRecipeWrapper> list = Lists.newArrayList();
		List allRecipes = CraftingManager.getInstance().getRecipeList();
		for (Object obj : allRecipes) {
			try {
				if (obj == null)
					continue;
				Class clazz = obj.getClass();
				if (clazz.getCanonicalName().equals(ADV_RECIPE_CLASSNAME)
						|| clazz.getCanonicalName().equals(
								ADV_SHAPELESS_RECIPE_CLASSNAME)) {
					try {
						Object[] inputs = (Object[]) clazz
								.getField(INPUT_FIELD).get(obj);
						ItemStack output = (ItemStack) clazz.getField(
								OUTPUT_FIELD).get(obj);
						list.add(new AdvRecipeWrapper(inputs, output));
					} catch (Throwable t) {
						LogHelper.warning("Exception parsing AdvRecipe");
						t.printStackTrace();
					}
				}
			} catch (Throwable t) {
				LogHelper.warning("Exception getting AdvRecipe list.");
				t.printStackTrace();
			}
		}
		return list;
	}
}
