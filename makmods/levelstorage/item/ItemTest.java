package makmods.levelstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemTest extends Item {

	public ItemTest(int id) {
		super(id);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		
	}
}
