package makmods.levelstorage.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import makmods.levelstorage.tileentity.IHasTextBoxes;
import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class PacketRecipeSelection extends PacketLV {

	public int newSlotId;
	public int x;
	public int y;
	public int z;
	public int dimId;

	public PacketRecipeSelection() {
		super(PacketTypeHandler.PACKET_RECIPE_SELECTION, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.newSlotId = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.dimId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.newSlotId);
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
		dos.writeInt(this.dimId);
	}

	@Override
	public void execute(INetworkManager network, Player player) {
		try {
			WorldServer world = DimensionManager.getWorld(this.dimId);

			if (world != null) {
				if (!world.isRemote) {
					TileEntity te = world.getBlockTileEntity(this.x, this.y,
							this.z);
					if (te != null) {
						if (te instanceof TileEntityMassInfuser) {
							TileEntityMassInfuser ihb = (TileEntityMassInfuser) te;
							ihb.handleRecipeSelection(newSlotId);
						}
					}
				}
			}
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE,
					"LevelStorage: PacketRecipeSelection - exception:");
			e.printStackTrace();
		}
	}

}
