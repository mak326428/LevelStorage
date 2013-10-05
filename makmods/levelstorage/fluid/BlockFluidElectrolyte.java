package makmods.levelstorage.fluid;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.ModFluids;
import makmods.levelstorage.init.ModUniversalInitializer;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidElectrolyte extends BlockFluidClassic {

	public static String UNLOC_NAME = "blockFluidElectrolyte";
	public static String NAME = "Electrolyte";

	public BlockFluidElectrolyte() {
		super(LevelStorage.configuration.getBlock(UNLOC_NAME,
		        3244).getInt(),
		        ModFluids.instance.fluidElectrolyte, Material.water);
		ModFluids.instance.fluidElectrolyte.setBlockID(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		Icon c = iconRegister
		        .registerIcon(ClientProxy.FLUID_ELECTROLYTE_TEXTURE);
		this.blockIcon = c;
		ModFluids.instance.fluidElectrolyte.setIcons(c);
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlockMaterial(x, y, z).isLiquid())
			return false;
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlockMaterial(x, y, z).isLiquid())
			return false;
		return super.displaceIfPossible(world, x, y, z);
	}

}
