package makmods.levelstorage.init;

import java.lang.reflect.Method;

import makmods.levelstorage.logic.util.LogHelper;

public class CompatibilityInitializer {
	
	public static final CompatibilityInitializer instance = new CompatibilityInitializer();

	private CompatibilityInitializer() {
		;
	}
	
	private void loadCompat(String name) {
		try {
			String classname = "makmods.levelstorage.compat." + name + ".CompatibilityLoader";
			Class compat = Class.forName(classname);
			Method load = compat.getMethod("load");
			load.invoke(null);
		} catch (Exception e) {
			LogHelper.severe(String.format("Failed to load compatibility submodule %s"));
			e.printStackTrace();
		}
	}
	
	public void init() {
		loadCompat("nei");
	}

}
