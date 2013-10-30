package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMassMelter extends BlockMachineStandart implements IHasRecipe {

	public BlockMassMelter(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockMassMelter), "ata", "cic", "mem", 'm',
				Items.getItem("massFabricator"), 'e', Items
						.getItem("energyCrystal"), 't', Items
						.getItem("teleporter"), 'a', Items
						.getItem("advancedMachine"), 'c', Items
						.getItem("advancedCircuit"), 'i', Items
						.getItem("iridiumPlate"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityMassMelter();
	}
}
