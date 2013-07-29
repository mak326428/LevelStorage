package makmods.levelstorage.logic;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class BlockLocation {
	private int dimId;
	private int x;
	private int y;
	private int z;

	/**
	 * Initializes a new instance of BlockLocation
	 * 
	 * @param dimId
	 *            Dimension ID
	 * @param x
	 *            X Coordinate
	 * @param y
	 *            Y Coordinate
	 * @param z
	 *            Z Coordinate
	 */
	public BlockLocation(int dimId, int x, int y, int z) {
		this.dimId = dimId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Initializes empty instance of BlockLocation (all values are 0s)
	 */
	public BlockLocation() {
		this.dimId = 0;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * Self-descriptive
	 * 
	 * @param other
	 *            BlockLocation for comparison
	 * @return Amount of space between two points, or Integer.MAX_VALUE if
	 *         another dimension
	 */
	public int getDistance(BlockLocation other) {
		if (this.dimId != other.dimId)
			return Integer.MAX_VALUE;
		int xDistance = Math.abs(this.x - other.x);
		int yDistance = Math.abs(this.y - other.y);
		int zDistance = Math.abs(this.z - other.z);

		return xDistance + yDistance + zDistance;
	}
	
	/**
	 * Gets energy discount for given energy and distance
	 * @param energy Energy
	 * @param distance Distance
	 * @return energy discount
	 */
	public static int getEnergyDiscount(int energy, int distance) {
		// Cross-Dimensional
		if (distance == Integer.MAX_VALUE)
			return (int)(energy * 0.25f);
		// Distance < 1000
		if (distance < 1000)
			return (int)(energy * 0.05f);
		// Distance > 1000 <2000
		if (distance > 1000 && distance < 2000)
			return (int)(energy * 0.1f);
		// Distance > 2000
		if (distance > 2000)
			return (int)(energy * 0.15f);
		// IDK what happened here
		return 0;
	}
	
	/**
	 * Returns whether or not DimensionId is valid
	 * @param dimId Dimension id
	 */
	public static boolean isDimIdValid(int dimId) {
		Integer[] ids = DimensionManager.getIDs();
		for (int id : ids) {
			if (id == dimId)
				return true;
		}
		return false;
	}

	/**
	 * Gets TileEntity
	 * 
	 * @return TileEntity of block on given coordinates
	 */
	public TileEntity getTileEntity() {
		if (!isDimIdValid(this.dimId))
			return null;
		return DimensionManager.getWorld(this.dimId).getBlockTileEntity(this.x,
				this.y, this.z);
	}

	// Getters & setters ahead
	public int getDimId() {
		return dimId;
	}

	public void setDimId(int dimId) {
		this.dimId = dimId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	// End of getters and setters
}
