package makmods.levelstorage.logic.util;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;

public class OreDictHelper {

	public static boolean oreExists(String name) {
		boolean exists = false;
		for (ItemStack stack : OreDictionary.getOres(name)) {
			exists = true;
		}
		return exists;
	}

	public static final String ORE_DICT_NOT_FOUND = "Unknown";

	static {
		if (!oreExists("oreCoal")) {
			if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
			        "addCoalOreToOreDict", true).getBoolean(true))
				OreDictionary.registerOre("oreCoal", Block.oreCoal);
		}
		// if (!oreExists("oreRedstone")) {
		// Redstone Glowing Ore is a separately another block,
		// so drill will treat redstone normal and redstone glowing blocks
		// as different while it shouldn't
		// this is why I add this option.
		// Other mods (i.e. GT, or forge in future) may correct that bug
		// And make things go crazy wrong, so just a fallback config option.
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
		        "addRedstoneOreToOreDict", true).getBoolean(true)) {
			OreDictionary.registerOre("oreRedstone", Block.oreRedstone);
			OreDictionary.registerOre("oreRedstone", Block.oreRedstoneGlowing);
		}
		// }
	}

	public static String getOreName(ItemStack st) {
		return OreDictionary.getOreName(OreDictionary.getOreID(st));
	}

}
