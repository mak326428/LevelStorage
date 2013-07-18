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
	
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		boolean seenBook = false;
		boolean seenEssence = false;
		// We just loop through all the crafting inventory
		// and see whether our requirements are met
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); ++i) {
			ItemStack currentStack = inventoryCrafting.getStackInSlot(i);

			if (currentStack != null) {
				if (currentStack.getItem() instanceof ItemLevelStorageBook) {
					// To prevent several books in one recipe
					if (seenBook) {
						seenBook = false;
						continue;
					}
					seenBook = true;
				}
			}

			for (XpStack entry : XpStackRegistry.instance.ITEM_XP_CONVERSIONS) {
				ItemStack stack = entry.stack;
				int value = entry.value;
				if (stack != null && currentStack != null) {
					if (stack.itemID == currentStack.itemID) {
						seenEssence = true;
					}
				}
			}
		}
		boolean actuallyMatches = seenEssence && seenBook;
		return actuallyMatches;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		int bookXp = 0;
		ItemStack initialBookStack = null;
		boolean seenBook = false;
		ArrayList<Boolean> valids = new ArrayList<Boolean>();
		int buffXp = 0;
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); ++i) {
			boolean cycleCompleted = false;

			ItemStack currentStack = inventoryCrafting.getStackInSlot(i);

			if (currentStack != null) {
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

			if (currentStack == null)
				cycleCompleted = true;

			valids.add(cycleCompleted);

		}

		for (boolean v : valids) {
			if (!v)
				return null;
		}

		if ((bookXp + buffXp) >= LevelStorage.itemLevelStorageBookSpace)
			return null;

		int totalXp = bookXp + buffXp;
		ItemStack result = new ItemStack(ModItems.itemLevelStorageBook);
		result.stackTagCompound = new NBTTagCompound();
		result.stackTagCompound.setInteger(ItemLevelStorageBook.STORED_XP_NBT,
				totalXp);

		return result;
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
