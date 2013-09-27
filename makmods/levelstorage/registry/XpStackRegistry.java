package makmods.levelstorage.registry;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.logging.Level;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;

public class XPStackRegistry {

	public static final XPStackRegistry instance = new XPStackRegistry();

	private XPStackRegistry() {
	}

	public ArrayList<XPStack> entries = new ArrayList<XPStack>();
	public static final String XP_REGISTRY_CATEGORY = "xpRegistry";

	public static final AbstractMap.SimpleEntry<Integer, Integer> XP_EU_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
			1, 256); // 1 XP = 256 EU
	public static final AbstractMap.SimpleEntry<Integer, Integer> UUM_XP_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
			1, 130); // 1 UUM = 130 XP

	public static final int ORE_DICT_NOT_FOUND = -1;

	public static ArrayList<ISimpleRecipeParser> parsers = Lists.newArrayList();

	static {
		parsers.add(new CraftingRecipesParser());
		parsers.add(new FurnaceRecipesParser());
		parsers.add(new IC2MachineRecipeParser(Recipes.compressor));
		parsers.add(new IC2MachineRecipeParser(Recipes.extractor));
		parsers.add(new IC2MachineRecipeParser(Recipes.macerator));
	}

	public void initCriticalNodes() {
		if (LevelStorage.configuration.get(LevelStorage.BALANCE_CATEGORY,
				"addCopperTinToBronzeCraftingRecipe", true).getBoolean(true)) {
			ItemStack bronze = OreDictionary.getOres("ingotBronze").get(0)
					.copy();
			bronze.stackSize = 4;
			Recipes.advRecipes.addRecipe(bronze, "cc", "ct",
					Character.valueOf('c'), "ingotCopper",
					Character.valueOf('t'), "ingotTin");
		}
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(Item.redstone),
				8));
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(
				Item.netherQuartz), 4));
		this.pushToRegistryWithConfig(new XPStack(
				new ItemStack(Item.ingotGold), 16));
		this.pushToRegistryWithConfig(new XPStack(
				new ItemStack(Item.enderPearl), 256));
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(Block.bedrock),
				Integer.MAX_VALUE));
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(Item.arrow), 1));
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(Item.bow), 9));
		this.pushToRegistryWithConfig(new XPStack(Items.getItem("resin"), 3));
		this.pushOreToRegistry("ingotCopper", 5);
		this.pushOreToRegistry("ingotTin", 6);
		this.pushOreToRegistry("oreIron", 6);

		this.pushToRegistryWithConfig(new XPStack(
				new ItemStack(Item.glowstone), 32));
		this.pushToRegistryWithConfig(new XPStack(new ItemStack(Item.diamond),
				512));
		this.pushToRegistryWithConfig(new XPStack(
				new ItemStack(Item.netherStar), 4096));
		this.pushToRegistryWithConfig(new XPStack(
				new ItemStack(Block.obsidian), 8));

		// dummy for debug
		// this.pushOreToRegistry("ingotGold", 1);

		if (LevelStorage.detectedGT) {
			this.pushOreToRegistry("dustDiamond", 512);
			this.pushOreToRegistry("dustTinyDiamond", 128);
			this.pushOreToRegistry("dustPlatinum", 2048);
			this.pushOreToRegistry("dustTinyPlatinum", 512);
			this.pushOreToRegistry("dustPlutonium", 1536);
			this.pushOreToRegistry("dustTinyPlutonium", 1536 / 4);
		}

		for (ISimpleRecipeParser parser : parsers)
			parser.parse();
		// two should suffice
		// if not, we'll add more
		while (true) {
			int parsed = 0;
			for (ISimpleRecipeParser parser : parsers)
				parsed += parser.parse();
			if (parsed == 0)
				break;
		}

		while (true) {
			int parsed = 0;
			for (ISimpleRecipeParser parser : parsers)
				parsed += parser.parse();
			if (parsed == 0)
				break;
		}

		// this.pushOreToRegistry("dustDiamond", 512);
	}

	public boolean containsStack(ItemStack stack) {
		// NO CME
		XPStack[] stacks = (XPStack[]) XPStackRegistry.instance.entries
				.toArray(new XPStack[XPStackRegistry.instance.entries.size()]);
		for (XPStack xpstack : stacks) {
			if (xpstack.stack.itemID == stack.itemID
					&& xpstack.stack.getItemDamage() == stack.getItemDamage())
				return true;
		}
		return false;
	}

	public int getStackValue(ItemStack stack) {
		if (!containsStack(stack))
			return 0;
		XPStack[] stacks = (XPStack[]) XPStackRegistry.instance.entries
				.toArray(new XPStack[XPStackRegistry.instance.entries.size()]);
		for (XPStack xpstack : stacks) {
			if (xpstack.stack.itemID == stack.itemID
					&& xpstack.stack.getItemDamage() == stack.getItemDamage())
				return xpstack.value;
		}
		return 0;
	}

	/**
	 * Very sneaky, right? :P
	 */

	public void printRegistry() {
		LogHelper.info("Starting printing the xp registry contents");
		for (XPStack s : this.entries) {
			LogHelper.info("\t#" + s.stack.itemID + ":"
					+ s.stack.getItemDamage() + " - "
					+ s.stack.getDisplayName() + " - " + s.value + " (1 "
					+ s.stack.getDisplayName() + " = " + s.value + " XP)");
		}
	}

	public void pushToRegistry(XPStack stack) {
		FMLLog.log(Level.INFO, "Adding #" + stack.stack.itemID + ":"
				+ stack.stack.getItemDamage() + " to the Xp Registry, value: "
				+ stack.value);
		this.entries.add(stack);
	}

	public void pushToRegistryWithConfig(XPStack stack) {
		Property property = LevelStorage.configuration
				.get(XP_REGISTRY_CATEGORY,
						stack.stack.getItem().getUnlocalizedName()
								.replace("item.", "").replace(".name", "")
								.replace("tile.", ""), stack.value);
		property.comment = "Set to -1 to disable";
		int value = property.getInt();
		if (value == -1) {
			LogHelper.warning("XP entry for item "
					+ CommonHelper.getNiceStackName(stack.stack) + " is disabled");
			return;
		}
		FMLLog.log(Level.INFO, "Adding #" + stack.stack.itemID + ":"
				+ stack.stack.getItemDamage() + " to the Xp Registry, value: "
				+ stack.value);
		this.entries.add(new XPStack(stack.stack, value));
	}

	public void pushOreToRegistry(String name, int value) {
		boolean exists = false;
		for (ItemStack stack : OreDictionary.getOres(name)) {
			this.pushToRegistry(new XPStack(stack, value));
			exists = true;
		}
		if (!exists) {
			FMLLog.warning(Reference.MOD_NAME
					+ ": failed to add ore to XP Registry - " + name);
		}
	}
}
