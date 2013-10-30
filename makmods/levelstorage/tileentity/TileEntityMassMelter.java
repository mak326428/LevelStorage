package makmods.levelstorage.tileentity;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.client.GUIMassMelter;
import makmods.levelstorage.gui.container.ContainerMassMelter;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventorySinkWithFluid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMassMelter extends TileEntityInventorySinkWithFluid
		implements ISidedInventory, ITEHasGUI {

	private int progress;
	public int ivBuffer = 0;
	public static final int EU_PER_IV = 30;
	public static final int OPERATIONS_PER_TICK = 100 / IVRegistry.IV_TO_FLUID_CONVERSION
			.getKey();

	public LogicSlot[] slots;
	public int maxProgress = 1;

	public TileEntityMassMelter() {
		super(9, 64);
		slots = new LogicSlot[getSizeInventory()];
		for (int i = 0; i < slots.length; i++)
			slots[i] = new LogicSlot(this, i);
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public int gaugeProgressScaled(int i) {
		if (getProgress() <= 0) {
			return 0;
		}
		int r = getProgress() * i / getMaxProgress();
		if (r > i) {
			r = i;
		}
		return r;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void addProgress(int progress) {
		this.progress += progress;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ivBuffer = nbttagcompound.getInteger("buffer");
		maxProgress = nbttagcompound.getInteger("maxProg");
		progress = nbttagcompound.getInteger("progress");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("buffer", ivBuffer);
		nbttagcompound.setInteger("maxProg", maxProgress);
		nbttagcompound.setInteger("progress", progress);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public String getInvName() {
		return "Mass Melter";
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return IVRegistry.instance.getValueFor(itemstack) != IVRegistry.NOT_FOUND;
	}

	@Override
	public int getCapacity() {
		return 400000;
	}

	@Override
	public void onUnloaded() {

	}

	@Override
	public int getMaxInput() {
		return 8192;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!LevelStorage.isSimulating())
			return;
		if (progress == 0)
			progress = -1;
		if (!canUse(EU_PER_IV))
			return;
		for (int i = 0; i < OPERATIONS_PER_TICK; i++) {
			if (!canUse(EU_PER_IV))
				break;
			if (this.ivBuffer < IVRegistry.IV_TO_FLUID_CONVERSION.getKey()) {
				int plusBuffer = 0;
				{
					for (LogicSlot slot : slots) {
						int retrieved = 0;
						{
							if (slot.get() != null) {
								ItemStack gauge = slot.get().copy();
								gauge.stackSize = 1;
								int ivFor = IVRegistry.instance
										.getValueFor(gauge);
								slot.consume(1);
								if (ivFor != IVRegistry.NOT_FOUND) {
									retrieved += ivFor;
									maxProgress = ivFor;
								}
							}
						}
						plusBuffer += retrieved;
						if (retrieved > 0)
							break;
					}
				}
				this.ivBuffer += plusBuffer;
			} else {
				// consume energy to transfer one IV into 1 mB of liquid IV
				int freeSpaceInTank = this.getFluidTank().getCapacity()
						- this.getFluidTank().getFluidAmount();
				if (freeSpaceInTank >= 0) {
					if (this.fill(ForgeDirection.UNKNOWN, new FluidStack(
							LSFluids.instance.fluidIV,
							IVRegistry.IV_TO_FLUID_CONVERSION.getValue()), true) != 0) {
						use(EU_PER_IV);
						this.ivBuffer -= IVRegistry.IV_TO_FLUID_CONVERSION
								.getKey();
						freeSpaceInTank--;
					}
				} else
					break;
			}
		}
		this.setProgress(ivBuffer >= IVRegistry.IV_TO_FLUID_CONVERSION
				.getValue() ? ivBuffer : 0);
	}

	@Override
	public void onLoaded() {

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (dir == ForgeDirection.UP)
			return isItemValidForSlot(slot, itemstack);
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return slot == 2 && side != ForgeDirection.UP.ordinal();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIMassMelter(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerMassMelter(player.inventory, this);
	}

}
