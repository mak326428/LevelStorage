package makmods.levelstorage.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.proxy.LSKeyboard;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals(LSKeyboard.PACKET_KEYBOARD_CHANNEL)) {
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
			DataInputStream dis = new DataInputStream(bis);
			try {
				String keyName = dis.readUTF();
				boolean active = dis.readBoolean();
				LevelStorage.keyboard.handleKeyChangeServer(
						(EntityPlayerMP) player, keyName, active);
				// LevelStorage.keyboard.printKeys();
			} catch (Exception e) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				playerMP.playerNetServerHandler.kickPlayerFromServer("Hacker!");
			}
			return;
		} else if (packet.channel.equals(Reference.CUSTOM_PACKET_CHANNEL)) {
			
		} else {
			PacketLS packetEE = PacketTypeHandler.buildPacket(packet.data);
			packetEE.execute(manager, player);
		}
	}

}
