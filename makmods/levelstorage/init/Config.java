package makmods.levelstorage.init;

import net.minecraftforge.common.Configuration;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.LogHelper;

public class Config {

	public static boolean ACTIVE = false;

	public enum LSConfigCategory {
		BALANCE(LevelStorage.BALANCE_CATEGORY), IV(IVRegistry.IV_CATEGORY), GENERAL(
				Configuration.CATEGORY_GENERAL);

		private final String name;

		private LSConfigCategory(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static int getInt(LSConfigCategory category, String name, int def,
			String comment) {
		if (!ACTIVE)
			LogHelper
					.severe("Tried to get config option after the config is closed. This is a bug, report it!");
		return LevelStorage.configuration.get(category.getName(), name, def,
				comment).getInt(def);
	}
}
