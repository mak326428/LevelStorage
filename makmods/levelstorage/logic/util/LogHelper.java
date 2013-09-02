package makmods.levelstorage.logic.util;

import java.util.logging.Level;

import makmods.levelstorage.LevelStorage;

/**
 * Just a util helper for easier logging
 */
public class LogHelper {

	public static void severe(String message) {
		LevelStorage.logger.log(Level.SEVERE, message);
	}

	public static void fine(String message) {
		LevelStorage.logger.log(Level.FINE, message);
	}

	public static void finer(String message) {
		LevelStorage.logger.log(Level.FINER, message);
	}

	public static void finest(String message) {
		LevelStorage.logger.log(Level.FINEST, message);
	}

	public static void info(String message) {
		LevelStorage.logger.log(Level.INFO, message);
	}

	public static void warning(String message) {
		LevelStorage.logger.log(Level.WARNING, message);
	}

}
