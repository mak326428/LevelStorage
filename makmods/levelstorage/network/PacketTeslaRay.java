package makmods.levelstorage.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.client.render.EnergyRayFX;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketTeslaRay extends PacketLS {

	public double initX;
	public double initY;
	public double initZ;
	public double tX;
	public double tY;
	public double tZ;

	public PacketTeslaRay() {
		super(PacketTypeHandler.PACKET_TESLA_RAY, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.initX = data.readDouble();
		this.initY = data.readDouble();
		this.initZ = data.readDouble();
		this.tX = data.readDouble();
		this.tY = data.readDouble();
		this.tZ = data.readDouble();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeDouble(initX);
		dos.writeDouble(initY);
		dos.writeDouble(initZ);
		dos.writeDouble(tX);
		dos.writeDouble(tY);
		dos.writeDouble(tZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void execute(INetworkManager network, Player player) {
		if (LevelStorage.getSide().isClient()) {
			EntityPlayer p = (EntityPlayer) player;
			EnergyRayFX pe = new EnergyRayFX(p.worldObj, initX, initY, initZ,
			        tX, tY, tZ, 48, 141, 255, 40);
			Minecraft.getMinecraft().effectRenderer.addEffect(pe);
		}
	}

}
