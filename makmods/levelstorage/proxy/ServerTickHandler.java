package makmods.levelstorage.proxy;

import java.util.ArrayList;
import java.util.EnumSet;

import makmods.levelstorage.lib.Reference;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	public static ArrayList<Task> todoList = new ArrayList<Task>();

	public static final int TASKS_PER_TICK = 4;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		for (int i = 0; i < TASKS_PER_TICK; i++) {
			if (todoList.size() > 0) {
				Task task = todoList.get(todoList.size() - 1);
				task.doJob();
				todoList.remove(task);
			}
		}
		if (todoList.size() > 0) {
			FMLLog.warning("WARNING: LevelStorage's tick handler: cannot keep up with the amount of upcoming requests!");
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return Reference.MOD_ID.toLowerCase();
	}

}
