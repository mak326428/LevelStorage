package makmods.levelstorage.command;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandChargeItems extends CommandBase {

	@Override
	public String getCommandName() {
		return "ls-charge-items";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/" + getCommandName() + " [<player name>]";
	}

	public List addTabCompletionOptions(ICommandSender par1ICommandSender,
			String[] args) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, getPlayers());
		return null;
	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public int chargeItems(EntityPlayerMP player) {
		int charged = 0;
		InventoryPlayer inv = player.inventory;
		for (ItemStack stack : inv.armorInventory)
			if (stack != null)
				if (stack.getItem() instanceof IElectricItem)
					charged += ElectricItem.manager.charge(stack,
							Integer.MAX_VALUE, 10, true, false);
		for (ItemStack stack : inv.mainInventory)
			if (stack != null)
				if (stack.getItem() instanceof IElectricItem)
					charged += ElectricItem.manager.charge(stack,
							Integer.MAX_VALUE, 10, true, false);
		return charged;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if (astring.length == 1) {
			String playerName = astring[0];
			EntityPlayerMP player = MinecraftServer
					.getServerConfigurationManager(MinecraftServer.getServer())
					.getPlayerForUsername(playerName);
			if (player == null)
				throw new PlayerNotFoundException();
			int charged = chargeItems(player);
			icommandsender.sendChatToPlayer(ChatMessageComponent
					.createFromText("\247dCharged all items inside "
							+ playerName + "'s inventory. (used "
								+ charged + " EU)"));
			return;
		}
		EntityPlayerMP player = getCommandSenderAsPlayer(icommandsender);
		int charged = chargeItems(player);
		icommandsender
				.sendChatToPlayer(ChatMessageComponent
						.createFromText("\247dCharged all items inside your inventory. (used "
								+ charged + " EU)"));
	}

}
