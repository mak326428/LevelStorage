package makmods.levelstorage.logic.util;

import makmods.levelstorage.lib.Reference;
import cpw.mods.fml.common.FMLLog;

/**
 * Just a util helper for easier logging
 */
public class LogHelper {

	public static void severe(String message) {
		FMLLog.severe(Reference.MOD_NAME + ": " + message);
	}

	public static void fine(String message) {
		FMLLog.fine(Reference.MOD_NAME + ": " + message);
	}

	public static void finer(String message) {
		FMLLog.finer(Reference.MOD_NAME + ": " + message);
	}

	public static void finest(String message) {
		FMLLog.finest(Reference.MOD_NAME + ": " + message);
	}

	public static void info(String message) {
		FMLLog.info(Reference.MOD_NAME + ": " + message);
	}

	public static void warning(String message) {
		FMLLog.warning(Reference.MOD_NAME + ": " + message);
	}

}
