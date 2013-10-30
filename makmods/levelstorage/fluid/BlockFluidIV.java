package makmods.levelstorage.fluid;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.LSFluids;
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

public class BlockFluidIV extends BlockFluidClassic {

	public BlockFluidIV() {
		super(LevelStorage.configuration.getBlock("fluidIV",
		        ModUniversalInitializer.instance.getNextBlockID()).getInt(),
		        LSFluids.instance.fluidIV, Material.water);
		LSFluids.instance.fluidIV.setBlockID(this);
		this.setUnlocalizedName("iv");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		Icon still = iconRegister
		        .registerIcon(ClientProxy.FLUID_IV_STILL_TEXTURE);
		Icon flowing = iconRegister
		        .registerIcon(ClientProxy.FLUID_IV_FLOWING_TEXTURE);
		
		this.blockIcon = still;
		LSFluids.instance.fluidIV.setStillIcon(still);
		LSFluids.instance.fluidIV.setFlowingIcon(flowing);
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
