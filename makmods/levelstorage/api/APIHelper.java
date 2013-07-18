package makmods.levelstorage.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
/*
 * Used internally, for other APIs. Use at your own risk.
 */
public class APIHelper {
	
	public static void logFailure() {
		FMLLog.log(Level.SEVERE, "LevelStorage API: failure. It may be caused by either");
		FMLLog.log(Level.SEVERE, "by LevelStorage not being installed on your minecraft instance");
		FMLLog.log(Level.SEVERE, "or mod incorrectly using API.");
	}
	// Gets instance for a singleton-like class
	// (Singletons and static classes are awesome for APIs BTW)
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
	
	public static Field getFieldFor(Class cls, String name) {
		try {
			return cls.getField(name);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}
	
	public static Method getMethodFor(Class cls, String name, Class ...parameterTypes) {
		try {
			return cls.getMethod(name, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
			logFailure();
			return null;
		}
	}
	
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
	
	public static String getMainPackage() {
		return "makmods.levelstorage";
	}
}
