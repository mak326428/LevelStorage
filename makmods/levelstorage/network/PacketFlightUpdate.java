package makmods.levelstorage.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public class PacketFlightUpdate extends PacketLS {

	public boolean allowFlying;
	public boolean isFlying;

	public PacketFlightUpdate() {
		super(PacketTypeHandler.PACKET_FLIGHT_UPDATE, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.allowFlying = data.readBoolean();
		this.isFlying = data.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeBoolean(allowFlying);
		dos.writeBoolean(isFlying);
	}

	@Override
	public void execute(INetworkManager network, Player player) {
		EntityPlayer p = (EntityPlayer) player;
		p.capabilities.allowFlying = allowFlying;
		p.capabilities.isFlying = isFlying;
	}

}
