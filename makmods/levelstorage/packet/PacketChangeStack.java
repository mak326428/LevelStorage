package makmods.levelstorage.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class PacketChangeStack extends PacketLV {

	public int dimId;
	public String playerUsername;
	public int slotId;
	public ItemStack stack;

	public PacketChangeStack() {
		super(PacketTypeHandler.PACKET_CHANGE_STACK, false);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.stack = ItemStack.loadItemStackFromNBT(Packet
				.readNBTTagCompound(data));
		this.playerUsername = data.readUTF();
		this.slotId = data.readInt();
		this.dimId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		NBTTagCompound stackTagT = null;
		stack.writeToNBT(stackTagT);
		Packet.writeNBTTagCompound(stackTagT, dos);

		dos.writeUTF(this.playerUsername);
		dos.writeInt(this.slotId);
		dos.writeInt(this.dimId);
	}

	@Override
	public void execute(INetworkManager network, Player player) {
		try {
			WorldServer world = DimensionManager.getWorld(this.dimId);

			if (world != null) {
				if (!world.isRemote) {
					for (Object pl : world.playerEntities) {
						if (((EntityPlayer) pl).username == this.playerUsername) {
							((EntityPlayer) pl).inventory.mainInventory[slotId] = this.stack;
						}
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
