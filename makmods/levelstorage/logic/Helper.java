package makmods.levelstorage.logic;

import makmods.levelstorage.registry.SyncType;

public class Helper {
	public static SyncType invertType(SyncType type) {
		if (type == SyncType.RECEIVER)
			return SyncType.TRANSMITTER;
		if (type == SyncType.TRANSMITTER)
			return SyncType.RECEIVER;
		return null;
	}
}
