package makmods.levelstorage.logic.uumsystem;

import ic2.api.item.Items;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.Helper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import cpw.mods.fml.common.FMLLog;

/**
 * Simple UUM recipe parser. Don't call {@link UUMRecipeParser.init}, it's
 * called during mod's postInit phase. You can access parsed recipes via
 * {@linkplain UUMRecipeParser.recipes} (from {@link UUMRecipeParser.instance} singleton instance)
 * 
 * @author mak326428
 * 
 */
public class UUMRecipeParser {

	public static final UUMRecipeParser instance = new UUMRecipeParser();

	// API doesn't provide access to these, I'm doing it the hacky way
	public static final String ADV_RECIPE_CLASS = "ic2.core.AdvRecipe";
	public static final String OUTPUT_FIELD = "output";
	public static final String INPUT_FIELD = "input";
	// Pink blob. Yes, that's it.
	public static final ItemStack UUM = Items.getItem("matter");

	public ArrayList<UUMRecipe> recipes;

	private UUMRecipeParser() {
		recipes = new ArrayList<UUMRecipe>();
	}

	public void init() {
		FMLLog.log(Level.INFO, Reference.MOD_NAME + ": parsing UUM recipes..");
		for (Object rec : CraftingManager.getInstance().getRecipeList()) {
			Class c = rec.getClass();
			if (c.getName() == ADV_RECIPE_CLASS) {
				try {
					Field output_field = c.getField(OUTPUT_FIELD);
					Field inputs_field = c.getField(INPUT_FIELD);
					Object[] inputs = (Object[]) inputs_field.get(rec);
					ItemStack output = (ItemStack) output_field.get(rec);
					boolean onlyUUMRecipe = true;
					int uumInRecipe = 0;
					for (Object i : inputs) {
						if (i instanceof ItemStack) {
							if (Helper.compareStacksGenerally((ItemStack) i,
							        UUM)) {
								uumInRecipe++;
							} else {
								onlyUUMRecipe = false;
								break;
							}
						}
					}
					if (onlyUUMRecipe && (uumInRecipe > 0)) {
						UUMRecipe doneRec = new UUMRecipe(output, uumInRecipe);
						FMLLog.log(Level.INFO, Reference.MOD_NAME
						        + ": UUM Recipe - ");
						FMLLog.log(Level.INFO,
						        "\t UUM Cost: " + doneRec.getUumCost() + " UUM");
						FMLLog.log(
						        Level.INFO,
						        "\t Output: "
						                + Helper.getNiceStackName(doneRec
						                        .getOutput()));
						this.recipes.add(doneRec);
					}

				} catch (Exception e) {
					FMLLog.severe(Reference.MOD_NAME
					        + ": something went wrong when parsing UUM recipes");
					e.printStackTrace();
				}
			}
		}
		FMLLog.log(Level.INFO, Reference.MOD_NAME
		        + ": finished parsing UUM recipes.");
	}

}
