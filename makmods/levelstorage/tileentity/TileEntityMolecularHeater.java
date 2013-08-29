package makmods.levelstorage.tileentity;

import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.collect.Lists;

public class TileEntityMolecularHeater extends TileEntityBasicMachine implements
        ISidedInventory {

	public TileEntityMolecularHeater() {
		super(9, 32768, false);
	}

	public static final int ENERGY_PER_SMELT = 1000;

	@Override
	public String getInvName() {
		return "Molecular Heater";
	}

	public ItemStack getDischargeStack() {
		return getStackInSlot(8);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (i == 8 && itemstack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) itemstack.getItem();
			if (item.canProvideEnergy(itemstack)
			        && item.getTier(itemstack) >= 2)
				return true;
			else
				return false;
		}
		if (i == 8 && !(itemstack.getItem() instanceof IElectricItem))
			return false;
		if (i == 0 || i == 1 || i == 2 || i == 3) {
			if (FurnaceRecipes.smelting().getSmeltingResult(itemstack) != null)
				return true;
		}
		return false;
	}

	private int discharge(int amount, boolean ignoreLimit) {
		ItemStack itemStack = getDischargeStack();
		if (itemStack == null)
			return 0;

		int energyValue = Info.itemEnergy.getEnergyValue(itemStack);
		if (energyValue == 0)
			return 0;

		Item item = itemStack.getItem();

		if ((item instanceof IElectricItem)) {
			IElectricItem elItem = (IElectricItem) item;

			if ((!elItem.canProvideEnergy(itemStack))) {
				return 0;
			}

			return ElectricItem.manager.discharge(itemStack, amount, 2,
			        ignoreLimit, false);
		}
		itemStack.stackSize -= 1;
		if (itemStack.stackSize <= 0)
			this.setInventorySlotContents(8, null);

		return energyValue;
	}

	public void dischargeItem() {
		if (this.getStored() < (this.getCapacity() - ((IElectricItem) getDischargeStack()
		        .getItem()).getTransferLimit(getDischargeStack())))
			addEnergy(discharge(Integer.MAX_VALUE, false));
	}

	public List<ItemStack> getInputStacks() {
		ItemStack i0 = this.getStackInSlot(0);
		ItemStack i1 = this.getStackInSlot(1);
		ItemStack i2 = this.getStackInSlot(2);
		ItemStack i3 = this.getStackInSlot(3);
		ArrayList<ItemStack> stacks = Lists.newArrayList();
		if (i0 != null)
			stacks.add(i0);
		if (i1 != null)
			stacks.add(i1);
		if (i2 != null)
			stacks.add(i2);
		if (i3 != null)
			stacks.add(i3);
		return stacks;
	}

	public boolean hasWork(int sId) {
		ItemStack inpStack = getStackInSlot(sId);
		if (inpStack == null)
			return false;
		ItemStack outputFor = FurnaceRecipes.smelting().getSmeltingResult(
		        inpStack);
		if (outputFor == null)
			return false;
		ItemStack outpStack = getStackInSlot(sId + 4);
		if (outpStack == null)
			return true;
		else {
			if (outpStack.itemID == outputFor.itemID
			        && outpStack.getItemDamage() == outputFor.getItemDamage()
			        && (64 - outputFor.stackSize) >= 0)
				return true;
			else
				return false;
		}
		// return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!LevelStorage.isSimulating())
			return;
		if (getDischargeStack() != null) {
			ItemStack dischStack = getDischargeStack();
			if (dischStack.getItem() instanceof IElectricItem) {
				IElectricItem eitem = (IElectricItem) dischStack.getItem();
				if (eitem.getTier(dischStack) >= 2
				        && eitem.canProvideEnergy(dischStack)) {
					dischargeItem();
				}
			}
		}
		List<ItemStack> inpStacks = getInputStacks();
		if (inpStacks.size() == 0)
			return;
		if (hasWork(0) || hasWork(1) || hasWork(2) || hasWork(3)) {
			if (this.getStored() > ENERGY_PER_SMELT * 4) {
				if (getInputStacks().size() > 0) {
					this.addProgress(1);
					if (getProgress() >= getMaxProgress()) {
						process(0);
						process(1);
						process(2);
						process(3);
						setProgress(0);
					}
				}
			}
		}
	}

	public void process(int sId) {
		int inputSlot = sId;
		int outputSlot = sId + 4;

		ItemStack outputFor = FurnaceRecipes.smelting().getSmeltingResult(
		        this.getStackInSlot(inputSlot));
		if (outputFor == null)
			return;
		ItemStack outputAlreadyExists = this.getStackInSlot(outputSlot);
		if (outputAlreadyExists == null) {
			this.setInventorySlotContents(outputSlot, outputFor.copy());
			ItemStack inpStack = this.getStackInSlot(inputSlot).copy();
			inpStack.stackSize--;
			if (inpStack.stackSize <= 0) {
				this.setInventorySlotContents(inputSlot, null);
				return;
			}
			this.setInventorySlotContents(inputSlot, inpStack.copy());
			this.addEnergy(-ENERGY_PER_SMELT);
		} else {
			if (outputAlreadyExists.itemID != outputFor.itemID
			        || outputAlreadyExists.getItemDamage() != outputFor
			                .getItemDamage())
				return;
			// OUTPUT STUFF
			ItemStack toBeOut = outputAlreadyExists.copy();
			toBeOut.stackSize += outputFor.stackSize;
			if (toBeOut.stackSize > 64)
				return;
			this.setInventorySlotContents(outputSlot, toBeOut);
			// INPUT STUFF
			ItemStack itemToBeInput = this.getStackInSlot(inputSlot).copy();
			itemToBeInput.stackSize--;
			if (itemToBeInput.stackSize <= 0) {
				this.setInventorySlotContents(inputSlot, null);
				return;
			}
			this.setInventorySlotContents(inputSlot, itemToBeInput.copy());
			this.addEnergy(-ENERGY_PER_SMELT);
		}
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockMolHeater);
	}

	@Override
	public void onUnloaded() {
		;
	}

	@Override
	public int getMaxInput() {
		return 512;
	}

	@Override
	public void onLoaded() {
		;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[var1];
		switch (side) {
			case UP:
				return new int[] { 0, 1, 2, 3 };
			case NORTH:
				return new int[] { 4, 5, 6, 7 };
			case SOUTH:
				return new int[] { 4, 5, 6, 7 };
			case WEST:
				return new int[] { 4, 5, 6, 7 };
			case EAST:
				return new int[] { 4, 5, 6, 7 };
			default:
				return new int[] {};
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		if (side == ForgeDirection.UP.ordinal())
			return isItemValidForSlot(slot, itemstack);
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[j];
		switch (side) {
			case WEST:
				return true;
			case EAST:
				return true;
			case NORTH:
				return true;
			case SOUTH:
				return true;
			default:
				return false;
		}
	}

	@Override
	public int getMaxProgress() {
		return 4;
	}

}
