package makmods.levelstorage.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.network.PacketLS;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketReRender extends PacketLS {
	
	public int x, y, z;

	public PacketReRender() {
		super(PacketTypeHandler.PACKET_RERENDER, false);
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		x = data.readInt();
		y = data.readInt();
		z = data.readInt();
	}
	
	public static void reRenderBlock(int x, int y, int z) {
		PacketReRender prr = new PacketReRender();
		prr.x = x;
		prr.y = y;
		prr.z = z;
		PacketDispatcher.sendPacketToAllPlayers(PacketTypeHandler.populatePacket(prr));
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(INetworkManager network, Player player) {
		Minecraft.getMinecraft().theWorld.markBlockForRenderUpdate(x, y, z);
	}

}
