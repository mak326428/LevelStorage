package makmods.levelstorage.compat.nei;

import makmods.levelstorage.logic.util.LogHelper;
import cpw.mods.fml.common.Loader;

public class CompatibilityLoader {

	public static void load() {
		if (!Loader.isModLoaded("NotEnoughItems")) {
			LogHelper.severe("NEI is not installed, aborting.");
			return;
		}
		// Not much to do here otherwise
	}

}
