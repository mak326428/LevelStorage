package makmods.levelstorage.logic.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

/**
 * A pending replace for OreFinder.
 * 
 * @author mak326428
 * 
 */
public class AdvBlockFinder {
	private ArrayList<BlockLocation> blocksFound = Lists.newArrayList();

	private int initialX;
	private int initialY;
	private int initialZ;

	private String targetName;
	private World world;

	/**
	 * Initializes a new instance of this class. <br />
	 * <b>WARNING: DO NOT USE THIS ON STONE OR <br />
	 * ANY OTHER MATERIAL LIKE STONE (IT USES RECURSION, IF YOU USE THIS ON <br />
	 * MATERIAL THAT THERE ARE MILLIONS OF IN THE WORLD, YOU MIGHT HAVE A LOT OF
	 * TROBLES <br />
	 * IF YOU USE THIS ON THAT MATERIAL (TESTED ON STONE), GAME WILL CRASH WITH
	 * STACK OVERFLOW!</b>
	 * 
	 * @param w
	 *            World to find blocks in
	 * @param x
	 *            X coordinate of the initial block
	 * @param y
	 *            Y coordinate of the initial block
	 * @param z
	 *            Z coordinate of the initial block
	 * @param targetName
	 *            OreDict-name of the target block. For instance, "oreTin",
	 *            "logWood"
	 */
	public AdvBlockFinder(World w, int x, int y, int z, String targetName) {
		this.initialX = x;
		this.initialY = y;
		this.initialZ = z;
		this.world = w;
		this.targetName = targetName;
		BlockLocation initialBlock = new BlockLocation(x, y, z);
		findContinuation(initialBlock);
	}

	/**
	 * Quite self-descriptive.
	 * 
	 * @return
	 */
	public List<BlockLocation> getBlocksFound() {
		return this.blocksFound;
	}

	private boolean isBlockOreDict(Block bl) {
		return bl != null && targetName != null ? OreDictionary.getOreName(
				OreDictionary.getOreID(new ItemStack(bl))).equals(targetName)
				: false;
	}

	private void findContinuation(BlockLocation loc) {
		boolean found = false;
		if (loc != null) {
			for (BlockLocation locat : blocksFound) {
				if (locat.equals(loc))
					found = true;
			}
			if (!found)
				blocksFound.add(loc);
		}
		if (!found) {
			for (int dir = 0; dir < 6; dir++) {
				BlockLocation newTh = loc.move(ForgeDirection.values()[dir], 1);
				int currX = newTh.getX();
				int currY = newTh.getY();
				int currZ = newTh.getZ();

				Block currBlock = Block.blocksList[this.world.getBlockId(currX,
						currY, currZ)];
				if (currBlock != null) {
					if (isBlockOreDict(currBlock)) {
						// Recursion, very dangerous, but i hope nobody
						// will
						// use
						// this on stone...
						findContinuation(new BlockLocation(
								this.world.provider.dimensionId, currX, currY,
								currZ));
					}
				}
			}
		}
	}
}