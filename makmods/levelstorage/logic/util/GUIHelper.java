package makmods.levelstorage.logic.util;

import java.util.Iterator;
import java.util.ListIterator;

import makmods.levelstorage.LevelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUIHelper {
	// I should've done it via AccessTransformers, but i just don't feel like
	// it.
	protected static boolean mergeItemStack(Container c,
	        ItemStack par1ItemStack, int par2, int par3, boolean par4) {
		boolean flag1 = false;
		int k = par2;

		if (par4) {
			k = par3 - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (par1ItemStack.isStackable()) {
			while (par1ItemStack.stackSize > 0
			        && (!par4 && k < par3 || par4 && k >= par2)) {
				slot = (Slot) c.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 != null
				        && itemstack1.itemID == par1ItemStack.itemID
				        && (!par1ItemStack.getHasSubtypes() || par1ItemStack
				                .getItemDamage() == itemstack1.getItemDamage())
				        && ItemStack.areItemStackTagsEqual(par1ItemStack,
				                itemstack1)) {
					int l = itemstack1.stackSize + par1ItemStack.stackSize;

					if (l <= par1ItemStack.getMaxStackSize()) {
						par1ItemStack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					} else if (itemstack1.stackSize < par1ItemStack
					        .getMaxStackSize()) {
						par1ItemStack.stackSize -= par1ItemStack
						        .getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = par1ItemStack.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (par4) {
					--k;
				} else {
					++k;
				}
			}
		}

		if (par1ItemStack.stackSize > 0) {
			if (par4) {
				k = par3 - 1;
			} else {
				k = par2;
			}

			while (!par4 && k < par3 || par4 && k >= par2) {
				slot = (Slot) c.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 == null) {
					slot.putStack(par1ItemStack.copy());
					slot.onSlotChanged();
					par1ItemStack.stackSize = 0;
					flag1 = true;
					break;
				}

				if (par4) {
					--k;
				} else {
					++k;
				}
			}
		}

		return flag1;
	}

	public static ItemStack shiftClickSlot(Container c, EntityPlayer player,
	        int sourceSlotIndex) {
		Slot sourceSlot = (Slot) c.inventorySlots.get(sourceSlotIndex);
		if (sourceSlot != null && sourceSlot.getHasStack()) {
			ItemStack sourceItemStack = sourceSlot.getStack();
			int oldSourceItemStackSize = sourceItemStack.stackSize;
			int run;
			Slot targetSlot;
			if (sourceSlot.inventory == player.inventory) {
				for (run = 0; run < 4 && sourceItemStack.stackSize > 0; ++run) {
					Iterator it;
					if (run < 2) {
						it = c.inventorySlots.iterator();

						while (it.hasNext()) {
							targetSlot = (Slot) it.next();
							if (targetSlot.isItemValid(sourceItemStack)
							        && (targetSlot.getStack() != null || run == 1)) {
								mergeItemStack(c, sourceItemStack,
								        targetSlot.slotNumber,
								        targetSlot.slotNumber + 1, false);
								if (sourceItemStack.stackSize == 0) {
									break;
								}
							}
						}
					} else {
						it = c.inventorySlots.iterator();

						while (it.hasNext()) {
							targetSlot = (Slot) it.next();
							if (targetSlot.inventory != player.inventory
							        && targetSlot.isItemValid(sourceItemStack)
							        && (targetSlot.getStack() != null || run == 3)) {
								mergeItemStack(c, sourceItemStack,
								        targetSlot.slotNumber,
								        targetSlot.slotNumber + 1, false);
								if (sourceItemStack.stackSize == 0) {
									break;
								}
							}
						}
					}
				}
			} else {
				for (run = 0; run < 2 && sourceItemStack.stackSize > 0; ++run) {
					ListIterator var9 = c.inventorySlots
					        .listIterator(c.inventorySlots.size());

					while (var9.hasPrevious()) {
						targetSlot = (Slot) var9.previous();
						if (targetSlot.inventory == player.inventory
						        && targetSlot.isItemValid(sourceItemStack)
						        && (targetSlot.getStack() != null || run == 1)) {
							mergeItemStack(c, sourceItemStack,
							        targetSlot.slotNumber,
							        targetSlot.slotNumber + 1, false);
							if (sourceItemStack.stackSize == 0) {
								break;
							}
						}
					}
				}
			}

			if (sourceItemStack.stackSize != oldSourceItemStackSize) {
				if (sourceItemStack.stackSize == 0) {
					sourceSlot.putStack((ItemStack) null);
				} else {
					sourceSlot.onPickupFromSlot(player, sourceItemStack);
				}

				if (LevelStorage.isSimulating()) {
					c.detectAndSendChanges();
				}
			}
		}

		return null;
	}
}
