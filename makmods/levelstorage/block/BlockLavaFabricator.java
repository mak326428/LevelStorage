package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLavaFabricator extends BlockMachineStandart implements IHasRecipe {

	public BlockLavaFabricator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockLavaFabricator), "aca", "pgp", Character
				.valueOf('a'), IC2Items.ADV_CIRCUIT, Character.valueOf('p'),
				IC2Items.ADV_ALLOY, Character.valueOf('c'),
				IC2Items.ADV_MACHINE, Character.valueOf('g'), Items
						.getItem("geothermalGenerator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLavaFabricator();
	}
}
