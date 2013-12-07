package makmods.levelstorage.tileentity;

import makmods.levelstorage.init.LSFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityIVGenerator extends TileEntity implements IFluidHandler {
	public boolean sunIsUp, skyIsVisible, noSunWorld, wetBiome, initialized;
	public static final double PLUS_EVERY_TICK = 2.0D;
	public static final int UNITS_PER_MB = 10;
	public int ticker = 0;
	public double internalIVTicker = 0;

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setDouble("ivInternal", internalIVTicker);
		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.tank.writeToNBT(fluidTankTag);
		par1NBTTagCompound.setTag("fluidTank", fluidTankTag);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.internalIVTicker = par1NBTTagCompound.getDouble("ivInternal");
		this.tank.readFromNBT(par1NBTTagCompound.getCompoundTag("fluidTank"));
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

	public FluidTank tank = new FluidTank(16 * 1000);

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	public void initialize() {
		wetBiome = worldObj.getWorldChunkManager()
				.getBiomeGenAt(xCoord, zCoord).getIntRainfall() > 0;
		noSunWorld = worldObj.provider.hasNoSky;
		updateVisibility();
		initialized = true;
	}

	public void updateVisibility() {
		Boolean rainWeather = wetBiome
				&& (worldObj.isRaining() || worldObj.isThundering());

		if ((!worldObj.isDaytime()) || (rainWeather)) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}

		if (!worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)
				|| (noSunWorld)) {
			skyIsVisible = false;
		} else {
			skyIsVisible = true;
		}
	}

	public int tickRate() {
		return 20;
	}

	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		if (!initialized)
			initialize();
		ticker++;
		if (ticker % tickRate() == 0)
			updateVisibility();
		boolean shouldProduce = skyIsVisible && sunIsUp;
		if (shouldProduce)
			produceIV();
	}

	public void produceIV() {
		if (this.tank.getFluidAmount() == this.tank.getCapacity())
			return;
		internalIVTicker += PLUS_EVERY_TICK;
		this.internalIVTicker -= UNITS_PER_MB
				* this.tank.fill(new FluidStack(LSFluids.instance.fluidIV,
						(int) (internalIVTicker / UNITS_PER_MB)), true);
	}
}
