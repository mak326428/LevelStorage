package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.tileentity.TileEntityASU;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockASU extends BlockMachineAdvanced implements IHasRecipe {
	public BlockASU(int id) {
		super(id);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityASU();
	}

	@Override
	public void addCraftingRecipe() {
		// TODO: make the recipe more expensive. MUCH MORE EXPENSIVE.
		Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.blockASU),
				"epe", "eme", "epe", 'e', new ItemStack(
						LSBlockItemList.itemAntimatterCrystal), 'm', Items
						.getItem("mfsUnit"), 'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack());
	}

}
