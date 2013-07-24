package makmods.levelstorage;

import java.util.ArrayList;

import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.item.ItemLevelStorageBook;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ExperienceRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		return this.getResInternal(inventoryCrafting) != null;
	}
	
	public ItemStack getResInternal(InventoryCrafting inventoryCrafting) {
		
		int bookXp = 0;
		ItemStack initialBookStack = null;
		boolean seenBook = false;
		ArrayList<Boolean> valids = new ArrayList<Boolean>();
		int buffXp = 0;
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); ++i) {
			boolean cycleCompleted = false;
			
			ItemStack currentStack = inventoryCrafting.getStackInSlot(i);
			if (currentStack != null && !currentStack.equals(null)) {
				if (currentStack.getItem() instanceof ItemLevelStorageBook) {
					if (currentStack.stackTagCompound != null) {
						if (currentStack.stackTagCompound
								.hasKey(ItemLevelStorageBook.STORED_XP_NBT)) {
							if (seenBook)
								return null;
							initialBookStack = currentStack;
							bookXp = currentStack.stackTagCompound
									.getInteger(ItemLevelStorageBook.STORED_XP_NBT);
							seenBook = true;
							cycleCompleted = true;
						}
					}
				}
			}

			for (XpStack entry : XpStackRegistry.instance.ITEM_XP_CONVERSIONS) {
				ItemStack stack = entry.stack;
				int value = entry.value;
				if (stack != null && currentStack != null) {
					if (stack.getItem() == currentStack.getItem()) {
						buffXp += value;
						cycleCompleted = true;
					}
				}
			}

			if (currentStack == null) {
				cycleCompleted = true;
			}

			valids.add(cycleCompleted);

		}

		for (boolean v : valids) {
			if (!v)
				return null;
		}
		
		if ((bookXp + buffXp) >= LevelStorage.itemLevelStorageBookSpace)
			return null;

		int totalXp = bookXp + buffXp;
		
		if (totalXp == 0)
			return null;
		ItemStack result = new ItemStack(ModItems.itemLevelStorageBook);
		result.stackTagCompound = new NBTTagCompound();
		result.stackTagCompound.setInteger(ItemLevelStorageBook.STORED_XP_NBT,
				totalXp);

		return result;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		return this.getResInternal(inventoryCrafting);
	}

	@Override
	public int getRecipeSize() {

		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {

		return null;
	}
}
