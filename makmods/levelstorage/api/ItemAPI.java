package makmods.levelstorage.api;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * ItemAPI for all the items (and blocks for that matter) inside of
 * LevelStorage. <br />
 */
public class ItemAPI {
	private static Class c_LSBlockItemList;

	static {
		c_LSBlockItemList = APIHelper.getClassByName("LSBlockItemList");
	}

	/**
	 * Gets item for the name and metadata you specified. <br />
	 * <b>WARNING: call this after LevelStorage passed its init-phase.</b>
	 * 
	 * @param name
	 *            Name of the item you wanna access
	 * @param meta
	 *            Metadata of the item (set to OreDictionary.WILDCARD_VALUE if
	 *            you don't care)
	 * @return ItemStack containing the item you requested, null and report to
	 *         console if it doesn't exist
	 */
	public static ItemStack getItem(String name, int meta) {
		try {
			Field f = c_LSBlockItemList.getDeclaredField(name);
			Object obj = f.get(null);
			if (obj instanceof Item)
				return new ItemStack((Item) obj, 1, meta);
			if (obj instanceof Block)
				return new ItemStack((Block) obj, 1, meta);
			return null;
		} catch (Exception e) {
			APIHelper.logFailure();
			e.printStackTrace();
			return null;
		}
	}
	
	public static ItemStack getSimpleItem(String name) {
		
	}
}
