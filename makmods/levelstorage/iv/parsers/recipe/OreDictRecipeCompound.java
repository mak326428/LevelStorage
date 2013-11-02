package makmods.levelstorage.iv.parsers.recipe;

import makmods.levelstorage.logic.util.CommonHelper;

public class OreDictRecipeCompound extends IWrappedRecipeCompound {
	
	private final String name;

	public OreDictRecipeCompound(String name) {
		this.name = name;
	}

	@Override
	public CompoundType getType() {
		return CompoundType.OREDICT;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "OreDictRC: " + name;
	}

}
