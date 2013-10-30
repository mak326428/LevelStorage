package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockParticleAccelerator extends BlockMachineStandart implements IHasRecipe {

	public BlockParticleAccelerator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockParticleAccelerator), "isi", "crc", "mpm",
				Character.valueOf('i'), IC2Items.IRIDIUM_PLATE.copy(),
				Character.valueOf('c'), IC2Items.ADV_CIRCUIT, Character
						.valueOf('m'), IC2Items.ADV_MACHINE, Character
						.valueOf('s'), Items.getItem("scanner"), Character
						.valueOf('p'), Items.getItem("patternstorage"),
				Character.valueOf('r'), Items.getItem("replicator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityParticleAccelerator();
	}

}