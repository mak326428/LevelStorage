package makmods.levelstorage.api;

import java.lang.reflect.Field;
import java.util.List;

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

	public static class SimpleItemAPI {
		
		private static Class c_SimpleItems;
		private static Object o_SimpleItemsInstance;
		private static final String SIMPLE_ITEMS_CLASSNAME = "item.SimpleItems";
		
		static {
			c_SimpleItems = APIHelper.getClassByName(SIMPLE_ITEMS_CLASSNAME);
			o_SimpleItemsInstance = APIHelper
			        .getInstanceFor(SIMPLE_ITEMS_CLASSNAME);
		}

		public static ItemStack getSimpleItem(String name) {
			try {
				int sItemsId = ((Item)o_SimpleItemsInstance).itemID;
				Field itemNamesField = c_SimpleItems.getField("itemNames");
				List<String> names = (List<String>)itemNamesField.get(o_SimpleItemsInstance);
				if (!names.contains(name)) {
					throw new Exception("SimpleItem not found!");
				}
				return new ItemStack(sItemsId, 1, names.indexOf(name));
			} catch (Exception e) {
				APIHelper.logFailure();
				e.printStackTrace();
			}
			return null;
		}
	}
}
