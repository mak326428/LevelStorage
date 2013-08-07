package makmods.levelstorage.logic;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class OreFinder {
		public ArrayList<BlockLocation> foundOre = new ArrayList<BlockLocation>();

		public int initialX;
		public int initialY;
		public int initialZ;

		public int aimBlockId;
		public int aimBlockMeta;

		// Unused, but what the heck, let it be here.
		public World world;

		public void calibrate(int x, int y, int z) {
			initialX = x;
			initialY = y;
			initialZ = z;
			BlockLocation initialBlock = new BlockLocation(
					world.provider.dimensionId, x, y, z);
			// foundOre.add(initialBlock);
			findContinuation(initialBlock);
		}

		public void findContinuation(BlockLocation loc) {
			boolean found = false;
			if (loc != null) {
				for (BlockLocation locat : foundOre) {
					if (locat.equals(loc))
						found = true;
				}
				if (!found)
					foundOre.add(loc);
			}
			if (!found) {
				for (int dir = 0; dir < 6; dir++) {
					BlockLocation newTh = loc.move(
							ForgeDirection.values()[dir], 1);
					int currX = newTh.getX();
					int currY = newTh.getY();
					int currZ = newTh.getZ();

					Block currBlock = Block.blocksList[this.world.getBlockId(
							currX, currY, currZ)];
					if (currBlock != null) {
						if (currBlock.blockID == aimBlockId) {
							int currMeta = this.world.getBlockMetadata(currX,
									currY, currZ);
							if (currMeta == aimBlockMeta) {
								// Recursion, very dangerous, but i hope nobody
								// will
								// use
								// this on stone...
								findContinuation(new BlockLocation(
										this.world.provider.dimensionId, currX,
										currY, currZ));
							}
						}
					}
				}
			}
		}
	}