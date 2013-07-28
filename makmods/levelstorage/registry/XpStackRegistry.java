package makmods.levelstorage.registry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.logging.Level;

import makmods.levelstorage.api.XpStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;

public class XpStackRegistry {

	public static final XpStackRegistry instance = new XpStackRegistry();

	private XpStackRegistry() {
	}

	public ArrayList<XpStack> ITEM_XP_CONVERSIONS = new ArrayList<XpStack>();

	public static final AbstractMap.SimpleEntry<Integer, Integer> XP_EU_CONVERSION = new AbstractMap.SimpleEntry<Integer, Integer>(
			1, 64); // 1 XP = 64 EU

	public static final int ORE_DICT_NOT_FOUND = -1;

	public void initCriticalNodes() {
		this.pushToRegistry(new XpStack(new ItemStack(Item.redstone), 8));
		this.pushToRegistry(new XpStack(new ItemStack(Item.netherQuartz), 4));
		this.pushToRegistry(new XpStack(new ItemStack(Item.glowstone), 32));
		this.pushToRegistry(new XpStack(new ItemStack(Item.diamond), 512));
		this.pushToRegistry(new XpStack(new ItemStack(Item.netherStar), 4096));
		this.pushToRegistry(new XpStack(new ItemStack(Block.obsidian), 8));
		//this.pushOreToRegistry("dustDiamond", 512);
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

	public void pushOreToRegistry(String name, int value) {
		// Something's wrong here, TODO: test.
		if (OreDictionary.getOreID(name) == ORE_DICT_NOT_FOUND) {
			FMLLog.log(Level.WARNING, "Ore " + name
					+ " is not found in the ore dictionary, ignoring");
			return;
		}
		for (ItemStack stack : OreDictionary.getOres(name)) {
			this.pushToRegistry(new XpStack(stack, value));
		}
	}
}
