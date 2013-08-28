package makmods.levelstorage.logic;

import makmods.levelstorage.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLNetworkHandler;

public class RemotePlayer extends EntityPlayer {
	
	int x;
	int y;
	int z;
	EntityPlayer realPlayer;

	public RemotePlayer(World world, int xCoord, int yCoord, int zCoord, InventoryPlayer inv, EntityPlayer realPlayer) {
		super(world, "[" + Reference.MOD_ID + "]");
		x = xCoord;
		y = yCoord;
		z = zCoord;
		posX = xCoord;
		posY = yCoord;
		posZ = zCoord;
		inventory = inv;
		this.realPlayer = realPlayer;
	}
	
	public boolean canCommandSenderUseCommand(int i, String s) {
		return true;
	}
	
	public ChunkCoordinates getPlayerCoordinates() {
		posX = x;
		posY = y;
		posZ = z;
		return new ChunkCoordinates(x, y, z);
	}
	
	public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {
		realPlayer.sendChatToPlayer(chatmessagecomponent);
	}
	
	public void openGui(Object mod, int modGuiId, World world, int x, int y, int z)
    {
		System.out.println("Opening the GUI");
		FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
        //FMLNetworkHandler.openGui(realPlayer, mod, modGuiId, world, x, y, z);
    }

}
