package makmods.levelstorage.iv;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import makmods.levelstorage.iv.parsers.AdvRecipeParser;
import makmods.levelstorage.iv.parsers.CraftingRecipeParser;
import makmods.levelstorage.iv.parsers.IC2MachineRecipeParser;
import makmods.levelstorage.iv.parsers.IRecipeParser;
import makmods.levelstorage.iv.parsers.SmeltingRecipeParser;
import makmods.levelstorage.iv.parsers.StandartOreRecipesParser;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

public class IVRegistry {
	public static final IVRegistry instance = new IVRegistry();
	public static final int NOT_FOUND = -1;
	public List<IVEntry> entries = Lists.newArrayList();

	/**
	 * Key - IV <br />
	 * Value - Fluid in mB
	 */
	public static final AbstractMap.SimpleEntry<Integer, Integer> IV_TO_FLUID_CONVERSION = new SimpleEntry<Integer, Integer>(
			10, 1);

	private IRecipeParser parsers[] = {
			new CraftingRecipeParser(),
			new StandartOreRecipesParser(),
			new IC2MachineRecipeParser(Recipes.compressor),
			new IC2MachineRecipeParser(Recipes.macerator),
			new IC2MachineRecipeParser(Recipes.extractor),
			new IC2MachineRecipeParser(Recipes.metalformerCutting),
			new IC2MachineRecipeParser(Recipes.metalformerExtruding),
			new IC2MachineRecipeParser(Recipes.metalformerRolling),
			new SmeltingRecipeParser(),
			new AdvRecipeParser(),
			// extraordinary copy-past to allow wider scanning
			new CraftingRecipeParser(), new StandartOreRecipesParser(),
			new IC2MachineRecipeParser(Recipes.compressor),
			new IC2MachineRecipeParser(Recipes.macerator),
			new IC2MachineRecipeParser(Recipes.extractor),
			new IC2MachineRecipeParser(Recipes.metalformerCutting),
			new IC2MachineRecipeParser(Recipes.metalformerExtruding),
			new IC2MachineRecipeParser(Recipes.metalformerRolling),
			new SmeltingRecipeParser(), new AdvRecipeParser(), };

	/**
	 * Used to prevent CMEs.
	 * 
	 * @return Exact copy of entries (mutable)
	 */
	public List<IVEntry> copyRegistry() {
		List<IVEntry> newList = Lists.newArrayList();
		for (IVEntry entry : entries)
			newList.add(entry);
		return newList;
	}

	private IVRegistry() {
		;
	}

	public void init() {
		initCriticalNodes();
		parseDynamically();
		printContents();
	}

	public void printContents() {
		LogHelper.info("Printing IVRegistry contents:");
		for (IVEntry entry : entries) {
			if (entry instanceof IVOreDictEntry) {
				IVOreDictEntry ode = (IVOreDictEntry) entry;
				LogHelper.info("\tOre Dictionary: " + ode.getName()
						+ ", value: " + ode.getValue());
			} else if (entry instanceof IVItemStackEntry) {
				IVItemStackEntry ise = (IVItemStackEntry) entry;
				LogHelper.info("\tItemStack: "
						+ ise.getStack().getDisplayName() + ", value: "
						+ ise.getValue());
			}
		}
	}

	private void initCriticalNodes() {
		// TODO: add parsing for standart oredict recipes
		registerIS(new ItemStack(Item.diamond), 8192);
		registerIS(new ItemStack(Block.oreCoal), 128);
		registerIS(new ItemStack(Item.coal), 128);
		registerIS(new ItemStack(Item.coal, 1, 1), 64);
		registerIS(new ItemStack(Block.wood, 1, OreDictionary.WILDCARD_VALUE),
				64);
		registerIS(new ItemStack(Item.ingotGold), 2048);
		registerIS(new ItemStack(Item.redstone), 64);
		registerIS(new ItemStack(Block.cobblestone), 1);
		registerIS(new ItemStack(Block.dirt), 1);
		registerIS(new ItemStack(Block.sand), 1);
		registerIS(new ItemStack(Item.ingotIron), 256);
		registerIS(new ItemStack(Item.clay), 16);
		registerIS(new ItemStack(Item.wheat), 32);
		registerIS(new ItemStack(Item.silk), 12);
		registerIS(new ItemStack(Block.obsidian), 64);
		registerIS(new ItemStack(Item.enderPearl), 1024);
		registerIS(new ItemStack(Item.blazeRod), 1536);
		registerIS(new ItemStack(Item.carrot), 64);
		registerIS(new ItemStack(Item.potato), 64);
		registerIS(new ItemStack(Item.glowstone), 384);
		registerIS(new ItemStack(Item.dyePowder, 1, 4), 768);
		registerIS(new ItemStack(Item.leather), 64);
		registerIS(new ItemStack(Item.emerald), 8192);
		registerIS(new ItemStack(Item.stick), 4);
		registerIS(new ItemStack(Block.pistonBase), 368);
		registerIS(new ItemStack(Item.feather), 48);
		registerIS(new ItemStack(Block.ice), 1);
		registerIS(new ItemStack(Block.dragonEgg), 2000000);
		registerIS(new ItemStack(Item.melon), 16);
		registerIS(new ItemStack(Item.netherQuartz), 24);
		registerIS(new ItemStack(Item.saddle), 256);

		registerIS(Items.getItem("iridiumOre").copy(), 131072);
		registerIS(Items.getItem("resin").copy(), 24);
		// registerIS(SimpleItemShortcut.DUST_CHROME.getItemStack().copy(), 8192
		// * 12);
		registerOreDict("ingotChrome", 8192 * 12);
		registerOreDict("ingotCopper", 85);
		registerOreDict("ingotTin", 255);
		registerOreDict("ingotBronze", 170);
	}

	public void registerIS(ItemStack stack, int value) {
		entries.add(new IVItemStackEntry(stack.copy(), value));
	}

	public void registerOreDict(String name, int value) {
		List<ItemStack> sts = OreDictionary.getOres(name);
		for (ItemStack st : sts)
			registerIS(st.copy(), value);
		entries.add(new IVOreDictEntry(name, value));
	}

	private void parseDynamically() {
		// This will cover everything.. it should
		for (int i = 0; i < parsers.length * 2; i++) {
			for (IRecipeParser parser : parsers)
				parser.parse();
			while (true) {
				int parsed = 0;
				for (IRecipeParser parser : parsers)
					parsed += parser.parse();
				if (parsed == 0)
					break;
			}
			while (true) {
				int parsed = 0;
				for (IRecipeParser parser : parsers)
					parsed += parser.parse();
				if (parsed == 0)
					break;
			}
		}
	}

	/**
	 * Respects stack size
	 * 
	 * @param st
	 *            ItemStack
	 * @return IV for the requested item
	 */
	public int getValueForItemStack(ItemStack st) {
		if (st == null)
			return NOT_FOUND;
		int baseValue = getValueFor(st);
		if (baseValue == NOT_FOUND)
			return NOT_FOUND;
		return baseValue * st.stackSize;
	}
	
	public static boolean hasValue(Object obj) {
		return getValue(obj) != NOT_FOUND;
	}
	
	public static int getValue(Object obj) {
		return instance.getValueFor(obj);
	}

	/**
	 * Gets IV for requested object
	 * 
	 * @param obj
	 *            Requested. Might be String (OreDict name) or ItemStack
	 * @return IV
	 */
	public int getValueFor(Object obj) {
		if (obj == null)
			return NOT_FOUND;
		// TODO: oredict resolving, if ItemStack
		if (obj instanceof String) {
			for (IVEntry entry : entries)
				if (entry instanceof IVOreDictEntry)
					if (((IVOreDictEntry) entry).getName().equals((String) obj))
						return entry.getValue();
		} else if (obj instanceof ItemStack) {
			for (IVEntry entry : entries)
				if (entry instanceof IVItemStackEntry) {
					IVItemStackEntry ivise = (IVItemStackEntry) entry;
					ItemStack stack = (ItemStack) obj;
					if (ivise.getStack().itemID == stack.itemID
							&& (ivise.getStack().getItemDamage() == stack
									.getItemDamage() || ivise.getStack()
									.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
						return entry.getValue();
					}
				}
		}
		if (obj instanceof ItemStack) {
			int id = OreDictionary.getOreID((ItemStack) obj);
			if (id != -1) {
				String name = OreDictionary.getOreName(id);
				for (IVEntry entry : entries) {
					if (entry instanceof IVOreDictEntry)
						if (((IVOreDictEntry) entry).getName().equals(name))
							return entry.getValue();
				}
			}
		}
		return NOT_FOUND;
	}
}
