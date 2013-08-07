package makmods.levelstorage.proxy;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TaskBreakBlock extends Task {

	public int dim;
	public int x;
	public int y;
	public int z;

	public TaskBreakBlock(int dim, int x, int y, int z) {
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void doJob() {
		World w = DimensionManager.getWorld(this.dim);
		if (!w.isAirBlock(this.x, this.y, this.z)) {
			Block b = Block.blocksList[w.getBlockId(this.x, this.y, this.z)];
			if (b.blockHardness != -1.0F) {
				w.setBlockTileEntity(this.x, this.y, this.z, null);
				w.setBlockToAir(this.x, this.y, this.z);
			}
		}
	}

}
