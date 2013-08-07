package makmods.levelstorage.fluid;

import ic2.api.item.Items;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class FluidElectrolyte extends Fluid {

	public FluidElectrolyte() {
		super("Electrolyte");
		this.setUnlocalizedName("electrolyte");
		this.setGaseous(false);
		// this.setBlockID(ModFluids.instance.blockElectrolyte);
		FluidContainerRegistry.registerFluidContainer(this,
				Items.getItem("electrolyzedWaterCell"), Items.getItem("cell"));
	}
}
