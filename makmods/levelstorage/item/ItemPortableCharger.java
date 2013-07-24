package makmods.levelstorage.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPortableCharger extends Item implements IInventory {

	public void writeNBT(ItemStack stack, PortableChargerNBTInfo info) {

		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();

		NBTTagCompound nbt = stack.stackTagCompound;

		if (info.getChargingStack() != null) {
			info.getChargingStack().writeToNBT(nbt);
		}
	}

	public ItemPortableCharger(int par1) {
		super(par1);
	}

	public class PortableChargerNBTInfo {
		private ItemStack chargingStack;
		private int frequency;

		public PortableChargerNBTInfo(ItemStack stack, int frequency) {
			this.chargingStack = stack;
			this.frequency = frequency;
		}

		public ItemStack getChargingStack() {
			return chargingStack;
		}

		public void setChargingStack(ItemStack chargingStack) {
			this.chargingStack = chargingStack;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}
	}

}
