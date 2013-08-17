package makmods.levelstorage.inventory;

import java.util.EnumSet;

import makmods.levelstorage.LevelStorage;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class InventoryNetTicker implements ITickHandler {
	
	public static final InventoryNetTicker instance = new InventoryNetTicker();
	public static int TICK_RATE;
	private int incrTicks = 0;
	
	static {
		Property p = LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"inventoryTickRate", 1);
		p.comment = "Inventory tickrate frequency. If something is laggy for you, make sure to increase the number.";
		TICK_RATE = p.getInt(1);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		incrTicks++;
		if (incrTicks > TICK_RATE) {
			InventoryNet.instance.segments.clear();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (incrTicks > TICK_RATE) {
			InventoryNet.instance.tick();
			incrTicks = 0;
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "invNetTickHandler";
	}

}
