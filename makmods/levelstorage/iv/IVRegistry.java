package makmods.levelstorage.iv;

import ic2.api.item.Items;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.iv.parsers.IRecipeParser;
import makmods.levelstorage.iv.parsers.IVRecipeParser;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.Loader;

/**
 * The heart of LevelStorage's IV system
 * 
 * @author mak326428
 * 
 */
public class IVRegistry {
	public static final IVRegistry instance = new IVRegistry();
	public static final int NOT_FOUND = -1;
	public List<IVItemStackEntry> itemStackEntries = Lists.newArrayList();
	public List<IVOreDictEntry> oreDictEntries = Lists.newArrayList();
	public static final boolean DEBUG = true;

	/**
	 * Key - IV <br />
	 * Value - Fluid in mB
	 */
	public static final AbstractMap.SimpleEntry<Integer, Integer> IV_TO_FLUID_CONVERSION = new SimpleEntry<Integer, Integer>(
			10, 1);

	public static final String IV_CATEGORY = "iv";

	public static Map<String, ItemStack> nameToItemStackMap = Maps.newHashMap();
	public static Map<ItemStack, String> itemStackToNameMap = Maps.newHashMap();

	public void recognizeOreDict() {
		try {
			String[] names = OreDictionary.getOreNames();
			for (String s : names) {
				try {
					if (DEBUG)
						LogHelper.info("OreDict entry: " + s);
					ArrayList<ItemStack> stacksForGiven = OreDictionary
							.getOres(s);
					for (ItemStack st : stacksForGiven) {
						if (st == null)
							continue;

						nameToItemStackMap.put(s, st);
						itemStackToNameMap.put(st, s);
					}

				} catch (Exception e) {
					LogHelper
							.severe("Exception when trying to dynamically allocate more ores to generate");
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			LogHelper
					.severe("Exception when trying to dynamically allocate more ores to generate");
			e.printStackTrace();
		}
	}

	/**
	 * Used to prevent CMEs.
	 * 
	 * @return Exact copy of entries (mutable)
	 */
	public List<IVEntry> copyRegistry() {
		List<IVEntry> newList = Lists.newArrayList();
		for (IVOreDictEntry entry : oreDictEntries)
			newList.add(entry);
		for (IVItemStackEntry entry : itemStackEntries)
			newList.add(entry);
		return newList;
	}

	private IVRegistry() {
		;
	}

	public void init() {
		DO_CACHING = true;
		initCriticalNodes();
		recognizeOreDict();
		parseDynamically();
		printContents();
	}

	public void runParser(IRecipeParser parser) {
		long startTime = System.currentTimeMillis();
		parser.parse();
		LogHelper.info(parser.getClass().getSimpleName() + " took "
				+ (System.currentTimeMillis() - startTime) + "ms.");
	}

	public void parseDynamically() {
		runParser(new IVRecipeParser());

	}

	public void printContents() {
		LogHelper.info("Printing IVRegistry contents:");
		List<IVEntry> entries = copyRegistry();
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
		assign(new ItemStack(Item.diamond), 8192);
		assign(new ItemStack(Block.oreCoal), 128);
		assign(new ItemStack(Item.coal), 128);
		assign(new ItemStack(Item.coal, 1, 1), 32);
		assign(new ItemStack(Block.wood, 1,
				OreDictionary.WILDCARD_VALUE), 32);
		assign(new ItemStack(Item.ingotGold), 2048);
		assign(new ItemStack(Item.redstone), 64);
		assign(new ItemStack(Block.stone), 1);
		assign(new ItemStack(Block.dirt), 1);
		assign(new ItemStack(Block.sand), 1);
		assign(new ItemStack(Item.ingotIron), 256);
		assign(new ItemStack(Item.clay), 16);
		assign(new ItemStack(Item.wheat), 32);
		assign(new ItemStack(Item.silk), 12);
		assign(new ItemStack(Block.obsidian), 64);
		assign(new ItemStack(Item.enderPearl), 1024);
		assign(new ItemStack(Item.blazeRod), 1536);
		assign(new ItemStack(Item.carrot), 64);
		assign(new ItemStack(Item.potato), 64);
		assign(new ItemStack(Item.glowstone), 384);
		assign(new ItemStack(Item.dyePowder, 1, 4), 768);
		assign(new ItemStack(Item.leather), 64);
		assign(new ItemStack(Item.emerald), 8192);
		assign(new ItemStack(Item.feather), 48);
		assign(new ItemStack(Block.ice), 1);
		assign(new ItemStack(Block.dragonEgg), 2000000);
		assign(new ItemStack(Item.melon), 16);
		assign(new ItemStack(Item.netherQuartz), 24);
		assign(new ItemStack(Item.saddle), 256);
		assign(new ItemStack(Item.netherStar), 524288);
		assign(Items.getItem("iridiumOre").copy(), 131072);
		assign(Items.getItem("resin").copy(), 24);
		assign(new ItemStack(Item.skull, 1, 1), 87381);
		assign(new ItemStack(Item.reed), 24);
		assign(new ItemStack(Block.slowSand), 49);
		assign(new ItemStack(Block.whiteStone), 4);
		assign(new ItemStack(Item.gunpowder), 192);
		assign(new ItemStack(Block.cobblestone), 1);
		assign(new ItemStack(Block.netherrack), 1);
		assign(Items.getItem("smallUran235"), 1024);
		assign(Items.getItem("Uran238"), 204);
		if (LevelStorage.configuration.get(LevelStorage.BALANCE_CATEGORY,
				"disableBlazeRodToPowderExploit", true).getBoolean(true))
			assign(new ItemStack(Item.blazePowder), 307);
		assign(new ItemStack(Item.flint), 4);
		assign(new ItemStack(Block.gravel), 4);
		assign(new ItemStack(Item.ghastTear), 4096);
		assign(new ItemStack(Item.rottenFlesh), 32);
		assign(new ItemStack(Block.plantYellow), 16);
		assign(new ItemStack(Block.plantRed), 16);
		assignAll(ItemRecord.class, 16384);
		// assign(Item.arrow, 16);
		assign("ingotTin", 255);
		assign("crystalCertusQuartz", 1024);
		assign("gemCertusQuartz", 1024);
		assign("ingotChrome", 12288);
		assign("ingotSilver", 512);
		assign("ingotCopper", 85);
		assign("ingotBronze", 170);
		if (Loader.isModLoaded("AdvancedSolarPanel"))
			IVCrossMod.addASPValues();
	}
	
	public void assign(Object obj, int value) {
		if (obj instanceof ItemStack)
			assignItemStack((ItemStack)obj, value);
		else if (obj instanceof String)
			assignOreDictionary((String)obj, value);
		else if (obj instanceof Item)
			assignItemStack(new ItemStack((Item)obj), value);
		else if (obj instanceof Block)
			assignItemStack(new ItemStack((Block)obj), value);
	}

	public void assign(Block item, int value) {
		assignItemStack(new ItemStack(item), value);
	}

	public void assignAll(Class<?> baseClass, int value) {
		for (Block b : Block.blocksList) {
			if (b != null) {
				if (baseClass.isAssignableFrom(b.getClass()))
					assignItemStack(new ItemStack(b), value);
			}
		}
		for (Item b : Item.itemsList) {
			if (b != null) {
				if (baseClass.isAssignableFrom(b.getClass()))
					assignItemStack(new ItemStack(b), value);
			}
		}
	}

	public void assignItemStack(ItemStack stack, int value) {
		int id = stack.itemID;
		int meta = stack.getItemDamage();
		int valueActual = LevelStorage.configuration.get(
				IV_CATEGORY,
				id + ":" + meta,
				value,
				stack.hasDisplayName() ? stack.getDisplayName().replace(
						".name", "") : stack.getUnlocalizedName()).getInt();
		if (valueActual > 0)
			itemStackEntries.add(new IVItemStackEntry(stack.copy(), value));
	}

	public void removeIV(Object obj) {
		Object toRemove = null;
		if (obj instanceof String) {
			String initialName = (String) obj;
			for (IVOreDictEntry entry : oreDictEntries) {
				if (entry.getName().equals(initialName)) {
					toRemove = entry;
					break;
				}
			}
		} else if (obj instanceof ItemStack) {
			ItemStack initialStack = (ItemStack) obj;
			for (IVItemStackEntry entry : itemStackEntries) {
				ItemStack entryStack = entry.getStack();
				if (entryStack.itemID == initialStack.itemID
						&& (entryStack.getItemDamage() == initialStack
								.getItemDamage() || entryStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
					toRemove = entry;
					break;
				}
			}
		}
		if (toRemove != null) {
			if (toRemove instanceof IVOreDictEntry)
				oreDictEntries.remove(toRemove);
			else
				itemStackEntries.remove(toRemove);
			IVRegistry.clearCache();
		} else
			LogHelper.severe("removeIV: obj's type is incorrect - "
					+ obj.getClass().getSimpleName());
	}

	public void assignItemStack_dynamic(ItemStack stack, int value) {
		itemStackEntries.add(new IVItemStackEntry(stack.copy(), value));
	}

	public void assignOreDict_dynamic(String name, int value) {
		oreDictEntries.add(new IVOreDictEntry(name, value));
	}

	public void assignOreDictionary(String name, int value) {
		int valueActual = LevelStorage.configuration.get(IV_CATEGORY, name,
				value).getInt();
		if (valueActual > 0) {
			List<ItemStack> sts = OreDictionary.getOres(name);
			for (ItemStack st : sts)
				assignItemStack(st.copy(), value);
			oreDictEntries.add(new IVOreDictEntry(name, value));
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

	public static boolean DO_CACHING = false;

	private static Map<List<Integer>, Integer> itemStackCache = Maps
			.newHashMap();
	private static Map<String, Integer> oreDictCache = Maps.newHashMap();

	public static void clearCache() {
		if (DEBUG) {
			LogHelper.info(String.format(
					"Clearing IV cache, %d ItemStack, %d OreDict",
					itemStackCache.size(), oreDictCache.size()));
		}
		itemStackCache.clear();
		oreDictCache.clear();
	}

	public int getValueFor(Object obj) {
		if (!DO_CACHING) {
			if (obj instanceof String)
				return getValueFor_internal((String) obj);
			else if (obj instanceof ItemStack)
				return getValueFor_internal((ItemStack) obj);
		}
		if (obj instanceof String) {
			String odName = (String) obj;
			if (!oreDictCache.containsKey(odName))
				oreDictCache.put(odName, getValueFor_internal(odName));
			return oreDictCache.get(odName);
		} else if (obj instanceof ItemStack) {
			List<Integer> lst = Lists.newArrayList();
			ItemStack objIS = (ItemStack) obj;
			lst.add(objIS.itemID);
			lst.add(objIS.getItemDamage());
			if (!itemStackCache.containsKey(lst))
				itemStackCache.put(lst, getValueFor_internal(objIS));
			return itemStackCache.get(lst);
		} else
			return NOT_FOUND;
	}

	public int getValueFor_internal(String name) {
		for (IVOreDictEntry oreDictEntry : oreDictEntries) {
			if (oreDictEntry.getName().equals(name))
				return oreDictEntry.getValue();
			if (!nameToItemStackMap.containsKey(name))
				return NOT_FOUND;
			ItemStack toResolveIS = nameToItemStackMap.get(name);
			// System.out.println(toResolveIS);
			for (IVItemStackEntry ivItemStackEntry : itemStackEntries) {
				ItemStack stack = ivItemStackEntry.getStack();

				if (stack.itemID == toResolveIS.itemID
						&& (stack.getItemDamage() == toResolveIS
								.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE))
					return ivItemStackEntry.getValue();
			}
		}
		return NOT_FOUND;
	}

	public int getValueFor_internal(ItemStack objStack) {
		if (objStack == null)
			return NOT_FOUND;
		for (IVItemStackEntry itemStackEntry : itemStackEntries) {
			ItemStack iterationIS = itemStackEntry.getStack();
			if (objStack.itemID == iterationIS.itemID
					&& (iterationIS.getItemDamage() == objStack.getItemDamage() || iterationIS
							.getItemDamage() == OreDictionary.WILDCARD_VALUE))
				return itemStackEntry.getValue();
			String toResolveIS = null;
			for (Entry<ItemStack, String> entryIs : itemStackToNameMap
					.entrySet()) {
				ItemStack st = entryIs.getKey();
				if (st.itemID == objStack.itemID
						&& (st.getItemDamage() == objStack.getItemDamage() || st
								.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
					toResolveIS = entryIs.getValue();
					break;
				}
			}

			for (IVOreDictEntry oreDictEntry : oreDictEntries) {
				if (oreDictEntry.getName().equals(toResolveIS))
					return oreDictEntry.getValue();

			}
		}
		return NOT_FOUND;
	}
}
