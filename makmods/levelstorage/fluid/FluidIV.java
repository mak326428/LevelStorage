package makmods.levelstorage.fluid;

import net.minecraftforge.fluids.Fluid;

public class FluidIV extends Fluid {

	public FluidIV() {
		super("IV");
		this.setUnlocalizedName("iv");
		this.setGaseous(false);
		//FluidContainerRegistry.registerFluidContainer(this,
		//		Items.getItem("electrolyzedWaterCell"), Items.getItem("cell"));
	}
}
