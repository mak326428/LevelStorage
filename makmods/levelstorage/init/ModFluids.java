package makmods.levelstorage.init;

import makmods.levelstorage.fluid.BlockFluidElectrolyte;
import makmods.levelstorage.fluid.FluidElectrolyte;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModFluids {

	public static ModFluids instance = new ModFluids();
	public FluidElectrolyte fluidElectrolyte;
	public BlockFluidElectrolyte blockElectrolyte;

	private ModFluids() {
	}

	public void init() {
		this.fluidElectrolyte = new FluidElectrolyte();
		FluidRegistry.registerFluid(this.fluidElectrolyte);
		this.blockElectrolyte = new BlockFluidElectrolyte();
		GameRegistry.registerBlock(this.blockElectrolyte,
		        BlockFluidElectrolyte.UNLOC_NAME);
		LanguageRegistry.addName(blockElectrolyte, BlockFluidElectrolyte.NAME);
	}

}
