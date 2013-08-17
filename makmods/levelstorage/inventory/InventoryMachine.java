package makmods.levelstorage.inventory;

import makmods.levelstorage.inventory.InventoryNet.NetSegment;
import net.minecraft.tileentity.TileEntity;

public class InventoryMachine extends TileEntity {
	private InventoryMachineType type;
	private NetSegment segment;

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

}
