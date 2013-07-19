package makmods.levelstorage.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;

public class CommonProxy {
	public void init() {
	}

	public void messagePlayer(EntityPlayer player, String message, Object[] args) {
		if ((player instanceof EntityPlayerMP)) {
			ChatMessageComponent msg;
			if (args.length > 0)
				msg = ChatMessageComponent.func_111082_b(message, args);
			else {
				msg = ChatMessageComponent.func_111077_e(message);
			}

			((EntityPlayerMP) player).sendChatToPlayer(msg);
		}
	}
}
