package makmods.levelstorage.logic;

import makmods.levelstorage.registry.SyncType;
import net.minecraft.item.ItemStack;

public class Helper {
	public static SyncType invertType(SyncType type) {
		if (type == SyncType.RECEIVER)
			return SyncType.TRANSMITTER;
		if (type == SyncType.TRANSMITTER)
			return SyncType.RECEIVER;
		return null;
	}

	public static boolean compareStacksGenerally(ItemStack s1, ItemStack s2) {
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;
		return (s1.itemID == s2.itemID)
				&& (s1.getItemDamage() == s2.getItemDamage())
				&& (s1.stackSize == s2.stackSize);
	}
	
	public static String getNiceStackName(ItemStack stack) {
		StringBuilder sb = new StringBuilder();
		if (stack == null) {
			sb.append("Nothing");
			return sb.toString();
		}
		sb.append(stack.stackSize);
		sb.append(" x ");
		sb.append(stack.getDisplayName());
		return sb.toString();
	}
}
