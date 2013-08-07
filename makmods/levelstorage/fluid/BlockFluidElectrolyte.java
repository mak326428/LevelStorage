package makmods.levelstorage.fluid;

import java.util.Random;

import buildcraft.energy.render.EntityDropParticleFX;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModFluids;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidElectrolyte extends BlockFluidClassic {
	
	protected float particleRed;
	protected float particleGreen;
	protected float particleBlue;

	public static String UNLOC_NAME = "blockFluidElectrolyte";
	public static String NAME = "Electrolyte";

	public BlockFluidElectrolyte() {
		super(LevelStorage.configuration.getBlock(UNLOC_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(),
				ModFluids.instance.fluidElectrolyte, Material.water);
		ModFluids.instance.fluidElectrolyte.setBlockID(this);
		this.particleBlue = 255;
		this.particleGreen = 182;
		this.particleRed = 125;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		Icon c = iconRegister.registerIcon(ClientProxy.FLUID_ELECTROLYTE_TEXTURE);
		this.blockIcon = c;
		ModFluids.instance.fluidElectrolyte.setIcons(c);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);

		if (rand.nextInt(10) == 0 && world.doesBlockHaveSolidTopSurface(x, y - 1, z) && !world.getBlockMaterial(x, y - 2, z).blocksMovement()) {
			double px = (double) ((float) x + rand.nextFloat());
			double py = (double) y - 1.05D;
			double pz = (double) ((float) z + rand.nextFloat());
			
			EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}
	
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlockMaterial(x,  y,  z).isLiquid()) return false;
		return super.canDisplace(world, x, y, z);
	}
	
	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlockMaterial(x,  y,  z).isLiquid()) return false;
		return super.displaceIfPossible(world, x, y, z);
	}

}
