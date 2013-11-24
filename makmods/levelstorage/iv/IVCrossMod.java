package makmods.levelstorage.iv;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

public class IVCrossMod {

	private static Class<?> aspItemAPI = null;
	private static Method aspGetItem = null;

	private static boolean wasInit = false;

	private static void aspLazyReflection() {
		if (wasInit)
			return;
		try {
			aspItemAPI = Class.forName("advsolar.api.ASPItemAPI");
			aspGetItem = aspItemAPI.getMethod("get", String.class);
			wasInit = true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void addASPValues() {
		
	}

	public static void addASP(String itemName, int metadata, int value) {
		try {
			aspLazyReflection();
			ItemStack retrieved = (ItemStack) aspGetItem.invoke(null, itemName);
			retrieved.setItemDamage(metadata);
			IVRegistry.instance.assignItemStack_dynamic(retrieved, value);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
