package makmods.levelstorage.logic;

import makmods.levelstorage.LevelStorage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;

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

	@Override
	public boolean equals(Object other) {
		boolean eq = true;
		eq = eq && other instanceof BlockLocation;
		if (eq) {
			eq = eq && ((BlockLocation) other).dimId == this.dimId;
			eq = eq && ((BlockLocation) other).x == this.x;
			eq = eq && ((BlockLocation) other).y == this.y;
			eq = eq && ((BlockLocation) other).z == this.z;
		}
		return eq;
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

	public String getBlockName() {
		try {
			World w = DimensionManager.getWorld(this.dimId);
			return new ItemStack(Block.blocksList[w.getBlockId(x, y, z)])
					.getDisplayName();
		} catch (Exception e) {
			return "Unknown.";
		}
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

	public BlockLocation copy() {
		return new BlockLocation(this.dimId, this.x, this.y, this.z);
	}

	public BlockLocation move(ForgeDirection dir, int space) {
		BlockLocation ret = this.copy();
		ret.x += dir.offsetX * space;
		ret.y += dir.offsetY * space;
		ret.z += dir.offsetZ * space;
		return ret;
	}

	private static final String BLOCK_LOCATION_NBT = "blockLocation";
	private static final String DIM_ID_NBT = "dimId";
	private static final String X_NBT = "xCoord";
	private static final String Y_NBT = "yCoord";
	private static final String Z_NBT = "zCoord";

	/**
	 * Writes an instance of {@link BlockLocation} on a given NBT tag
	 * 
	 * @param nbt
	 *            NBT to write to
	 * @param location
	 *            Self-descriptive
	 */
	public static void writeToNBT(NBTTagCompound nbt, BlockLocation location) {
		NBTTagCompound blockLocationC = new NBTTagCompound();
		blockLocationC.setInteger(DIM_ID_NBT, location.dimId);
		blockLocationC.setInteger(X_NBT, location.x);
		blockLocationC.setInteger(Y_NBT, location.y);
		blockLocationC.setInteger(Z_NBT, location.z);
		nbt.setCompoundTag(BLOCK_LOCATION_NBT, blockLocationC);
	}

	/**
	 * Reads an instance of {@link BlockLocation} from a given NBT tag
	 * 
	 * @param nbt
	 *            NBT tag compound to read from
	 * @return An instance of {@link BlockLocation} containing read data
	 */
	public static BlockLocation readFromNBT(NBTTagCompound nbt) {
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagCompound blockLocationC = nbt.getCompoundTag(BLOCK_LOCATION_NBT);
		BlockLocation loc = new BlockLocation();
		if ((blockLocationC.getInteger(X_NBT) == 0)
				&& (blockLocationC.getInteger(Y_NBT) == 0)
				&& (blockLocationC.getInteger(Z_NBT) == 0))
			return null;
		loc.setDimId(blockLocationC.getInteger(DIM_ID_NBT));
		loc.setX(blockLocationC.getInteger(X_NBT));
		loc.setY(blockLocationC.getInteger(Y_NBT));
		loc.setZ(blockLocationC.getInteger(Z_NBT));
		return loc;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BlockLocation: ");
		sb.append("x: " + this.x);
		sb.append(' ');
		sb.append("y: " + this.y);
		sb.append(' ');
		sb.append("z: " + this.z);
		sb.append(' ');
		sb.append("dimId: " + this.dimId);
		sb.append(';');
		return sb.toString();
	}

	/**
	 * Gets energy discount for given energy and distance
	 * 
	 * @param energy
	 *            Energy
	 * @param distance
	 *            Distance
	 * @return energy discount
	 */
	public static int getEnergyDiscount(int energy, int distance) {
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"enableEnergyLoss", false).getBoolean(false)) {
			// Cross-Dimensional
			if (distance == Integer.MAX_VALUE)
				return (int) (energy * 0.25f);
			// Distance < 1000
			if (distance < 1000)
				return (int) (energy * 0.05f);
			// Distance > 1000 <2000
			if (distance > 1000 && distance < 2000)
				return (int) (energy * 0.1f);
			// Distance > 2000
			if (distance > 2000)
				return (int) (energy * 0.15f);
		}
		return 0;
	}

	/**
	 * Returns whether or not DimensionId is valid
	 * 
	 * @param dimId
	 *            Dimension id
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
		return this.dimId;
	}

	public void setDimId(int dimId) {
		this.dimId = dimId;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	// End of getters and setters
}
