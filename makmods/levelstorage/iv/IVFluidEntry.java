package makmods.levelstorage.iv;

import net.minecraftforge.fluids.Fluid;

public class IVFluidEntry implements IVEntry {

	private final Fluid fluid;
	private final int value;

	public IVFluidEntry(Fluid fl, int value) {
		this.fluid = fl;
		this.value = value;
	}

	@Override
	/**
	 * How much IV does 1 mB of Fluid cost?
	 */
	public int getValue() {
		return value;
	}
	
	public Fluid getFluid() {
		return fluid;
	}

	public IVFluidEntry clone() {
		return new IVFluidEntry(fluid, value);
	}

}
