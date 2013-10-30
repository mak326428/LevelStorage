package makmods.levelstorage.fluid;

import ic2.api.item.Items;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class FluidIV extends Fluid {

	public FluidIV() {
		super("IV");
		this.setUnlocalizedName("iv");
		this.setGaseous(false);
		//FluidContainerRegistry.registerFluidContainer(this,
		//		Items.getItem("electrolyzedWaterCell"), Items.getItem("cell"));
	}
}
