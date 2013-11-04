package makmods.levelstorage.iv.parsers.recipe;


public class OreDictRecipeCompound extends IWrappedRecipeCompound {
	
	private final String name;

	public OreDictRecipeCompound(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "OreDictRC: " + name;
	}

}
