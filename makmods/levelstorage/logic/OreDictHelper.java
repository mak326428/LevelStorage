package makmods.levelstorage.logic;

import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
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
		if (!oreExists("oreCoal"))
			OreDictionary.registerOre("oreCoal", Block.oreCoal);
	}
	
	public static String getOreName(ItemStack st) {
		return OreDictionary.getOreName(OreDictionary.getOreID(st));
	}

}
