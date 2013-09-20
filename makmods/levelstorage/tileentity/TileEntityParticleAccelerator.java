package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.api.ParticleAcceleratorRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityParticleAccelerator extends TileEntityBasicMachine
		implements ISidedInventory {

	public TileEntityParticleAccelerator() {
		super(2, 2000000, false);
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
			return new int[] { 0 };
		else
			return new int[] { 1 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public int getMaxProgress() {
		return 40;
	}

	@Override
	public void onUnloaded() {
	}

	@Override
	public int getMaxInput() {
		return 2048;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i != 0)
			return false;
		if (ParticleAcceleratorRegistry.getValueFor(itemstack) != -1)
			return true;
		return false;
	}

	@Override
	public void onLoaded() {

	}

}
