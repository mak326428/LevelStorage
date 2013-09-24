package makmods.levelstorage.gui.logicslot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface LogicSlot {
	public boolean isValid(ItemStack stack);
	public boolean canConsume(int amount);
	public boolean canAdd(int amount);
	public IInventory getInventory();
	public void add(int amount);
	public void consume(int amount);
	public ItemStack get();
}
