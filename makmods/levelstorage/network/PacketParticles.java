package makmods.levelstorage.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public class PacketParticles extends PacketLV {

	public double x;
	public double y;
	public double z;
	public double velX;
	public double velY;
	public double velZ;
	public String name;

	public PacketParticles() {
		super(PacketTypeHandler.PACKET_PARTICLES, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.velX = data.readDouble();
		this.velY = data.readDouble();
		this.velZ = data.readDouble();
		this.x = data.readDouble();
		this.y = data.readDouble();
		this.z = data.readDouble();
		this.name = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeDouble(this.velX);
		dos.writeDouble(this.velY);
		dos.writeDouble(this.velZ);
		dos.writeDouble(this.x);
		dos.writeDouble(this.y);
		dos.writeDouble(this.z);
		dos.writeUTF(this.name);
	}

	@Override
	public void execute(INetworkManager network, Player player) {
		EntityPlayer p = (EntityPlayer) player;
		p.worldObj.spawnParticle(this.name, this.x, this.y, this.z, this.velX,
				this.velY, this.velZ);
	}

}
