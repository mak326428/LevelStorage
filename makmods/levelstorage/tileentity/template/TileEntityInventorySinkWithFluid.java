package makmods.levelstorage.tileentity.template;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileEntityInventorySinkWithFluid extends
		TileEntityInventorySink implements IFluidHandler {

	public FluidTank tank;

	public TileEntityInventorySinkWithFluid(int inventorySize, int tankSize) {
		super(inventorySize);
		tank = new FluidTank(tankSize * 1000);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.tank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.tank.writeToNBT(fluidTankTag);
		nbttagcompound.setTag("fluidTank", fluidTankTag);
	}

	public FluidTank getFluidTank() {
		return tank;
	}

	public int gaugeLiquidScaled(int i) {
		if (getFluidTank().getFluidAmount() <= 0)
			return 0;

		return getFluidTank().getFluidAmount() * i
				/ getFluidTank().getCapacity();
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	public int gaugeEnergyScaled(int i) {
		if (getStored() <= 0) {
			return 0;
		}
		int r = getStored() * i / getCapacity();
		if (r > i) {
			r = i;
		}
		return r;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

}
