package makmods.levelstorage.logic.util;

import net.minecraft.nbt.NBTTagCompound;

public enum SimpleMode {
	ON(true), OFF(false);

	public boolean boolValue;
	public static String NBT_NAME = "mode";
	public static String NBT_COMPOUND_NAME = "mode_c";
	

	private SimpleMode(boolean value) {
		this.boolValue = value;
	}

	public SimpleMode getReverse(SimpleMode forWhat) {
		return forWhat == ON ? OFF : ON;
	}
	
	public SimpleMode getReverse() {
		return getReverse(this);
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger(NBT_NAME, this.ordinal());
			nbt.setCompoundTag(NBT_COMPOUND_NAME, compound);
		}
	}
	
	public static SimpleMode readFromNBT(NBTTagCompound nbtTag) {
		if (nbtTag != null) {
			NBTTagCompound compound = nbtTag.getCompoundTag(NBT_COMPOUND_NAME);
			if (compound != null) {
				return SimpleMode.values()[compound.getInteger(NBT_NAME)];
			}
		}
		return OFF;
	}
}
