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
	
	public static void addGTMaterial(String material, int valuePerMaterialUnit) {
		double val = valuePerMaterialUnit;
		IVRegistry.instance.assign("dustTiny" + material, (int)Math.floor(val / 9.0D));
		IVRegistry.instance.assign("ingot" + material, (int)Math.floor(val));
		IVRegistry.instance.assign("dust" + material, (int)Math.floor(val));
		IVRegistry.instance.assign("block" + material, (int)Math.floor(val * 9.0D));
		IVRegistry.instance.assign("dustSmall" + material, (int)Math.floor(val / 4.0D));
	}
	
	public static void addGTValues() {
		addGTMaterial("Nickel", 300);
		addGTMaterial("Platinum", 1024);
		addGTMaterial("Invar", 270);
		addGTMaterial("Aluminium", 384);
		addGTMaterial("Titanium", 448);
		
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
