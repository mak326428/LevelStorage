package makmods.levelstorage.iv;

import net.minecraft.item.ItemStack;

public interface IVEntry {
	public int getValue();
	public IVEntry clone();
}
