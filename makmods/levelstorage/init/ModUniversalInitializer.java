package makmods.levelstorage.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Initializes all the items and blocks in the {@link LSBlockItemList}. Invoked
 * during init-phase.
 * 
 * @author mak326428
 * 
 */
public class ModUniversalInitializer {

	public static final ModUniversalInitializer instance = new ModUniversalInitializer();

	private ModUniversalInitializer() {
		;
	}

	private int incrementalItemId = 5000;
	private int incrementalBlockId = 1340;

	private static enum FieldKind {
		BLOCK, ITEM, UNKNOWN
	}

	public FieldKind getKindOfField(Field f) {
		Class<?> clazz = f.getType();
		// System.out.println(clazz.getName());
		// System.out.println(clazz.getSuperclass().getSimpleName());
		if (Item.class.isAssignableFrom(clazz))
			return FieldKind.ITEM;
		else if (Block.class.isAssignableFrom(clazz))
			return FieldKind.BLOCK;
		return FieldKind.UNKNOWN;
	}

	public int getNextBlockID() {
		incrementalBlockId++;
		return incrementalBlockId;
	}

	public int getNextItemID() {
		incrementalItemId++;
		return incrementalItemId;
	}

	public boolean resolveRegisterWith(Field f) {
		Class<?> clazz = f.getType();
		AnnotatedElement el = clazz;
		if (el.isAnnotationPresent(RegisterWith.class)) {
			RegisterWith rw = el.getAnnotation(RegisterWith.class);
			String loadWith = rw.value();
			if (loadWith != null)
				return Loader.isModLoaded(loadWith);
		}
		return true;
	}

	public void create(Field f) {
		LogHelper.info("Initializing block/item: " + f.getName());
		try {
			if (!resolveRegisterWith(f))
				return;
			Class c = f.getType();
			int id = 0;
			FieldKind fk = getKindOfField(f);
			if (fk == FieldKind.BLOCK)
				id = LevelStorage.configuration.getBlock(f.getName(),
						getNextBlockID()).getInt();
			else if (fk == FieldKind.ITEM)
				id = LevelStorage.configuration.getItem(f.getName(),
						getNextItemID()).getInt();
			else
				LogHelper
						.severe("object is neither item nor block. This is a bug!");
			Constructor con = c.getConstructor(int.class);
			f.set(null, con.newInstance(id));
			Object obj = f.get(null);
			if (obj instanceof Block)
				((Block) obj).setUnlocalizedName(f.getName());
			if (obj instanceof Item)
				((Item) obj).setUnlocalizedName(f.getName());
		} catch (ClassCastException e) {
		} catch (Exception e) {
			LogHelper.severe("failed to initialize block/item");
			e.printStackTrace();
		}
	}

	private Class<? extends ItemBlock> getPreferredItemBlock(Field f) {
		Class blockClass = f.getType();
		CustomItemBlock customIBAnn = (CustomItemBlock) blockClass
				.getAnnotation(CustomItemBlock.class);
		if (customIBAnn != null) {
			LogHelper
					.info(String
							.format("Block %s has @CustomItemBlock annotation (ItemBlock is %s), using it",
									f.getName(), customIBAnn.itemBlock()
											.getCanonicalName()));
			return customIBAnn.itemBlock();
		}
		return ItemBlock.class;
	}

	private void registerBlock(Field f) {
		try {
			if (!resolveRegisterWith(f))
				return;
			Object obj = f.get(null);
			if (obj instanceof Block) {
				LogHelper.info("Registering block " + f.getName()
						+ " in GameRegistry");
				Block currBlock = (Block) obj;
				String name = f.getName();
				GameRegistry.registerBlock(currBlock, getPreferredItemBlock(f),
						name);
			}
		} catch (ClassCastException e) {
			;
		} catch (Exception e) {
			LogHelper.severe(": failed to register block");
			e.printStackTrace();
		}

	}

	private void addRecipe(Field f) {
		if (!resolveRegisterWith(f))
			return;
		if (!Modifier.isPrivate(f.getModifiers())) {
			Object obj;
			try {
				obj = f.get(null);
			} catch (Exception e) {
				LogHelper.severe("Failed to add recipe for " + f.getType().getSimpleName());
				e.printStackTrace();
				return;
			}
			if (!(obj instanceof IHasRecipe)) {
				LogHelper.warning("Item/Block has no recipe: " + f.getType().getSimpleName());
				return;
			}
			Property p4 = LevelStorage.configuration.get(
					LevelStorage.RECIPES_CATEGORY, f.getName(), true);
			p4.comment = "Determines whether or not item's recipe is enabled";
			boolean enable = p4.getBoolean(true);
			if (enable) {
				LogHelper.info("Adding recipe for: " + f.getName());
				((IHasRecipe)obj).addCraftingRecipe();
			} else {
				LogHelper.info(String.format(
						"Recipe disabled for: %s (config)", f.getName()));
			}

		}
	}

	private void setBlockMiningLevels(Field f) {
		if (!resolveRegisterWith(f))
			return;
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
			this.addRecipe(f);
		for (Field f : items)
			this.setBlockMiningLevels(f);
	}
}
