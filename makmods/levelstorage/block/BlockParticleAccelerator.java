package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockParticleAccelerator extends BlockMachineStandart implements
		IHasRecipe {

	public BlockParticleAccelerator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockParticleAccelerator), "ImI", "RNR", "TTT",
				Character.valueOf('m'), Items.getItem("massFabricator"),
				Character.valueOf('I'), "ingotIridium", 'R', Items
						.getItem("RTGPellets"), Character.valueOf('N'), Items
						.getItem("advancedMachine"), Character.valueOf('T'),
				Items.getItem("replicator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityParticleAccelerator();
	}

}