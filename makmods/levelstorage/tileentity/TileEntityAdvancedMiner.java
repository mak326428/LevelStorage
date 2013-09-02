package makmods.levelstorage.tileentity;

import ic2.api.item.ElectricItem;
import ic2.api.item.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.item.ItemEnhancedDiamondDrill;
import makmods.levelstorage.logic.DummyPlayer;
import makmods.levelstorage.logic.util.OreDictHelper;
import makmods.levelstorage.logic.util.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.ReflectionHelper;

// TODO: complete. A little tiny bit of effort to make it as awesome as...
public class TileEntityAdvancedMiner extends TileEntityInventorySink implements
        IInventory {

	public static final Map<Integer, ItemStack> slotMapping = Maps.newHashMap();
	public static final ItemStack MINING_PIPE = Items.getItem("miningPipe");
	public static final ItemStack MINING_PIPE_TIP = Items
	        .getItem("miningPipeTip");

	public int progress = 0;

	static {
		slotMapping.put(0, new ItemStack(LSBlockItemList.itemEnhDiamondDrill));
		slotMapping.put(1, MINING_PIPE);
		slotMapping.put(2, new ItemStack(LSBlockItemList.itemAdvScanner));
	}

	public TileEntityAdvancedMiner() {
		super(3);
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(LSBlockItemList.blockAdvMiner);
	}

	public static final String NBT_LEVEL = "yAxis";
	public static final String NBT_TASKS = "tasks";

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("lastMode", this.lastMode.ordinal());
		par1NBTTagCompound.setInteger("progress", this.progress);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);

		this.lastMode = Mode.values()[par1NBTTagCompound.getInteger("lastMode")];
		this.progress = par1NBTTagCompound.getInteger("progress");
	}

	@Override
	public String getInvName() {
		return "Adv Miner";
	}

	@Override
	public int getCapacity() {
		return 16384;
	}

	@Override
	public int getMaxInput() {
		return 512;
	}

	@Override
	public boolean explodes() {
		return true;
	}

	public ItemStack getDrillStack() {
		return getStackInSlot(0);
	}

	public ItemStack getPipesStack() {
		return getStackInSlot(1);
	}

	public ItemStack getScannerStack() {
		return getStackInSlot(2);
	}

	public static enum DrillEnhancement {
		FORTUNE, SILKTOUCH;

		public static DrillEnhancement readDrillEnhancement(
		        ItemStack par1ItemStack) {
			if (par1ItemStack.stackTagCompound == null)
				par1ItemStack.stackTagCompound = new NBTTagCompound();
			NBTTagCompound enh = par1ItemStack.stackTagCompound
			        .getCompoundTag(ItemEnhancedDiamondDrill.ENHANCEMENT_NBT);
			if (!enh.hasKey(ItemEnhancedDiamondDrill.ENHANCEMENT_ID_NBT))
				return null;
			else {
				int id = enh
				        .getInteger(ItemEnhancedDiamondDrill.ENHANCEMENT_ID_NBT);
				int lvl = enh
				        .getInteger(ItemEnhancedDiamondDrill.ENHANCEMENT_LVL_NBT);
				if (id > 0 && lvl > 0) {
					Enchantment ench = Enchantment.enchantmentsList[id];
					if (ench.fortune.effectId == ench.effectId)
						return FORTUNE;
					else
						return SILKTOUCH;
				}
			}
			return null;
		}

		public static int getFortuneLevel(ItemStack par1ItemStack) {
			if (readDrillEnhancement(par1ItemStack) == FORTUNE) {
				NBTTagCompound enh = par1ItemStack.stackTagCompound
				        .getCompoundTag(ItemEnhancedDiamondDrill.ENHANCEMENT_NBT);
				int lvl = enh
				        .getInteger(ItemEnhancedDiamondDrill.ENHANCEMENT_LVL_NBT);
				return lvl;
			}
			return 0;
		}

	}

	/*
	 * public boolean canMine(int x, int y, int z) { int id =
	 * this.worldObj.getBlockId(x, y, z); int meta =
	 * this.worldObj.getBlockMetadata(x, y, z); if (id == 0) { return true; } if
	 * ((id == MINING_PIPE.itemID) || (id == MINING_PIPE_TIP.itemID) || (id ==
	 * Block.chest.blockID)) return false; if (((id ==
	 * Block.waterMoving.blockID) || (id == Block.waterStill.blockID) || (id ==
	 * Block.lavaMoving.blockID) || (id == Block.lavaStill.blockID))) { return
	 * true; } Block block = Block.blocksList[id]; if
	 * (block.getBlockHardness(this.worldObj, x, y, z) < 0.0F) return false; if
	 * ((block.canCollideCheck(meta, false)) &&
	 * (block.blockMaterial.isToolNotRequired())) return true; if (id ==
	 * Block.web.blockID) return true;
	 * 
	 * if (this.getDrillStack() != null) { try { HashMap toolClasses = (HashMap)
	 * ReflectionHelper .getPrivateValue(ForgeHooks.class, null, new String[] {
	 * "toolClasses" }); List tc = (List) toolClasses.get(Integer.valueOf(this
	 * .getDrillStack().itemID)); if (tc == null) return
	 * this.getDrillStack().getItem() .canHarvestBlock(block); Object[] ta =
	 * tc.toArray(); String cls = (String) ta[0]; int hvl = ((Integer)
	 * ta[1]).intValue();
	 * 
	 * HashMap toolHarvestLevels = (HashMap) ReflectionHelper
	 * .getPrivateValue(ForgeHooks.class, null, new String[] {
	 * "toolHarvestLevels" }); Integer bhl = (Integer)
	 * toolHarvestLevels.get(Arrays .asList(new Serializable[] {
	 * Integer.valueOf(block.blockID), Integer.valueOf(meta), cls }));
	 * 
	 * if (bhl == null) return this.getDrillStack().getItem()
	 * .canHarvestBlock(block); if (bhl.intValue() > hvl) return false; return
	 * this.getDrillStack().getItem().canHarvestBlock(block); } catch (Throwable
	 * e) { return false; } }
	 * 
	 * return false; }
	 */

	private boolean harvestBlock(int x, int y, int z, int blockId) {
		if (this.getDrillStack().itemID == LSBlockItemList.itemEnhDiamondDrill.itemID) {
			if (!ElectricItem.manager.use(this.getDrillStack(), 80, null))
				return false;
		} else {
			throw new IllegalStateException("invalid drill: "
			        + this.getDrillStack());
		}

		int energyCost = 2 * (this.yCoord - y);

		if (this.getStored() >= energyCost) {
			this.addEnergy(-energyCost);

			DrillEnhancement enh = DrillEnhancement
			        .readDrillEnhancement(getDrillStack());
			if (enh == null) {
				StackHelper.distributeDrop(this, Block.blocksList[blockId]
				        .getBlockDropped(this.worldObj, x, y, z,
				                this.worldObj.getBlockMetadata(x, y, z), 0));
			} else {
				if (enh == DrillEnhancement.FORTUNE) {
					StackHelper.distributeDrop(this, Block.blocksList[blockId]
					        .getBlockDropped(this.worldObj, x, y, z,
					                this.worldObj.getBlockMetadata(x, y, z),
					                DrillEnhancement
					                        .getFortuneLevel(getDrillStack())));
				} else {
					List<ItemStack> drops = Lists.newArrayList();
					if (Block.blocksList[blockId].canSilkHarvest(this.worldObj,
					        new DummyPlayer(worldObj), x, y, z,
					        this.worldObj.getBlockMetadata(x, y, z))) {
						drops.add(new ItemStack(blockId, 1, this.worldObj
						        .getBlockMetadata(x, y, z)));
					} else {
						drops = Block.blocksList[blockId].getBlockDropped(
						        this.worldObj, x, y, z,
						        this.worldObj.getBlockMetadata(x, y, z), 0);
					}
					StackHelper.distributeDrop(this, drops);
				}
			}

			this.worldObj.setBlockToAir(x, y, z);

			return true;
		}
		return false;
	}

	private Mode lastMode = Mode.None;

	private MineResult mineBlock(int x, int y, int z) {
		int blockId = this.worldObj.getBlockId(x, y, z);
		boolean isAirBlock = true;

		if ((blockId != 0)
		        && (!Block.blocksList[blockId].isAirBlock(this.worldObj, x, y,
		                z))) {
			isAirBlock = false;

			if (!canMine(x, y, z)) {
				return MineResult.Failed_Perm;
			}
		}

		Mode mode;

		int duration;
		if (isAirBlock) {
			mode = Mode.MineAir;
			int energyPerTick = 3;
			duration = 1;
		} else {
			if (this.getDrillStack().itemID == LSBlockItemList.itemEnhDiamondDrill.itemID) {
				mode = Mode.MineDrill;
				int energyPerTick = 20;
				duration = 2;
			} else {

				throw new IllegalStateException("invalid drill: "
				        + this.getDrillStack());

			}
		}

		if (this.lastMode != mode) {
			this.lastMode = mode;
			this.progress = 0;
		}

		int energyPerTick = 256;

		if (this.progress < duration) {
			if (this.getStored() >= energyPerTick) {
				this.addEnergy(-energyPerTick);
				this.progress += 1;
				return MineResult.Working;
			}
		} else if ((isAirBlock) || (harvestBlock(x, y, z, blockId))) {
			this.progress = 0;
			return MineResult.Done;
		}

		return MineResult.Failed_Temp;
	}

	private void chargeTools() {
		if (this.getDrillStack() != null) {
			this.addEnergy(-ElectricItem.manager.charge(this.getDrillStack(),
			        this.getStored(), 2, false, false));
		}

		if (this.getScannerStack() != null) {
			this.addEnergy(-ElectricItem.manager.charge(this.getScannerStack(),
			        this.getStored(), 2, false, false));
		}
	}

	public void onUnloaded() {

	}

	private int scannedLevel = -1;
	private int scanRange = 0;
	private int lastX;
	private int lastZ;

	public void onLoaded() {
		this.scannedLevel = -1;
		this.lastX = this.xCoord;
		this.lastZ = this.zCoord;
	}

	public boolean checkToWork() {
		boolean flag = true;
		if (getDrillStack() == null
		        || getDrillStack().itemID != LSBlockItemList.itemEnhDiamondDrill.itemID)
			flag = false;
		if (getScannerStack() == null
		        || getScannerStack().itemID != LSBlockItemList.itemAdvScanner.itemID)
			flag = false;
		if (getPipesStack() == null
		        || getPipesStack().itemID != MINING_PIPE.itemID)
			flag = false;
		return flag;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		chargeTools();
		if (checkToWork())
			if (work()) {
				onInventoryChanged();
			}
	}

	private int getOperationHeight() {
		for (int y = this.yCoord - 1; y >= 0; y--) {
			int blockId = this.worldObj.getBlockId(this.xCoord, y, this.zCoord);

			if (blockId != MINING_PIPE.itemID) {
				return y;
			}
		}

		return -1;
	}

	private boolean withDrawPipe(int y) {
		if (this.lastMode != Mode.Withdraw) {
			this.lastMode = Mode.Withdraw;
			this.progress = 0;
		}

		if ((y < 0)
		        || (this.worldObj.getBlockId(this.xCoord, y, this.zCoord) != MINING_PIPE_TIP.itemID)) {
			y++;
		}

		if ((y != this.yCoord) && (this.getStored() >= 3)) {
			if (this.progress < 20) {
				this.addEnergy(-3);
				this.progress += 1;
			} else {
				this.progress = 0;

				removePipe(y);
			}

			return true;
		}

		return false;
	}

	private void removePipe(int y) {
		this.worldObj.setBlockToAir(this.xCoord, y, this.zCoord);

		List drops = new ArrayList();
		drops.add(MINING_PIPE.copy());

		StackHelper.distributeDrop(this, drops);

		if ((this.getPipesStack() != null)
		        && (this.getPipesStack().itemID != MINING_PIPE.itemID)) {
			ItemStack filler = this.decrStackSize(1, 1);
			Item fillerItem = filler.getItem();

			if ((fillerItem instanceof ItemBlock))
				((ItemBlock) fillerItem).onItemUse(filler, new DummyPlayer(
				        this.worldObj), this.worldObj, this.xCoord, y + 1,
				        this.zCoord, 0, 0.0F, 0.0F, 0.0F);
		}
	}

	private boolean work() {
		int operationHeight = getOperationHeight();

		if (this.getDrillStack() == null) {
			return withDrawPipe(operationHeight);
		}
		if (operationHeight >= 0) {
			int blockId = this.worldObj.getBlockId(this.xCoord,
			        operationHeight, this.zCoord);

			// if (blockId != MINING_PIPE.itemID) {
			//
			// if (operationHeight > 0) {
			// return digDown(operationHeight, false);
			// }
			// return false;
			// }

			MineResult result = mineLevel(operationHeight);

			if (result == MineResult.Done)
				return digDown(operationHeight - 1, true);
			if (result == MineResult.Working) {
				return true;
			}
			return false;
		}

		return false;
	}

	private boolean digDown(int y, boolean removeTipAbove) {
		if ((this.getPipesStack() == null)
		        || (this.getPipesStack().itemID != MINING_PIPE.itemID))
			return false;

		if (y < 0) {
			if (removeTipAbove)
				this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord,
				        MINING_PIPE.itemID);

			return false;
		}

		MineResult result = mineBlock(this.xCoord, y, this.zCoord);

		if ((result == MineResult.Failed_Temp)
		        || (result == MineResult.Failed_Perm)) {
			if (removeTipAbove)
				this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord,
				        MINING_PIPE.itemID);

			return false;
		}
		if (result == MineResult.Done) {
			if (removeTipAbove)
				this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord,
				        MINING_PIPE.itemID);

			this.decrStackSize(1, 1);
			this.worldObj.setBlock(this.xCoord, y, this.zCoord,
			        MINING_PIPE_TIP.itemID);
		}

		return true;
	}

	private MineResult mineLevel(int y) {
		if (this.getScannerStack() == null)
			return MineResult.Done;

		if (this.scannedLevel != y) {
			this.scanRange = 16;
		}

		if (this.scanRange > 0) {
			this.scannedLevel = y;

			for (int x = this.xCoord - this.scanRange; x <= this.xCoord
			        + this.scanRange; x++) {
				for (int z = this.zCoord - this.scanRange; z <= this.zCoord
				        + this.scanRange; z++) {
					int blockId = this.worldObj.getBlockId(x, y, z);
					int meta = this.worldObj.getBlockMetadata(x, y, z);
					boolean isValidTarget = false;

					if (OreDictHelper.getOreName(
					        new ItemStack(blockId, 1, meta)).startsWith("ore")
					        && (canMine(x, y, z))) {
						isValidTarget = true;
					}

					if (isValidTarget) {
						MineResult result = mineTowards(x, y, z);

						if (result == MineResult.Done)
							return MineResult.Working;
						if (result != MineResult.Failed_Perm) {
							return result;
						}
					}
				}
			}

			return MineResult.Done;
		}
		return MineResult.Failed_Temp;
	}

	private MineResult mineTowards(int x, int y, int z) {
		int cx = this.xCoord;
		for (int cz = this.zCoord; (cx != x) || (cz != z);) {
			boolean isCurrentPos = (cx == this.lastX) && (cz == this.lastZ);

			if (Math.abs(x - cx) >= Math.abs(z - cz))
				cx += (x > cx ? 1 : -1);
			else {
				cz += (z > cz ? 1 : -1);
			}

			boolean isBlocking = false;

			if (isCurrentPos) {
				isBlocking = true;
			} else {
				int blockId = this.worldObj.getBlockId(cx, y, cz);
			}

			if (isBlocking) {
				MineResult result = mineBlock(cx, y, cz);

				if (result == MineResult.Done) {
					this.lastX = cx;
					this.lastZ = cz;
				}

				return result;
			}
		}

		this.lastX = this.xCoord;
		this.lastZ = this.zCoord;

		return MineResult.Done;
	}

	public boolean canMine(int x, int y, int z) {
		int id = this.worldObj.getBlockId(x, y, z);
		int meta = this.worldObj.getBlockMetadata(x, y, z);
		if (id == 0) {
			return true;
		}
		if ((id == MINING_PIPE.itemID) || (id == MINING_PIPE_TIP.itemID)
		        || (id == Block.chest.blockID))
			return false;
		if (((id == Block.waterMoving.blockID)
		        || (id == Block.waterStill.blockID)
		        || (id == Block.lavaMoving.blockID) || (id == Block.lavaStill.blockID))) {
			return true;
		}
		Block block = Block.blocksList[id];
		if (block.getBlockHardness(this.worldObj, x, y, z) < 0.0F)
			return false;
		if ((block.canCollideCheck(meta, false))
		        && (block.blockMaterial.isToolNotRequired()))
			return true;
		if (id == Block.web.blockID)
			return true;

		if (this.getDrillStack() != null) {
			try {
				HashMap toolClasses = (HashMap) ReflectionHelper
				        .getPrivateValue(ForgeHooks.class, null,
				                new String[] { "toolClasses" });
				List tc = (List) toolClasses.get(Integer.valueOf(this
				        .getDrillStack().itemID));
				if (tc == null)
					return this.getDrillStack().canHarvestBlock(block);
				Object[] ta = tc.toArray();
				String cls = (String) ta[0];
				int hvl = ((Integer) ta[1]).intValue();

				HashMap toolHarvestLevels = (HashMap) ReflectionHelper
				        .getPrivateValue(ForgeHooks.class, null,
				                new String[] { "toolHarvestLevels" });
				Integer bhl = (Integer) toolHarvestLevels.get(Arrays
				        .asList(new Serializable[] {
				                Integer.valueOf(block.blockID),
				                Integer.valueOf(meta), cls }));

				if (bhl == null)
					return this.getDrillStack().canHarvestBlock(block);
				if (bhl.intValue() > hvl)
					return false;
				return this.getDrillStack().canHarvestBlock(block);
			} catch (Throwable e) {
				return false;
			}
		}

		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack.itemID == slotMapping.get(i).itemID;
	}

	static enum MineResult {
		Working, Done, Failed_Temp, Failed_Perm;
	}

	static enum Mode {
		None, Withdraw, MineAir, MineDrill,
	}

	@Override
    public boolean acceptsEnergyFrom(TileEntity emitter,
            ForgeDirection direction) {
	    return true;
    }

	@Override
    public int getOutput() {
	    return getMaxSafeInput();
    }
}
