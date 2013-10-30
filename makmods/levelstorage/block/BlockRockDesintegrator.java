package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.block.item.ItemBlockRockDesintegrator;
import makmods.levelstorage.init.CustomItemBlock;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.Lists;

@CustomItemBlock(itemBlock=ItemBlockRockDesintegrator.class)
public class BlockRockDesintegrator extends BlockMachineStandart implements IHasRecipe {

	public BlockRockDesintegrator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		List<ItemStack> lavas = Lists.newArrayList();
		List<ItemStack> waters = Lists.newArrayList();
		for (FluidContainerData d : FluidContainerRegistry
				.getRegisteredFluidContainerData()) {
			if (d.fluid.fluidID == FluidRegistry.WATER.getID())
				waters.add(d.filledContainer.copy());
			if (d.fluid.fluidID == FluidRegistry.LAVA.getID())
				lavas.add(d.filledContainer.copy());
		}
		for (ItemStack lava : lavas)
			for (ItemStack water : waters) {
				Recipes.advRecipes.addRecipe(new ItemStack(
						LSBlockItemList.blockRockDesintegrator), "lll", "cmc",
						"www", Character.valueOf('l'), lava, Character
								.valueOf('w'), water, Character.valueOf('c'),
						IC2Items.BASIC_CIRCUIT, Character.valueOf('m'), Items
								.getItem("machine"));
			}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityRockDesintegrator();
	}

}