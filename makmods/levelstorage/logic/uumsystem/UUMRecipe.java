package makmods.levelstorage.logic.uumsystem;

import net.minecraft.item.ItemStack;

// Not a IRecipe.
/**
 * Simple class for wrapping uum recipes
 * @author mak326428
 */
public class UUMRecipe {
	// GETTERS AND SETTERS ZONE
	public int getUumCost() {
		return uumCost;
	}

	public void setUumCost(int uumCost) {
		this.uumCost = uumCost;
	}

	public ItemStack getOutput() {
		return output;
	}

	public void setOutput(ItemStack output) {
		this.output = output;
	}
	// END OF GETTERS AND SETTERS ZONE
	private int uumCost;
	private ItemStack output;

	public UUMRecipe(ItemStack output, int uumCost) {
		this.uumCost = uumCost;
		this.output = output;
	}
}
