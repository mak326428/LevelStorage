package makmods.levelstorage.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * API for XpStackRegistry.
 */
public class XpRegistryAPI {
	// Internal zone, using reflection to get access to class.
	private static Method m_pushToRegistry;
	private static Method m_pushOreToRegistry;

	private static Object c_XpStackRegistry_instance;
	private static Class c_XpStackRegistry_class;

	private static Field f_XpStackRegistry_conversions;

	private static final String REGISTRY_CLASS_NAME = "registry.XpStackRegistry";

	static {
		// Class & its instance
		c_XpStackRegistry_instance = APIHelper
		        .getInstanceFor(REGISTRY_CLASS_NAME);
		c_XpStackRegistry_class = APIHelper.getClassByName(REGISTRY_CLASS_NAME);

		// Methods
		m_pushToRegistry = APIHelper.getMethodFor(c_XpStackRegistry_class,
		        "pushToRegistry", XpStack.class);
		m_pushOreToRegistry = APIHelper.getMethodFor(c_XpStackRegistry_class,
		        "pushOreToRegistry", String.class, int.class);

		// Fields
		f_XpStackRegistry_conversions = APIHelper.getFieldFor(
		        c_XpStackRegistry_class, "ITEM_XP_CONVERSIONS");

	}

	// End of internal zone

	/**
	 * Gets all the entries in the XP Registry
	 */
	public static ArrayList<XpStack> getConversions() {
		try {
			return (ArrayList<XpStack>) f_XpStackRegistry_conversions
			        .get(c_XpStackRegistry_instance);
		} catch (Exception e) {
			e.printStackTrace();
			APIHelper.logFailure();
			return null;
		}
	}

	/**
	 * Adds {@link.XpStack} to the registry.
	 */
	public static void pushToRegistry(XpStack stack) {
		try {
			m_pushToRegistry.invoke(c_XpStackRegistry_instance, stack);
		} catch (Exception e) {
			e.printStackTrace();
			APIHelper.logFailure();
			return;
		}
	}

	/**
	 * Adds multiple items to registry. All OreDict's ones matching "name" <br />
	 * parameter, to be exact
	 * 
	 * @param name Name of the ore you want to add to XpRegistry.
	 * @param value Wished value of the ore.
	 */
	public static void pushOreToRegistry(String name, int value) {
		try {
			m_pushOreToRegistry.invoke(c_XpStackRegistry_instance, name, value);
		} catch (Exception e) {
			e.printStackTrace();
			APIHelper.logFailure();
			return;
		}
	}
}
