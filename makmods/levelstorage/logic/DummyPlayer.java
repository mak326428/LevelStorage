package makmods.levelstorage.logic;

import makmods.levelstorage.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class DummyPlayer extends EntityPlayer {
	public DummyPlayer(World world) {
		super(world, "[" + Reference.MOD_ID + "]");
	}

	public boolean canCommandSenderUseCommand(int i, String s) {
		return false;
	}

	public ChunkCoordinates getPlayerCoordinates() {
		return null;
	}

	public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {
	}
}