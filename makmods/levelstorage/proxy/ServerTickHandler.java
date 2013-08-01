package makmods.levelstorage.proxy;

import java.util.EnumSet;

import makmods.levelstorage.lib.Reference;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return Reference.MOD_ID.toLowerCase();
	}

}
