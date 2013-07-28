package makmods.levelstorage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import makmods.levelstorage.api.APIHelper;
import makmods.levelstorage.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

public class BlockItemIds {
	public static final BlockItemIds instance = new BlockItemIds();
	public final Map<String, Integer> ids = new HashMap<String, Integer>();
	private Configuration config;

	public void addId(String forWhat, int id) {
		this.ids.put(forWhat, id);
	}

	public int getIdFor(String forWhat) {
		if (!ids.containsKey(forWhat)) {
			return 0;
		}
		return this.ids.get(forWhat);
	}

	// TODO: better name
	public void messWithConfig(Configuration config) {
		this.config = config;
		/*
		 * String itemXPBookId = "itemXPBookId"; String itemAdvScannerId =
		 * "itemAdvScannerId"; String blockXpGenId = "blockXpGenId"; String
		 * blockXpChargerId = "blockXpChargerId"; String itemFreqCard =
		 * "itemFrequencyCard"; String blockWirelessConductor =
		 * "blockWirelessConductor"; String blockWlessPowerSync =
		 * "blockWlessPowerSync"; BlockItemIds.instance.addId(itemXPBookId,
		 * config.getItem(itemXPBookId, 2085).getInt());
		 * BlockItemIds.instance.addId(itemFreqCard,
		 * config.getItem(itemFreqCard, 2086).getInt());
		 * BlockItemIds.instance.addId(itemAdvScannerId,
		 * config.getItem(itemAdvScannerId, 2087).getInt());
		 * BlockItemIds.instance.addId(blockXpGenId,
		 * config.getBlock(blockXpGenId, 237).getInt());
		 * BlockItemIds.instance.addId(blockXpChargerId,
		 * config.getBlock(blockXpChargerId, 238).getInt());
		 * BlockItemIds.instance.addId(blockWirelessConductor,
		 * config.getBlock(blockWirelessConductor, 239).getInt());
		 * BlockItemIds.instance.addId(blockWlessPowerSync,
		 * config.getBlock(blockWlessPowerSync, 240).getInt());
		 */
		// it's very dangerous to use reflection in obfuscated environment, but
		// we're using srg
		int currBlockId = 250;
		Field[] declaredBlocks = ModBlocks.class.getDeclaredFields();
		Object modBlocksInstance = null;
		try {
			modBlocksInstance = ModBlocks.class.getField("instance").get(null);
		} catch (Exception e1) {
			FMLLog.warning(Reference.MOD_NAME
					+ ": failed to get instance for ModBlocks");
		}
		for (Field f : declaredBlocks) {
			try {
				Object o = f.get(modBlocksInstance);
				if (o instanceof Block) {
					BlockItemIds.instance.addId(
							((Block) o).getUnlocalizedName(),
							config.getBlock(((Block) o).getUnlocalizedName(),
									currBlockId).getInt());
					currBlockId++;
				}
			} catch (Exception e) {
				continue;
			}
		}

		LevelStorage.itemLevelStorageBookSpace = config.get(
				Configuration.CATEGORY_GENERAL, "XPBookCapacity", 16384)
				.getInt();
	}

	private BlockItemIds() {
	}
}
