package makmods.levelstorage.tileentity;

import makmods.levelstorage.ModItems;
import makmods.levelstorage.item.ItemFrequencyCard;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWirelessConductor extends TileEntity {

	public ItemStack insertedCard = null;

	public TileEntityWirelessConductor() {
		
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey(ItemFrequencyCard.NBT_DIM_ID)) {
			if (insertedCard == null)
				insertedCard = new ItemStack(ModItems.instance.itemFreqCard);
			if (insertedCard.stackTagCompound == null)
				insertedCard.stackTagCompound = new NBTTagCompound();
			insertedCard.stackTagCompound.setBoolean(
					ItemFrequencyCard.NBT_WAS_USED, true);
			insertedCard.stackTagCompound
					.setInteger(ItemFrequencyCard.NBT_DIM_ID,
							par1NBTTagCompound
									.getInteger(ItemFrequencyCard.NBT_DIM_ID));
			insertedCard.stackTagCompound.setInteger(
					ItemFrequencyCard.NBT_X_POS,
					par1NBTTagCompound.getInteger(ItemFrequencyCard.NBT_X_POS));
			insertedCard.stackTagCompound.setInteger(
					ItemFrequencyCard.NBT_Y_POS,
					par1NBTTagCompound.getInteger(ItemFrequencyCard.NBT_Y_POS));
			insertedCard.stackTagCompound.setInteger(
					ItemFrequencyCard.NBT_Z_POS,
					par1NBTTagCompound.getInteger(ItemFrequencyCard.NBT_Z_POS));
		}
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		if (insertedCard != null) {
			par1NBTTagCompound.setInteger(ItemFrequencyCard.NBT_DIM_ID,
					insertedCard.stackTagCompound
							.getInteger(ItemFrequencyCard.NBT_X_POS));
			par1NBTTagCompound.setInteger(ItemFrequencyCard.NBT_X_POS,
					insertedCard.stackTagCompound
							.getInteger(ItemFrequencyCard.NBT_Y_POS));
			par1NBTTagCompound.setInteger(ItemFrequencyCard.NBT_Y_POS,
					insertedCard.stackTagCompound
							.getInteger(ItemFrequencyCard.NBT_Y_POS));
			par1NBTTagCompound.setInteger(ItemFrequencyCard.NBT_Y_POS,
					insertedCard.stackTagCompound
							.getInteger(ItemFrequencyCard.NBT_Y_POS));
		}
	}

}
