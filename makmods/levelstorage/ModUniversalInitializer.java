package makmods.levelstorage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Level;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Initializes all the items and blocks in the {@link LSBlockItemList}. Invoked
 * during init-phase.
 * 
 * @author mak326428
 * 
 */
public class ModUniversalInitializer {

	public static final ModUniversalInitializer instance = new ModUniversalInitializer();

	public static final String NAME = "NAME";
	public static final String BLOCK_PREFIX = "Block";
	public static final String ITEM_PREFIX = "Item";

	private ModUniversalInitializer() {
		;
	}

	// TODO: finish up stuff here
	// because everything seems broken
	public void create(Field f) {
		LogHelper.info("Initializing block/item: " + f.getName());
		try {
			Class c = f.getType();
			int id = 0;
			if (c.getSimpleName().startsWith(BLOCK_PREFIX))
				id = LevelStorage.configuration.getBlock(f.getName(),
				        LevelStorage.getAndIncrementCurrId()).getInt();
			else if (c.getSimpleName().startsWith(ITEM_PREFIX))
				id = LevelStorage.configuration.getItem(f.getName(),
				        LevelStorage.getAndIncrementCurrId()).getInt();
			else
				LogHelper
				        .severe("object is neither item nor block. This is a bug!");
			Constructor con = c.getConstructor(int.class);
			f.set(null, con.newInstance(id));
		} catch (ClassCastException e) {
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, Reference.MOD_NAME
			        + ": failed to initialize block/item");
			e.printStackTrace();
		}
	}

	private void registerBlock(Field f) {
		try {
			Object obj = f.get(null);
			if (obj instanceof Block) {
				LogHelper.info("Registering block " + f.getName()
				        + " in GameRegistry");
				Block currBlock = (Block) obj;
				String name = (String) currBlock.getClass()
				        .getField("UNLOCALIZED_NAME").get(null);
				GameRegistry.registerBlock(currBlock, name);
			}
		} catch (ClassCastException e) {
			;
		} catch (Exception e) {
			LogHelper.severe(": failed to register block");
			e.printStackTrace();
		}

	}

	private void addName(Field f) {
		try {
			Object obj = f.get(null);
			if (obj instanceof Block) {
				Block curr = (Block) obj;
				String name = (String) curr.getClass().getField(NAME).get(null);
				LanguageRegistry.addName(curr, name);
				LogHelper.info("Added name for block " + f.getName() + ": "
				        + name);
			} else if (obj instanceof Item) {
				Item curr = (Item) obj;
				String name = (String) curr.getClass().getField(NAME).get(null);
				LanguageRegistry.addName(curr, name);
				LogHelper.info("Added name for item " + f.getName() + ": "
				        + name);
			} else {
				throw new Exception(
				        "Object is neither item nor block. This is a bug!");
			}

		} catch (ClassCastException e) {
		} catch (Exception e) {
			LogHelper.severe(": failed to add name");
			e.printStackTrace();
		}

	}

	private void addRecipe(Field f) {
		if (!Modifier.isPrivate(f.getModifiers())) {
			Property p4 = LevelStorage.configuration.get(
			        LevelStorage.RECIPES_CATEGORY, f.getName(), true);
			p4.comment = "Determines whether or not item's recipe is enabled";
			boolean enable = p4.getBoolean(true);
			if (enable) {
				LogHelper.info("Adding recipe for: " + f.getName());
				try {
					f.getType().getMethod("addCraftingRecipe").invoke(null);
				} catch (ClassCastException e) {
				} catch (Exception e) {
					LogHelper.severe(": failed to add recipe");
					e.printStackTrace();
				}
			} else {
				LogHelper.info(String.format(
				        "Recipe disabled for: %s (config)", f.getName()));
			}

		}
	}

	private void setBlockMiningLevels(Field f) {
		try {
			Object obj = f.get(null);
			if (obj instanceof Block) {
				LogHelper.info("Setting harvest level for block: "
				        + f.getName());
				MinecraftForge.setBlockHarvestLevel((Block) obj, "pickaxe", 1);
			}
		} catch (ClassCastException e) {
		} catch (Exception e) {
			LogHelper.severe("failed to set block's mining level");
			e.printStackTrace();
		}
	}

	public void init() {
		Field[] items = LSBlockItemList.class.getDeclaredFields();
		for (Field f : items)
			this.create(f);
		for (Field f : items)
			this.registerBlock(f);
		for (Field f : items)
			this.addName(f);
		for (Field f : items)
			this.addRecipe(f);
		for (Field f : items)
			this.setBlockMiningLevels(f);

	}
}
