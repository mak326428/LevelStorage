package makmods.levelstorage.proxy;

import java.util.ArrayList;
import java.util.EnumSet;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	public static ArrayList<Task> todoList = new ArrayList<Task>();

	public static int TASK_PER_N_TICKS = 1;

	private static int currInc = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		//TODO: readd
		//WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		currInc++;
		if (currInc > TASK_PER_N_TICKS) {
			if (todoList.size() > 0) {
				Task task = todoList.get(todoList.size() - 1);
				task.doJob();
				todoList.remove(task);
			}
			currInc = 0;
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return Reference.MOD_ID.toLowerCase();
	}

}
