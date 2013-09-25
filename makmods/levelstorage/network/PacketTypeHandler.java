package makmods.levelstorage.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import makmods.levelstorage.lib.Reference;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * @author pahimar
 * 
 */
public enum PacketTypeHandler {
	PACKET_PRESS_BUTTON(PacketPressButton.class), PACKET_TEXT_CHANGED(
			PacketTextChanged.class), PACKET_PARTICLE(PacketParticle.class), PACKET_FLIGHT_UPDATE(
			PacketFlightUpdate.class), PACKET_TESLA_RAY(PacketTeslaRay.class), PACKET_PARTICLES(
			PacketParticles.class), PACKET_TILE_UPDATE(PacketTileUpdate.class);

	private Class<? extends PacketLS> clazz;

	PacketTypeHandler(Class<? extends PacketLS> clazz) {

		this.clazz = clazz;
	}

	public static PacketLS buildPacket(byte[] data) {

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		int selector = bis.read();
		DataInputStream dis = new DataInputStream(bis);

		PacketLS packet = null;

		try {
			packet = values()[selector].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		packet.readPopulate(dis);

		return packet;
	}

	public static PacketLS buildPacket(PacketTypeHandler type) {

		PacketLS packet = null;

		try {
			packet = values()[type.ordinal()].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return packet;
	}

	public static Packet populatePacket(PacketLS packetLV) {

		byte[] data = packetLV.populate();

		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = Reference.MOD_ID;
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = packetLV.isChunkDataPacket;

		return packet250;
	}
}
