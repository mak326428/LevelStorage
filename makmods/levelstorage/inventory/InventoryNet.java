package makmods.levelstorage.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import makmods.levelstorage.logic.BlockLocation;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class InventoryNet {
	public static final InventoryNet instance = new InventoryNet();

	public List<NetSegment> segments = new ArrayList<NetSegment>();

	public void tick() {
		for (NetSegment segment : this.segments) {
			segment.tick();
			if (segment.machines.size() == 0)
				segments.remove(segment);
		}
	}

	public NetSegment getBlockSegment(BlockLocation block) {
		World w = DimensionManager.getWorld(block.getDimId());
		if (!w.isAirBlock(block.getX(), block.getY(), block.getZ())) {
			if (w.blockHasTileEntity(block.getX(), block.getY(), block.getZ())) {
				TileEntity tile = w.getBlockTileEntity(block.getX(),
				        block.getY(), block.getZ());
				if (tile != null) {
					if (tile instanceof InventoryMachine) {
						return ((InventoryMachine) tile).getSegment();
					}
				}
			}
		}
		return null;
	}

	public class NetSegment {

		public HashMap<InventoryMachineType, InventoryMachine> machines = new HashMap<InventoryMachineType, InventoryMachine>();

		public void tick() {
			for (Entry<InventoryMachineType, InventoryMachine> machine : machines.entrySet()) {
				InventoryMachine m = machine.getValue();
				if (m != null)
					m.onSegmentUpdate();
			}
		}

		public InventoryMachine[] getMachinesByType(InventoryMachineType type) {
			if (type != null) {
				ArrayList<InventoryMachine> machines = new ArrayList<InventoryMachine>();
				for (Entry<InventoryMachineType, InventoryMachine> mach : this.machines
				        .entrySet()) {
					if (mach != null) {
						if (mach.getKey() != null && mach.getValue() != null) {
							if (mach.getKey().equals(type)) {
								machines.add(mach.getValue());
							}
						}
					}
				}
				return (InventoryMachine[]) machines
				        .toArray(new InventoryMachine[machines.size()]);
			}
			return null;
		}
	}
}
