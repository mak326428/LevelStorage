package makmods.levelstorage.registry;


public enum SyncType {
	TRANSMITTER, RECEIVER;
	
	public SyncType getInverse() {
		if (this == TRANSMITTER)
			return RECEIVER;
		else
			return TRANSMITTER;
	}
}
