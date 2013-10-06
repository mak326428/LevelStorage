package makmods.levelstorage.block;

import makmods.levelstorage.tileentity.TileEntityCombustibleGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCombustibleGenerator extends BlockMachineStandart {

	public BlockCombustibleGenerator(int id) {
		super(id);
	}

	public static void addCraftingRecipe() {

	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityCombustibleGenerator();
	}
}
