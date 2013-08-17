package makmods.levelstorage.inventory;

import makmods.levelstorage.inventory.InventoryNet.NetSegment;
import net.minecraft.tileentity.TileEntity;

public class InventoryMachine extends TileEntity {
	/**
	 * Type of this machine
	 */
	private InventoryMachineType type;
	/**
	 * Segment this machine belongs to
	 */
	private NetSegment segment;

	/**
	 * Create a new instance of this class
	 * 
	 * @param t
	 *            Type of this machine
	 * @param s
	 *            Segment this machine belongs to
	 */
	public InventoryMachine(InventoryMachineType t, NetSegment s) {
		this.type = t;
		this.segment = s;
		this.segment.machines.put(type, this);
	}

	public InventoryMachineType getType() {
		return type;
	}

	public void setType(InventoryMachineType type) {
		this.type = type;
	}

	public NetSegment getSegment() {
		return segment;
	}

	public void setSegment(NetSegment segment) {
		this.segment = segment;
	}

	/**
	 * Called when tick() method of the NetSegment method is called.
	 * <p>
	 * <b>WARNING:</b> do not do anything complicated with it! It will be called
	 * every tick!
	 * </p>
	 */
	public void onSegmentUpdate() {

	}

}
