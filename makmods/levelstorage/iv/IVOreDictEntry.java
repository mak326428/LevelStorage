package makmods.levelstorage.iv;

public class IVOreDictEntry implements IVEntry {
	
	private final String name;
	private final int value;

	public IVOreDictEntry(String oreDictName, int value) {
		this.name = oreDictName;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}
