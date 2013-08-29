package makmods.levelstorage.registry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.logging.Level;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.Helper;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;

public class XpStackRegistry {

	public static final XpStackRegistry instance = new XpStackRegistry();

	private XpStackRegistry() {
	}

	public ArrayList<XpStack> ITEM_XP_CONVERSIONS = new ArrayList<XpStack>();
	public static final String XP_REGISTRY_CATEGORY = "xpRegistry";

	public static final AbstractMap.SimpleEntry<Integer, Integer> XP_EU_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
	        1, 64); // 1 XP = 64 EU
	public static final AbstractMap.SimpleEntry<Integer, Integer> UUM_XP_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
	        1, 130); // 1 UUM = 130 XP

	public static final int ORE_DICT_NOT_FOUND = -1;

	public void initCriticalNodes() {
		this.pushToRegistry_critical(new XpStack(new ItemStack(Item.redstone),
		        8));
		this.pushToRegistry_critical(new XpStack(new ItemStack(
		        Item.netherQuartz), 4));
		this.pushToRegistry_critical(new XpStack(new ItemStack(Item.glowstone),
		        32));
		this.pushToRegistry_critical(new XpStack(new ItemStack(Item.diamond),
		        512));
		this.pushToRegistry_critical(new XpStack(
		        new ItemStack(Item.netherStar), 4096));
		this.pushToRegistry_critical(new XpStack(new ItemStack(Block.obsidian),
		        8));
		// dummy for debug
		// this.pushOreToRegistry("ingotGold", 1);

		if (LevelStorage.detectedGT) {
			this.pushOreToRegistry("dustDiamond", 512);
			this.pushOreToRegistry("dustTinyDiamond", 128);
			this.pushOreToRegistry("dustPlatinum", 2048);
			this.pushOreToRegistry("dustTinyPlatinum", 512);
			this.pushOreToRegistry("dustPlutonium", 1536);
			this.pushOreToRegistry("dustTinyPlutonium", 1536 / 4);

		}
		// this.pushOreToRegistry("dustDiamond", 512);
	}

	public void printRegistry() {
		FMLLog.log(Level.INFO, "Starting printing the xp registry contents");
		for (XpStack s : this.ITEM_XP_CONVERSIONS) {
			FMLLog.log(Level.INFO,
			        "\t#" + s.stack.itemID + ":" + s.stack.getItemDamage()
			                + " - " + s.stack.getDisplayName() + " - "
			                + s.value + " (1 " + s.stack.getDisplayName()
			                + " = " + s.value + " XP)");
		}
	}

	public void pushToRegistry(XpStack stack) {
		FMLLog.log(Level.INFO, "Adding #" + stack.stack.itemID + ":"
		        + stack.stack.getItemDamage() + " to the Xp Registry, value: "
		        + stack.value);
		this.ITEM_XP_CONVERSIONS.add(stack);
	}

	public void pushToRegistry_critical(XpStack stack) {
		Property property = LevelStorage.configuration
		        .get(XP_REGISTRY_CATEGORY,
		                stack.stack.getItem().getUnlocalizedName()
		                        .replace("item.", "").replace(".name", "")
		                        .replace("tile.", ""), stack.value);
		property.comment = "Set to -1 to disable";
		int value = property.getInt();
		if (value == -1) {
			LogHelper.warning("XP entry for item "
			        + Helper.getNiceStackName(stack.stack) + " is disabled");
			return;
		}
		FMLLog.log(Level.INFO, "Adding #" + stack.stack.itemID + ":"
		        + stack.stack.getItemDamage() + " to the Xp Registry, value: "
		        + stack.value);
		this.ITEM_XP_CONVERSIONS.add(new XpStack(stack.stack, value));
	}

	public void pushOreToRegistry(String name, int value) {
		boolean exists = false;
		for (ItemStack stack : OreDictionary.getOres(name)) {
			this.pushToRegistry(new XpStack(stack, value));
			exists = true;
		}
		if (!exists) {
			FMLLog.warning(Reference.MOD_NAME
			        + ": failed to add ore to XP Registry - " + name);
		}
	}
}
