package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.gui.client.GUIParticleAccelerator;
import makmods.levelstorage.gui.container.ContainerParticleAccelerator;
import makmods.levelstorage.gui.logicslot.HelperLogicSlot;
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
	
	public HelperLogicSlot sampleSlot;
	public HelperLogicSlot inputSlot;
	public HelperLogicSlot outputSlot;

	public TileEntityParticleAccelerator() {
		super(3, 2000000, false);
		sampleSlot = new HelperLogicSlot(this, 0);
		inputSlot = new HelperLogicSlot(this, 1);
		outputSlot = new HelperLogicSlot(this, 2);
	}

	public int mode = ANTIMATTER_PRODUCTION_MODE;

	public static final int ANTIMATTER_PRODUCTION_MODE = 0;
	public static final int MATTER_RESHAPING_MODE = 1;

	public static final int ANTIMATTER_IV = 32768;

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("modePA", mode);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		mode = par1NBTTagCompound.getInteger("modePA");
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

	public boolean hasMatterReshapingWork() {
		int ivForSample = IVRegistry.instance.getValueFor(getPattern());
		if (ivForSample == IVRegistry.NOT_FOUND)
			return false;
		{
			ItemStack thing = this.getPattern().copy();
			thing.stackSize = 1;
			if (!InventoryUtil.addToInventory(this, 2, thing, true))
				return false;
		}
		if (getStackInSlot(1) == null)
			return false;
		int totalInInput = 0;
		int ivForInput = IVRegistry.instance.getValueFor(getStackInSlot(1));
		ItemStack stackInput = getStackInSlot(1).copy();
		for (int i = 0; i < stackInput.stackSize; i++) {
			totalInInput += ivForInput;
		}
		if (totalInInput >= ivForSample)
			return true;
		return false;
	}
	
	public void workMatterReshaping() {
		
	}

	public void updateEntity() {
		super.updateEntity();
		if (!LevelStorage.isSimulating())
			return;
		ItemStack pattern = getPattern();
		if (pattern == null) {
			if (mode == MATTER_RESHAPING_MODE)
				return;
		}
		if (mode == MATTER_RESHAPING_MODE) {
			if (hasMatterReshapingWork()) {
				if (canUse(ENERGY_COST_PER_TICK)) {
					use(ENERGY_COST_PER_TICK);
					this.addProgress(1);
					if (getProgress() >= getMaxProgress()) {
						setProgress(0);
						workMatterReshaping();
					}
				}
			}
		} else {
			
		}
	}

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
