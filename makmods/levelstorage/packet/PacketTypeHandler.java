package makmods.levelstorage.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import makmods.levelstorage.lib.Reference;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * @author pahimar
 * 
 */
// I just simply wanted to make it work, so i just simply copypasted everything
// from ee3
// also it's a very neat packet system, license permits it (if it does)
public enum PacketTypeHandler {
	PACKET_PRESS_BUTTON(PacketPressButton.class), PACKET_TEXT_CHANGED(
			PacketTextChanged.class), PACKET_PARTICLES(PacketParticles.class);

	private Class<? extends PacketLV> clazz;

	PacketTypeHandler(Class<? extends PacketLV> clazz) {

		this.clazz = clazz;
	}

	public static PacketLV buildPacket(byte[] data) {

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		int selector = bis.read();
		DataInputStream dis = new DataInputStream(bis);

		PacketLV packet = null;

		try {
			packet = values()[selector].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		packet.readPopulate(dis);

		return packet;
	}

	public static PacketLV buildPacket(PacketTypeHandler type) {

		PacketLV packet = null;

		try {
			packet = values()[type.ordinal()].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return packet;
	}

	public static Packet populatePacket(PacketLV packetLV) {

		byte[] data = packetLV.populate();

		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = Reference.MOD_ID;
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = packetLV.isChunkDataPacket;

		return packet250;
	}
}
