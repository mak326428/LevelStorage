package makmods.levelstorage.tileentity;

import makmods.levelstorage.gui.client.GUIIVGenerator;
import makmods.levelstorage.gui.container.ContainerIVGenerator;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.init.Config;
import makmods.levelstorage.init.Config.LSConfigCategory;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityIVGenerator extends TileEntityInventory implements IFluidHandler, ITEHasGUI {
	public boolean sunIsUp, skyIsVisible, noSunWorld, wetBiome, initialized;
	public static int IV_PER_TICK = 20;
	public int ticker = 0;
	public static int BUFF_IV_T = 5;
	public double internalIVTicker = 0;
	public int latestSpeed;
	public LogicSlot upgradeSlot;
	
	public TileEntityIVGenerator() {
		super(1);
		upgradeSlot = new LogicSlot(this, 0);
	}
	
	public static void getConfig() {
		IV_PER_TICK = Config.getInt(LSConfigCategory.IV, "IVGenerator_Rate", 20, "Speed at which IV Generators produce IV. (per tick)");
	}

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
	
	public int calculateBuffIV() {
		if (upgradeSlot.get() == null)
			return 0;
		if (!isItemValidForSlot(0, upgradeSlot.get()))
			return 0;
		ItemStack is = upgradeSlot.get();
		return BUFF_IV_T * is.stackSize;
	}

	public void produceIV() {
		if (this.tank.getFluidAmount() == this.tank.getCapacity())
			return;
		int mb = IVRegistry.IV_TO_FLUID_CONVERSION.getKey();
		latestSpeed = IV_PER_TICK + calculateBuffIV();
		internalIVTicker += latestSpeed;
		this.internalIVTicker -= mb
				* this.tank.fill(new FluidStack(LSFluids.instance.fluidIV,
						(int) (internalIVTicker / (double)mb)), true);
	}

	@Override
	public String getInvName() {
		return "IV Generator";
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return CommonHelper.areStacksEqual(SimpleItemShortcut.IV_GENERATOR_UPGRADE.getItemStack(), itemstack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIIVGenerator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerIVGenerator(player.inventory, this);
	}
}
