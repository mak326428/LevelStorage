package makmods.levelstorage.api;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;

/**
 * <p>
 * An API for IV Registry.
 * </p>
 * <p>
 * Like with all APIs that use reflection, you don't want to use this very
 * often.
 * </p>
 * <p>
 * You probably will use getValue() which could be a stress for performance.
 * Advice: make a cache
 * </p>
 * 
 * @author mak326428
 */
public class IVAPI {
	// TODO: add API for removeIV(Object obj)
	private static boolean wasInit = false;

	private static Class<?> api_ivRegistryClass = null;
	private static Method api_ivGetValue = null;
	private static Method api_assignItemStack = null;
	private static Method api_assignOreDict = null;
	private static Object api_instance = null;
	private static int NOT_FOUND = -1;

	private static void lazyInit() {
		if (!wasInit) {
			try {
				api_ivRegistryClass = Class
						.forName("makmods.levelstorage.iv.IVRegistry");
				api_ivGetValue = api_ivRegistryClass.getMethod("getValue",
						Object.class);
				api_assignItemStack = api_ivRegistryClass.getMethod(
						"assignItemStack_dynamic", ItemStack.class, int.class);
				api_assignOreDict = api_ivRegistryClass.getMethod(
						"assignOreDict_dynamic", String.class, int.class);
				api_instance = api_ivRegistryClass.getField("instance").get(
						null);
				NOT_FOUND = api_ivRegistryClass.getField("NOT_FOUND").getInt(
						null);
				wasInit = true;
			} catch (Exception e) {
				APIHelper.logFailure();
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds an IV value for OreDictionary entry
	 * 
	 * @param name
	 *            OreDictionary name
	 * @param value
	 *            IV
	 * 
	 * @throws RuntimeException
	 *             when reflection failed
	 */
	public static void assignOreDictionary(String name, int value) {
		try {
			api_assignOreDict.invoke(api_instance, name, value);
		} catch (Exception e) {
			APIHelper.logFailure();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds an IV value for ItemStack
	 * 
	 * @param itemStack
	 *            ItemStack you want to register IV with
	 * @param value
	 *            wished IV
	 * 
	 * @throws RuntimeException
	 *             when reflection failed
	 */
	public static void assignItemStack(ItemStack itemStack, int value) {
		try {
			api_assignItemStack.invoke(api_instance, itemStack, value);
		} catch (Exception e) {
			APIHelper.logFailure();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets IV value for specified ItemStack
	 * 
	 * @param is
	 *            ItemStack
	 * @return IV value for ItemStack requested
	 * 
	 * @throws RuntimeException
	 *             when reflection failed
	 */
	public static int getValue(ItemStack is) {
		return getValue_internal(is);
	}

	/**
	 * Gets IV value for OreDict entry
	 * 
	 * @param oreDictName
	 *            OreDictionary name ("ingotCopper", "ingotTin", etc.)
	 * @return IV value for OreDict name requested
	 * 
	 * @throws RuntimeException
	 *             when reflection failed
	 */
	public static int getValue(String oreDictName) {
		return getValue_internal(oreDictName);
	}

	/*
	 * Internal use only, not to confuse anybody
	 */
	private static int getValue_internal(Object obj) {
		try {
			lazyInit();
			return ((Integer) api_ivGetValue.invoke(null, obj));
		} catch (Exception e) {
			APIHelper.logFailure();
			throw new RuntimeException(e);
		}
	}

}
