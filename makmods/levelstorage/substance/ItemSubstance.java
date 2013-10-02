package makmods.levelstorage.substance;

import java.util.Map;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.ModUniversalInitializer;
import net.minecraft.item.Item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ItemSubstance extends Item {

	public ItemSubstance(int par1) {
		super(LevelStorage.configuration.getItem("substances",
				ModUniversalInitializer.instance.getNextItemID()).getInt());
	}

}
