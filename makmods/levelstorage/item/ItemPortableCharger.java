package makmods.levelstorage.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPortableCharger extends Item implements IInventory {
	
	public static final String NBT_FREQUENCY = "frequency";

	public void writeNBT(ItemStack stack, PortableChargerNBTInfo info) {

		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();

		NBTTagCompound nbt = stack.stackTagCompound;

		if (info.getChargingStack() != null) {
			info.getChargingStack().writeToNBT(nbt);
		}
		
		nbt.setInteger(NBT_FREQUENCY, info.getFrequency());
	}
	
	public PortableChargerNBTInfo getNBT(ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();

		NBTTagCompound nbt = stack.stackTagCompound;
		
		PortableChargerNBTInfo info = new PortableChargerNBTInfo();
		
		ItemStack stackCharging = ItemStack.loadItemStackFromNBT(nbt);
		if (stackCharging != null)
			info.setChargingStack(stackCharging);
		
		if (nbt.hasKey(NBT_FREQUENCY)) {
			info.setFrequency(nbt.getInteger(NBT_FREQUENCY));
		}
		
		return info;
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
		
		public PortableChargerNBTInfo() {
			this.frequency = 0;
			this.chargingStack = null;
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
