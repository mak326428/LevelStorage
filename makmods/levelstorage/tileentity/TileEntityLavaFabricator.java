package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.gui.client.GUILavaFabricator;
import makmods.levelstorage.gui.container.ContainerLavaFabricator;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventorySinkWithFluid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLavaFabricator extends TileEntityInventorySinkWithFluid
		implements ITEHasGUI, ISidedInventory {

	public static final int EU_PER_LAVA_MB = 25;

	public LogicSlot fluidInput;
	public LogicSlot fluidOutput;

	public TileEntityLavaFabricator() {
		super(2, 16);
		this.fluidInput = new LogicSlot(this, 0);
		this.fluidOutput = new LogicSlot(this, 1);
	}

	@Override
	public String getInvName() {
		return "Lava Fabricator";
	}

	public boolean isValidInputInSlot(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		ItemStack copy = itemstack.copy();
		ItemStack filled = FluidContainerRegistry.fillFluidContainer(
				new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME), copy);
		return filled != null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0)
			return isValidInputInSlot(itemstack);
		return false;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockLavaFabricator);
	}

	@Override
	public int getCapacity() {
		return 32768;
	}

	@Override
	public void onUnloaded() {
	}

	@Override
	public int getMaxInput() {
		return 512;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	@Override
	public void onLoaded() {
		;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	public void fillContainerIfPossible() {
		if (this.fluidInput.get() == null)
			return;
		ItemStack copy = this.fluidInput.get().copy();
		ItemStack filled = FluidContainerRegistry.fillFluidContainer(
				new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME), copy);
		if (this.tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME)
			if (fluidOutput.add(filled, true)) {
				fluidInput.consume(1);
				this.tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
				fluidOutput.add(filled, false);
			}
	}

	public void updateEntity() {
		super.updateEntity();
		this.fillContainerIfPossible();
		while (canUse(EU_PER_LAVA_MB)) {
			boolean used = false;
			if (this.tank.getCapacity() - this.tank.getFluidAmount() >= 1) {
				this.tank.setFluid(new FluidStack(FluidRegistry.LAVA, this.tank
						.getFluidAmount() + 1));
				used = true;
			}
			if (!used)
				break;
			else
				use(EU_PER_LAVA_MB);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUILavaFabricator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerLavaFabricator(player.inventory, this);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		if (i == 1)
			return true;
		else
			return false;
	}

}
