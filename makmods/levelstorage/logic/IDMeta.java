package makmods.levelstorage.logic;

public class IDMeta {
	
	private int id;
	private int metadata;
	
	public IDMeta(int id, int metadata) {
		this.id = id;
		this.metadata = metadata;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getMetadata() {
		return metadata;
	}

	public void setMetadata(int metadata) {
		this.metadata = metadata;
	}
	
	public IDMeta clone() {
		return new IDMeta(id, metadata);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof IDMeta))
			return false;
		IDMeta other = (IDMeta)obj;
		return other.id == this.id && other.metadata == this.metadata;
	}
}
