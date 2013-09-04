package makmods.levelstorage.registry;

import makmods.levelstorage.logic.util.LogHelper;

public enum SyncType {
	TRANSMITTER, RECEIVER;
	
	public SyncType getInverse() {
		if (this == TRANSMITTER)
			return RECEIVER;
		else
			return TRANSMITTER;
	}
}
