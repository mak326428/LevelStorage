package makmods.levelstorage.init;

/**
 * If an item/block gets loaded without this interface implemented, it is marked
 * as a item/block that has no recipe. Otherwise, {@link #addCraftingRecipe()}
 * is invoked when needed.
 * 
 * @author mak326428
 * 
 */
public interface IHasRecipe {
	/**
	 * Invoked by {@link ModUniversalInitializer} when an item is past its
	 * initialization state and recipe is to be added.
	 */
	void addCraftingRecipe();
}
