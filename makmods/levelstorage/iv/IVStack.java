package makmods.levelstorage.iv;

import makmods.levelstorage.init.LSFluids;
import net.minecraftforge.fluids.FluidStack;

public class IVStack {
	private int leftoverIV;
	private int mbAmount;
	
	public IVStack(int mbAmt, int leftover) {
		this.leftoverIV = leftover;
		this.mbAmount = mbAmt;
	}
	
	public int getLeftoverIV() {
		return leftoverIV;
	}

	public void setLeftoverIV(int leftoverIV) {
		this.leftoverIV = leftoverIV;
	}

	public int getMbAmount() {
		return mbAmount;
	}

	public void setMbAmount(int mbAmount) {
		this.mbAmount = mbAmount;
	}

	public FluidStack toFluidStack() {
		return new FluidStack(LSFluids.instance.fluidIV, mbAmount);
	}
}
