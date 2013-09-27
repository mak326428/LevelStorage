package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.client.GUIParticleAccelerator;
import makmods.levelstorage.gui.container.ContainerParticleAccelerator;
import makmods.levelstorage.gui.logicslot.HelperLogicSlot;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.util.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityParticleAccelerator extends TileEntityBasicMachine
		implements ISidedInventory, ITEHasGUI, IHasButtons {

	public TileEntityParticleAccelerator() {
		super(3, 2000000, false);
		sampleSlot = new HelperLogicSlot(this, 0);
		inputSlot = new HelperLogicSlot(this, 1);
		outputSlot = new HelperLogicSlot(this, 2);
	}

	public int mode = ANTIMATTER_PRODUCTION_MODE;

	public static final int ANTIMATTER_PRODUCTION_MODE = 0;
	public static final int MATTER_RESHAPING_MODE = 1;

	public static final ItemStack ANTIMATTER_IS = SimpleItems.instance
			.getIngredient(9);

	public static final int ANTIMATTER_IV = 32768;

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("modePA", mode);
		if (bufferStack != null) {
			NBTTagCompound tc = new NBTTagCompound();
			bufferStack.writeToNBT(tc);
			par1NBTTagCompound.setCompoundTag("bufferStack", tc);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		mode = par1NBTTagCompound.getInteger("modePA");
		if (par1NBTTagCompound.hasKey("bufferStack")) {
			bufferStack = ItemStack.loadItemStackFromNBT(par1NBTTagCompound
					.getCompoundTag("bufferStack"));
		}
	}

	@Override
	public String getInvName() {
		return "Particle Accelerator";
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockParticleAccelerator);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		ForgeDirection dir = ForgeDirection.getOrientation(var1);
		if (dir.equals(ForgeDirection.UP))
			return new int[] { 0, 1 };
		else
			return new int[] { 2 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		if (j == ForgeDirection.UP.ordinal())
			return isItemValidForSlot(i, itemstack);
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		ForgeDirection dirhit = ForgeDirection.getOrientation(j);
		switch (dirhit) {
		case EAST: {
			return i == 2;
		}
		case NORTH: {
			return i == 2;
		}
		case WEST: {
			return i == 2;
		}
		case SOUTH: {
			return i == 2;
		}
		default:
			return false;
		}
	}

	@Override
	public int getMaxProgress() {
		return 400;
	}

	@Override
	public void onUnloaded() {
	}

	@Override
	public int getMaxInput() {
		return 8192;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 2)
			return false;
		if (IVRegistry.instance.getValueFor(itemstack) != IVRegistry.NOT_FOUND)
			return true;
		return false;
	}

	@Override
	public void onLoaded() {

	}

	public static final int ENERGY_COST_PER_TICK = 1024;

	public ItemStack getPattern() {
		return getStackInSlot(0);
	}

	public HelperLogicSlot sampleSlot;
	public HelperLogicSlot inputSlot;
	public HelperLogicSlot outputSlot;

	public boolean hasMRWork() {
		if (sampleSlot.get() == null)
			return false;
		if (inputSlot.get() == null)
			return false;
		ItemStack tmp1 = getPatternIdeal().copy();
		tmp1.stackSize = getRequiredInputForOutput().plusOutput;
		if (!outputSlot.add(tmp1, true))
			return false;
		int totalIVInInput = 0;
		final int ivForOneItemInInput = IVRegistry.instance
				.getValueFor(inputSlot.get());
		for (int i = 0; i < inputSlot.get().stackSize; i++)
			totalIVInInput += ivForOneItemInInput;
		if (totalIVInInput >= IVRegistry.instance.getValueFor(sampleSlot.get()))
			return true;
		if (outputSlot.get() != null) {
			if (outputSlot.get().itemID != sampleSlot.get().itemID
					|| outputSlot.get().getItemDamage() != sampleSlot.get()
							.getItemDamage())
				return false;
		}
		return false;
	}

	public InputMinusOutputPlusRatio getRequiredInputForOutput() {
		ItemStack st = sampleSlot.get();
		if (st == null)
			return null;
		int sampleIV = IVRegistry.instance.getValueFor(st);
		if (sampleIV == IVRegistry.NOT_FOUND)
			return null;
		if (inputSlot.get() == null)
			return null;
		int itemInInputIV = IVRegistry.instance.getValueFor(inputSlot.get());
		if (itemInInputIV == IVRegistry.NOT_FOUND)
			return null;
		if (sampleIV > itemInInputIV)
			return new InputMinusOutputPlusRatio(
					(int) Math.ceil(((float) sampleIV)
							/ ((float) itemInInputIV)), 1);
		else
			// so reshaping is not equivalent.
			// previously 255 (tin) would give you 4x64 (redstone), however it's
			// not balanced
			return new InputMinusOutputPlusRatio(1,
					(int) Math.floor(((float) itemInInputIV)
							/ ((float) sampleIV)));
	}

	public static class InputMinusOutputPlusRatio {
		public int minusInput;
		public int plusOutput;

		public InputMinusOutputPlusRatio(int mI, int pO) {
			minusInput = mI;
			plusOutput = pO;
		}
	}

	public ItemStack getPatternIdeal() {
		ItemStack pEx = sampleSlot.get().copy();
		pEx.stackSize = 1;
		return pEx;
	}

	public InputMinusOutputPlusRatio getRequiredInputForAntimatter() {
		if (inputSlot.get() == null)
			return null;
		int itemInInputIV = IVRegistry.instance.getValueFor(inputSlot.get());
		if (itemInInputIV == IVRegistry.NOT_FOUND)
			return null;
		if (ANTIMATTER_IV > itemInInputIV)
			return new InputMinusOutputPlusRatio(
					(int) Math.ceil(((float) ANTIMATTER_IV)
							/ ((float) itemInInputIV)), 1);
		else
			return new InputMinusOutputPlusRatio(1,
					(int) Math.ceil(((float) itemInInputIV)
							/ ((float) ANTIMATTER_IV)));
	}

	public void handleAntimatterTick() {
		if (!hasAntimatterWork())
			return;
		InputMinusOutputPlusRatio requiredInput = getRequiredInputForAntimatter();
		if (requiredInput == null)
			return;
		if (getProgress() == 0) {
			if (outputSlot.add(ANTIMATTER_IS, true)) {
				inputSlot.consume(requiredInput.minusInput);
				ItemStack pIdeal = ANTIMATTER_IS.copy();
				pIdeal.stackSize = requiredInput.plusOutput;
				outputSlot.add(pIdeal, false);
			}
		}
		if (canUse(ENERGY_COST_PER_TICK)) {
			use(ENERGY_COST_PER_TICK);
			addProgress(1);
			if (getProgress() >= getMaxProgress())
				setProgress(0);
		}
	}

	public boolean hasAntimatterWork() {
		if (inputSlot.get() == null)
			return false;
		if (!outputSlot.add(ANTIMATTER_IS, true))
			return false;
		int totalIVInInput = 0;
		final int ivForOneItemInInput = IVRegistry.instance
				.getValueFor(inputSlot.get());
		for (int i = 0; i < inputSlot.get().stackSize; i++)
			totalIVInInput += ivForOneItemInInput;
		if (totalIVInInput >= ANTIMATTER_IV)
			return true;
		return false;
	}

	public void handleMatterTick() {
		if (!hasMRWork())
			return;
		InputMinusOutputPlusRatio requiredInput = getRequiredInputForOutput();
		if (requiredInput == null)
			return;
		if (getProgress() == 0) {
			if (outputSlot.add(getPatternIdeal(), true)) {
				inputSlot.consume(requiredInput.minusInput);
				ItemStack pIdeal = getPatternIdeal().copy();
				pIdeal.stackSize = requiredInput.plusOutput;
				outputSlot.add(pIdeal, false);
			}
		}
		if (canUse(ENERGY_COST_PER_TICK)) {
			use(ENERGY_COST_PER_TICK);
			addProgress(1);
			if (getProgress() >= getMaxProgress())
				setProgress(0);
		}
	}

	public void updateEntity() {
		super.updateEntity();
		if (!LevelStorage.isSimulating())
			return;
		switch (mode) {
		case ANTIMATTER_PRODUCTION_MODE:
			handleAntimatterTick();
			break;
		case MATTER_RESHAPING_MODE:
			handleMatterTick();
			break;
		}
	}

	public ItemStack bufferStack;

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIParticleAccelerator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerParticleAccelerator(player.inventory, this);
	}

	@Override
	public void handleButtonClick(int buttonId) {
		if (this.mode == 0)
			this.mode = 1;
		else
			this.mode = 0;
	}

}
