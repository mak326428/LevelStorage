package makmods.levelstorage.block;

import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.tileentity.TileEntityIVGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIVGenerator extends BlockMachineStandart implements
		IHasRecipe {

	public BlockIVGenerator(int par1) {
		super(par1);
		TileEntityIVGenerator.getConfig();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityIVGenerator();
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(this), "ASA", "MNM", "ADA",
				'A',
				SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack(),
				'M', new ItemStack(LSBlockItemList.blockMassMelter), 'S',
				new ItemStack(LSBlockItemList.blockMulticoreSolarPanel), 'N',
				new ItemStack(Item.netherStar), 'D', new ItemStack(
						Block.dragonEgg));
	}
}
