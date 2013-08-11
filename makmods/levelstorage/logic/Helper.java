package makmods.levelstorage.logic;

import makmods.levelstorage.registry.SyncType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

	public static boolean compareStacksGenerallyNoStackSize(ItemStack s1,
			ItemStack s2) {
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;
		return (s1.itemID == s2.itemID)
				&& (s1.getItemDamage() == s2.getItemDamage());
	}

	/*public static void addRecipe(ItemStack res, ItemStack... data) {
		int currIngr = 0;
		char[] letters = { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o' };
		String row1 = "";
		String row2 = "";
		String row3 = "";
		for (ItemStack dt : data) {
			currIngr++;
			if (currIngr > 0 && currIngr < 4) {
				if (dt != null) {
					row1 += letters[currIngr - 1];
				} else {
					row1 += " ";
				}
			}
			if (currIngr > 3 && currIngr < 7) {
				if (dt != null) {
					row2 += letters[currIngr - 1];
				} else {
					row2 += " ";
				}
			}
			if (currIngr > 6 && currIngr < 10) {
				if (dt != null) {
					row3 += letters[currIngr - 1];
				} else {
					row3 += " ";
				}
			}
		}

		String total = row1 + row2 + row3;
		total = total.replace(" ", "");
		for (char ch : letters) {
			if (total.) {
				
			}
		}
	}*/

	public static boolean isNumberNegative(int number) {
		return number != Math.abs(number);
	}

	public static int getRandomNumber(int signs) {
		// Hacky hacky hack hack
		String max = "1";
		for (int i = 0; i < signs; i++) {
			max += "0";
		}
		int maxSign = Integer.parseInt(max);
		boolean negativeSign = Math.random() < 0.5D;
		if (negativeSign)
			return (int) (-(Math.random() * maxSign));
		else
			return (int) (Math.random() * maxSign);
	}

	public static void spawnLightning(World w, int x, int y, int z, boolean exp) {
		EntityLightningBolt lightning = new EntityLightningBolt(w, x, y, z);
		w.addWeatherEffect(lightning);
		if (exp) {
			w.createExplosion(lightning, x, y, z,
					(float) (Math.random() * 1.0f), true);
		}
		w.spawnEntityInWorld(lightning);

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
