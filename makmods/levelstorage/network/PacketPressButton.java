package makmods.levelstorage.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import makmods.levelstorage.tileentity.IHasButtons;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class PacketPressButton extends PacketLV {

	public int buttonId;
	public int x;
	public int y;
	public int z;
	public int dimId;

	public PacketPressButton() {
		super(PacketTypeHandler.PACKET_PRESS_BUTTON, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.buttonId = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.dimId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.buttonId);
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
				TileEntity te = world
				        .getBlockTileEntity(this.x, this.y, this.z);
				if (te != null) {
					if (te instanceof IHasButtons) {
						IHasButtons ihb = (IHasButtons) te;
						ihb.handleButtonClick(this.buttonId);
					}
				}
			}
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE,
			        "LevelStorage: PacketPressButton - exception:");
			e.printStackTrace();
		}
	}

}
