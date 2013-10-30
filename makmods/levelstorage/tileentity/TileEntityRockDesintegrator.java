package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.gui.client.GUIRockDesintegrator;
import makmods.levelstorage.gui.container.ContainerRockDesintegrator;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.gui.logicslot.LogicSlot;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import makmods.levelstorage.tileentity.template.TileEntityInventorySink;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRockDesintegrator extends TileEntityInventorySink
		implements ISidedInventory, ITEHasGUI {

	private LogicSlot[] slots;

	public static int EU_PER_BLOCK = 64;

	public TileEntityRockDesintegrator() {
		super(16);
		slots = new LogicSlot[getSizeInventory()];
		for (int i = 0; i < this.getSizeInventory(); i++)
			slots[i] = new LogicSlot(this, i);
	}

	public boolean insertIfPossible(ItemStack st1) {
		boolean inserted = false;
		for (int i = 0; i < slots.length; i++) {
			LogicSlot slot = slots[i];
			if (slot.add(st1.copy(), true)) {
				slot.add(st1.copy(), false);
				inserted = true;
				break;
			}
		}
		return inserted;
	}

	@Override
	public String getInvName() {
		return "Rock Desintegrator";
	}

	public void updateEntity() {
		super.updateEntity();
		int antiDeadlock = 0;
		while (canUse(EU_PER_BLOCK)) {
			antiDeadlock++;
			// since I am using while statement, some deadlocks may happen and
			// freeze up the world
			if (antiDeadlock >= 100) {
				LogHelper
						.severe("[Rock Desintegrator] Deadlock happened! Awful! Report to LS immediately!");
				antiDeadlock = 0;
				break;
			}
			boolean inserted = insertIfPossible(new ItemStack(Block.cobblestone));
			if (!inserted)
				break;
			else
				use(EU_PER_BLOCK);
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockRockDesintegrator);
	}

	@Override
	public int getCapacity() {
		return 8192;
	}

	@Override
	public void onUnloaded() {
	}

	@Override
	public int getMaxInput() {
		return 512;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	@Override
	public void onLoaded() {
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGUI(EntityPlayer player, World world, int x, int y,
			int z) {
		return new GUIRockDesintegrator(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, World world, int x,
			int y, int z) {
		return new ContainerRockDesintegrator(player.inventory, this);
	}

}
