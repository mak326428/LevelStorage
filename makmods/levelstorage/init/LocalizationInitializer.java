package makmods.levelstorage.init;

import java.util.List;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.LogHelper;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Localization support
 * 
 * @author mak326428
 * 
 */
public class LocalizationInitializer {

	public static final LocalizationInitializer instance = new LocalizationInitializer();
	public static List<String> localizationList = Lists.newArrayList();
	public static final String LOCALIZATION_PATH_PREFIX = "/assets/"
			+ Reference.MOD_ID.toLowerCase() + "/lang/";

	private LocalizationInitializer() {
		;
	}

	static {
		localizationList.add("en_US");
		localizationList.add("de_DE");
		localizationList.add("zh_CN");
		localizationList.add("ru_RU");
	}

	public void init() {
		for (String locale : localizationList) {
			LogHelper.info("Loading locale " + locale);
			LanguageRegistry.instance().loadLocalization(
					LOCALIZATION_PATH_PREFIX + locale + ".lang", locale, false);
		}
	}
}
