package makmods.levelstorage.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;

/**
 * Used internally, for other APIs. Use at your own risk.
 */
public class APIHelper {

	/**
	 * Used internally, for logging failures. <br />
	 * <b>DO NOT USE IN YOUR OWN IMPLEMENTATION!</b>
	 */
	public static void logFailure() {
		FMLLog.log(Level.SEVERE,
		        "LevelStorage API: failure. It may be caused by either");
		FMLLog.log(Level.SEVERE,
		        "LevelStorage not being installed on your minecraft instance");
		FMLLog.log(Level.SEVERE, "or mod incorrectly using API.");
	}

	/**
	 * Gets instance for simple singleton classes. <br />
	 * Accesses "instance" field.
	 * 
	 * @param classname
	 *            Name of the class inside <em>makmods.levelstorage</em>
	 *            package.
	 * @return Instance of requested class
	 */
	public static Object getInstanceFor(String classname) {
		String clsname = getMainPackage() + "." + classname;
		try {
			Class cl = Class.forName(clsname);
			Field f = cl.getField("instance");
			return f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}

	/**
	 * Shortcut. Gets field for requested parameters. <br />
	 * Logs failure in case of bad stuff happening (doesn't throw exception) <br />
	 * 
	 * @param cls
	 *            Class to look into.
	 * @param name
	 *            Name of field you'd like to get.
	 * @return Requested field.
	 */
	public static Field getFieldFor(Class cls, String name) {
		try {
			return cls.getField(name);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}

	/**
	 * Shortcut. Gets method for requested parameters. <br />
	 * Logs failure in case of bad stuff happening (doesn't throw exception) <br />
	 * 
	 * @param cls
	 *            Class to look into.
	 * @param name
	 *            Name of method you'd like to get.
	 * @return Requested method.
	 */
	public static Method getMethodFor(Class cls, String name,
	        Class... parameterTypes) {
		try {
			return cls.getMethod(name, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}

	/**
	 * Returns class for requested class name. <br />
	 * <b>WARNING: </b> do not use for your own purposes.
	 * 
	 * @param classname
	 *            Class name
	 * @return
	 */
	public static Class getClassByName(String classname) {
		String clsname = getMainPackage() + "." + classname;
		try {
			return Class.forName(clsname);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}

	/**
	 * Used internally, always returns "makmods.levelstorage"
	 * 
	 * @return "makmods.levelstorage"
	 */
	public static String getMainPackage() {
		return "makmods.levelstorage";
	}
}
