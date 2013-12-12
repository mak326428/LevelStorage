package makmods.levelstorage.proxy;

import ic2.api.item.Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

public class SimpleRecipeAdder {

	public final static int SIMPLE_ITEM_ID = SimpleItems.instance.itemID;

	public static void add3by3(ItemStack output, ItemStack input) {
		ItemStack ci = input.copy();
		Recipes.advRecipes.addRecipe(output.copy(), "ccc", "ccc", "ccc",
				Character.valueOf('c'), ci);
	}

	public static void addChromeRecipes() {
		System.out.println("adding chrome recipes");
		FurnaceRecipes.smelting().addSmelting(SIMPLE_ITEM_ID,
				SimpleItemShortcut.DUST_CHROME.getMetadata(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		add3by3(SimpleItemShortcut.DUST_CHROME.getItemStack(),
				SimpleItemShortcut.DUST_TINY_CHROME.getItemStack());
		Recipes.macerator.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy()), null,
				SimpleItemShortcut.DUST_CHROME.getItemStack());
		FurnaceRecipes.smelting().addSmelting(SIMPLE_ITEM_ID,
				SimpleItemShortcut.CRUSHED_CHROME_ORE.getMetadata(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		FurnaceRecipes.smelting().addSmelting(SIMPLE_ITEM_ID,
				SimpleItemShortcut.PURIFIED_CHROME_ORE.getMetadata(),
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy(), 10.0F);
		// 3 outputs
		ItemStack oreWashingOutput1 = SimpleItemShortcut.PURIFIED_CHROME_ORE
				.getItemStack().copy();
		ItemStack oreWashingOutput2 = SimpleItemShortcut.DUST_TINY_CHROME
				.getItemStack().copy();
		oreWashingOutput2.stackSize = 2;
		ItemStack oreWashingOutput3 = Items.getItem("stoneDust").copy();
		System.out.println("oreWashingOutput1: " + oreWashingOutput1);
		System.out.println("oreWashingOutput2: " + oreWashingOutput2);
		System.out.println("oreWashingOutput3: " + oreWashingOutput3);
		Recipes.oreWashing.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.CRUSHED_CHROME_ORE.getItemStack()), null,
				oreWashingOutput1, oreWashingOutput2, oreWashingOutput3);
		// 3 outputs
		ItemStack centrifugeOutput1 = SimpleItemShortcut.DUST_CHROME
				.getItemStack().copy();
		ItemStack centrifugeOutput2 = SimpleItemShortcut.DUST_TINY_OSMIUM
				.getItemStack();
		centrifugeOutput2.stackSize = 1;
		ItemStack centrifugeOutput3 = SimpleItemShortcut.TINY_IRIDIUM_DUST
				.getItemStack().copy();
		Recipes.centrifuge.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.PURIFIED_CHROME_ORE.getItemStack()), null,
				centrifugeOutput1, centrifugeOutput2, centrifugeOutput3);
		System.out.println("Past centrifuge");
		ItemStack irBit = SimpleItemShortcut.TINY_IRIDIUM_DUST.getItemStack()
				.copy();
		irBit.stackSize = 9;
		Recipes.compressor.addRecipe(new RecipeInputItemStack(irBit), null,
				Items.getItem("iridiumOre").copy());
		Recipes.metalformerRolling.addRecipe(new RecipeInputItemStack(
				SimpleItemShortcut.INGOT_CHROME.getItemStack().copy()), null,
				SimpleItemShortcut.PLATE_CHROME.getItemStack().copy());
		Recipes.advRecipes.addShapelessRecipe(
				SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(),
				SimpleItemShortcut.ANTIMATTER_GLOB.getItemStack(),
				IC2Items.IRIDIUM_PLATE,
				SimpleItemShortcut.ANTIMATTER_GLOB.getItemStack());
		System.out.println("Past metalformer & advshapelessrecipe adding");
		addScannerRecipe(SimpleItemShortcut.DUST_TINY_OSMIUM.getItemStack(),
				20000, 2000000);
		LogHelper.finest("Successfully complete adding Simple Recipes");
		// Recipes.oreWashing
	}

	public static void addScannerRecipe(ItemStack output, int recordedAmountUU,
			int recordedAmountEU) {
		if (output == null)
			return;
		NBTTagCompound metadata = new NBTTagCompound();

		metadata.setInteger(AMOUNT_UU, recordedAmountUU);
		metadata.setInteger(AMOUNT_EU, recordedAmountEU);

		Recipes.Scanner.addRecipe(new RecipeInputItemStack(output), metadata,
				new ItemStack[0]);
	}

	public static final String AMOUNT_EU = "recordedAmountEU";
	public static final String AMOUNT_UU = "recordedAmountUU";

	public static void addRecipesAfterMUIFinished() {
		ItemStack crushedChromiteOre = SimpleItemShortcut.CRUSHED_CHROME_ORE
				.getItemStack().copy();
		crushedChromiteOre.stackSize = 2;
		Recipes.macerator.addRecipe(new RecipeInputItemStack(new ItemStack(
				LSBlockItemList.blockChromiteOre)), null, crushedChromiteOre);
		addChromeRecipes();
		ItemStack outp = new ItemStack(SimpleItems.instance.itemID, 1, SimpleItemShortcut.IV_GENERATOR_UPGRADE.ordinal());
		Recipes.advRecipes.addShapelessRecipe(
				outp,
				SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(), Items
						.getItem("overclockerUpgrade"), new ItemStack(
						LSBlockItemList.blockMassMelter));
	}

	public static void addSimpleCraftingRecipes() {
		// Osmiridium alloy -> osmiridium plate
		ItemStack rec1 = SimpleItems.instance.getIngredient(2);
		rec1.stackSize = 4;
		Recipes.compressor.addRecipe(new RecipeInputOreDict(
				"itemOsmiridiumAlloy"), null, SimpleItems.instance
				.getIngredient(3));
		// 4 tiny osmium dusts -> 1 dust
		GameRegistry.addRecipe(SimpleItems.instance.getIngredient(1), "SS",
				"SS", Character.valueOf('S'),
				SimpleItems.instance.getIngredient(0));

		// Osmium dust -> osmium ingot
		ItemStack osmIngot = SimpleItems.instance.getIngredient(4);
		ItemStack osmDust = SimpleItems.instance.getIngredient(1);
		FurnaceRecipes.smelting().addSmelting(osmDust.itemID,
				osmDust.getItemDamage(), osmIngot, 20.0F);

		// Osmium Ingots + Iridium Ingots = Osmiridium Alloy
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(2),
				"OOO", "III", "   ", Character.valueOf('O'), "ingotOsmium",
				Character.valueOf('I'), "ingotIridium");
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(2),
				"   ", "OOO", "III", Character.valueOf('O'), "ingotOsmium",
				Character.valueOf('I'), "ingotIridium");

		// Iridium Ore -> Iridium Ingot
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"addIridiumOreToIngotCompressorRecipe", true).getBoolean(true)) {
			try {
				Recipes.compressor.addRecipe(
						new RecipeInputItemStack(Items.getItem("iridiumOre")),
						null, SimpleItems.instance.getIngredient(5));
			} catch (Throwable t) {
				LogHelper
						.warning("Failed to add Iridium ore -> ingot recipe. Fallbacking.");
				t.printStackTrace();
			}
		}

		// UUM -> Osmium pile
		GameRegistry.addRecipe(SimpleItems.instance.getIngredient(0), "U U",
				"UUU", "U U", Character.valueOf('U'), Items.getItem("matter"));

		Recipes.advRecipes.addRecipe(
				OreDictionary.getOres("itemAntimatterTinyPile").get(0), "ppp",
				"ppp", "ppp", Character.valueOf('p'), "itemAntimatterMolecule");
		Recipes.advRecipes.addRecipe(OreDictionary
				.getOres("itemAntimatterGlob").get(0), "ppp", "ppp", "ppp",
				Character.valueOf('p'), "itemAntimatterTinyPile");
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(7)
				.copy(), " a ", "ana", " a ", Character.valueOf('a'),
				SimpleItems.instance.getIngredient(10).copy(), Character
						.valueOf('n'), new ItemStack(Item.netherStar).copy());
		Recipes.advRecipes.addShapelessRecipe(
				SimpleItems.instance.getIngredient(11),
				Items.getItem("overclockerUpgrade"),
				IC2Items.CARBON_PLATE.copy(), IC2Items.CARBON_PLATE.copy(),
				IC2Items.ADV_CIRCUIT.copy());

	}

}
