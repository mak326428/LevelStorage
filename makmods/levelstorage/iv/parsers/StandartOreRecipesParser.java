package makmods.levelstorage.iv.parsers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.collect.Lists;

public class StandartOreRecipesParser implements IRecipeParser {

	public List<OreRecipeWrapper> recipes = null;

	public List<OreRecipeWrapper> getRecipes() {
		List<OreRecipeWrapper> r = Lists.newArrayList();
		List allTheRecipes = CraftingManager.getInstance().getRecipeList();
		for (Object obj : allTheRecipes) {
			try {
				if (obj instanceof ShapedOreRecipe) {
					ShapedOreRecipe sor = (ShapedOreRecipe) obj;
					r.add(new OreRecipeWrapper(sor.getRecipeOutput(), sor
							.getInput()));
				} else if (obj instanceof ShapelessOreRecipe) {
					ShapelessOreRecipe sor = (ShapelessOreRecipe) obj;
					ArrayList al = sor.getInput();
					ItemStack output = sor.getRecipeOutput();
					Object[] inputs = new Object[al.size()];
					for (int i = 0; i < al.size(); i++)
						inputs[i] = al.get(i);
					r.add(new OreRecipeWrapper(output.copy(), inputs));
				}
			} catch (Throwable t) {
				LogHelper.warning("Exception parsing recipe!");
				t.printStackTrace();
			}
		}
		return r;
	}

	public static class OreRecipeWrapper {
		private ItemStack output;
		private Object[] inputs;

		public OreRecipeWrapper(ItemStack outp, Object[] inp) {
			inputs = inp;
			output = outp;
		}

		public ItemStack getOutput() {
			return output;
		}

		public void setOutput(ItemStack output) {
			this.output = output;
		}

		public Object[] getInputs() {
			return inputs;
		}

		public void setInputs(Object[] inputs) {
			this.inputs = inputs;
		}
	}

	@Override
	public String getName() {
		return "Ore Recipes Parser";
	}

	@Override
	public int parse() {
		if (recipes == null)
			recipes = getRecipes();
		int parsed = 0;
		for (OreRecipeWrapper recipe : recipes) {
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

}
