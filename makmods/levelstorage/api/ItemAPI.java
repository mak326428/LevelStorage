package makmods.levelstorage.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Used to get access to Items in this mod Currently existing items:
 * itemLevelStorageBook itemAdvScanner itemFreqCard blockXpGen blockXpCharger
 * blockWlessConductor blockWlessPowerSync
 */
public class ItemAPI {
	private static Class modItemsClass;
	private static Class modBlocksClass;
	private static Object modItemsInstance;
	private static Object modBlocksInstance;
	private static Map<String, Object> everythingInClasses = new HashMap<String, Object>();

	private static Map<String, Object> getItemsAndBlocks() {
		Map<String, Object> objs = new HashMap<String, Object>();

		Field[] items = modItemsClass.getDeclaredFields();
		Field[] blocks = modBlocksClass.getDeclaredFields();

		for (Field item : items) {
			if (item.getName() != "instance") {
				if (!Modifier.isPrivate(item.getModifiers())) {
					try {
						objs.put(item.getName(), item.get(modItemsInstance));
					} catch (Exception e) {
						APIHelper.logFailure();
						e.printStackTrace();
					}
				}
			}
		}
		for (Field block : blocks) {
			if (block.getName() != "instance") {
				if (!Modifier.isPrivate(block.getModifiers())) {
					try {
						objs.put(block.getName(), block.get(modBlocksInstance));
					} catch (Exception e) {
						APIHelper.logFailure();
						e.printStackTrace();
					}
				}
			}
		}

		return objs;
	}

	static {
		try {
			modItemsClass = Class.forName("makmods.levelstorage.ModItems");
			modBlocksClass = Class.forName("makmods.levelstorage.ModBlocks");
			modItemsInstance = APIHelper.getInstanceFor("ModItems");
			modBlocksInstance = APIHelper.getInstanceFor("ModBlocks");
			everythingInClasses = getItemsAndBlocks();
		} catch (ClassNotFoundException e) {
			APIHelper.logFailure();
			e.printStackTrace();
		}
	}

	/**
	 * Gets the item (or block)
	 * 
	 * @return ItemStack for requested Item, if doesn't exist, null.
	 */
	public static ItemStack getItem(String name) {

		Iterator it = everythingInClasses.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Object> current = (Map.Entry<String, Object>) it
					.next();
			String nameCurrent = current.getKey();
			Object objCurrent = current.getValue();

			if (nameCurrent == name) {
				if (objCurrent instanceof Block)
					return new ItemStack((Block) objCurrent);
				else {
					if (objCurrent instanceof Item)
						return new ItemStack((Item) objCurrent);
				}
			}

			it.remove();
		}

		return null;
	}

}
