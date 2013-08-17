package makmods.levelstorage.inventory;

import makmods.levelstorage.logic.BlockLocation;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * Used for inventoryhandling
 * 
 * @author mak326428
 * 
 */
public class InventoryTile {
	private IInventory inventory;
	private BlockLocation worldLocation;
	private World world;

	public InventoryTile(IInventory inventory, BlockLocation location, World w) {
		this.inventory = inventory;
		this.worldLocation = location;
		this.world = w;
	}

	public IInventory getInventory() {
		return inventory;
	}

	public void setInventory(IInventory inventory) {
		this.inventory = inventory;
	}

	public BlockLocation getWorldLocation() {
		return worldLocation;
	}

	public void setWorldLocation(BlockLocation worldLocation) {
		this.worldLocation = worldLocation;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

}
