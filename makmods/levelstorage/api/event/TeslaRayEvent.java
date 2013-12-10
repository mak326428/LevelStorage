package makmods.levelstorage.api.event;

import makmods.levelstorage.network.packet.PacketTeslaRay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
/**
 * Invoked when a player shoots a tesla ray
 * @author mak326428
 *
 */
public class TeslaRayEvent extends PlayerEvent {
	
	private EntityPlayer player;
	private double initX;
	private double initY;
	private double initZ;
	private double tX;
	private double tY;
	private double tZ;

	public TeslaRayEvent(World world, EntityPlayer player, double initX,
			double initY, double initZ, double tX, double tY, double tZ) {
		super(player);
		this.player = player;
		this.initX = initX;
		this.initY = initY;
		this.initZ = initZ;
		this.tX = tX;
		this.tY = tY;
		this.tZ = tZ;
	}

	public double getTargetX() {
		return tX;
	}

	public double getTargetY() {
		return tY;
	}

	public double getTargetZ() {
		return tZ;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public double getInitX() {
		return initX;
	}

	public void setInitX(double initX) {
		this.initX = initX;
	}

	public double getInitY() {
		return initY;
	}

	public void setInitY(double initY) {
		this.initY = initY;
	}

	public double getInitZ() {
		return initZ;
	}

	public void setInitZ(double initZ) {
		this.initZ = initZ;
	}

	public void setTargetX(double x) {
		this.tX = x;
	}

	public void setTargetY(double y) {
		this.tY = y;
	}

	public void setTargetZ(double z) {
		this.tZ = z;
	}

}
