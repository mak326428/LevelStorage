package makmods.levelstorage.logic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.dimension.LSDimensions;
import makmods.levelstorage.registry.SyncType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonHelper {
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
	
	public static ItemStack createEnchantedBook(Enchantment ench, int level) {
		if (ench == null)
			return null;
		ItemStack book = new ItemStack(Item.enchantedBook);
		book.stackTagCompound = new NBTTagCompound();
		NBTTagList storedEnchs = new NBTTagList();
		NBTTagCompound enchanted = new NBTTagCompound();
		enchanted.setShort("id", (short) ench.effectId);
		enchanted.setShort("lvl", (short) level);
		storedEnchs.appendTag(enchanted);
		book.getTagCompound().setTag("StoredEnchantments", storedEnchs);
		return book;
	}

	public static void dropBlockInWorld(World par1World, int par2, int par3,
			int par4, ItemStack par5ItemStack) {
		if (!par1World.isRemote
				&& par1World.getGameRules().getGameRuleBooleanValue(
						"doTileDrops")) {
			float f = 0.7F;
			double d0 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d1 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d2 = (double) (par1World.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(par1World, (double) par2
					+ d0, (double) par3 + d1, (double) par4 + d2, par5ItemStack);
			entityitem.delayBeforeCanPickup = 10;
			par1World.spawnEntityInWorld(entityitem);
		}
	}

	/**
	 * Just a shortcut
	 * 
	 * @param player
	 *            Player to message to
	 * @param message
	 *            Message
	 */
	public static void messagePlayer(EntityPlayer player, String message) {
		LevelStorage.proxy.messagePlayer(player, message, new Object[0]);
	}

	public static <T> List<T> convertToList(T[] elements) {
		ArrayList<T> list = Lists.newArrayList();
		for (T element : elements)
			list.add(element);
		return list;
	}

	/**
	 * Creates a EntityItem in the world with the given arguments
	 * 
	 * @param world
	 *            World
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @param z
	 *            Z
	 * @param stack
	 *            Stack to be dropped to the world
	 */
	public static void dropBlockInWorld_exact(World world, double x, double y,
			double z, ItemStack stack) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			EntityItem entityitem = new EntityItem(world, x, y, z, stack);
			entityitem.delayBeforeCanPickup = 0;
			world.spawnEntityInWorld(entityitem);
		}
	}

	public static boolean handleMachineRightclick(World world, int x, int y,
			int z, EntityPlayer player) {
		if (player.isSneaking())
			return false;
		else {
			if (!world.isRemote) {
				// WARNING
				// player.travelToDimension(LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID);
				TileEntity tile = world.getBlockTileEntity(x, y, z);
				if (tile != null) {
					player.openGui(LevelStorage.instance, 16384, world, x, y, z);
				}
			}

			return true;
		}
	}

	public static void dropBlockItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory))
			return;
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z
						+ rz, new ItemStack(item.itemID, item.stackSize,
						item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound(
							(NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
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

	/*
	 * public static void addRecipe(ItemStack res, ItemStack... data) { int
	 * currIngr = 0; char[] letters = { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i',
	 * 'o' }; String row1 = ""; String row2 = ""; String row3 = ""; for
	 * (ItemStack dt : data) { currIngr++; if (currIngr > 0 && currIngr < 4) {
	 * if (dt != null) { row1 += letters[currIngr - 1]; } else { row1 += " "; }
	 * } if (currIngr > 3 && currIngr < 7) { if (dt != null) { row2 +=
	 * letters[currIngr - 1]; } else { row2 += " "; } } if (currIngr > 6 &&
	 * currIngr < 10) { if (dt != null) { row3 += letters[currIngr - 1]; } else
	 * { row3 += " "; } } }
	 * 
	 * String total = row1 + row2 + row3; total = total.replace(" ", ""); for
	 * (char ch : letters) { if (total.) {
	 * 
	 * } } }
	 */

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
		sb.append(" of ");
		String name = stack.getDisplayName();
		if (name.endsWith(".name")) {
			if (FMLCommonHandler.instance().getSide().isClient())
				sb.append(LanguageRegistry.instance().getStringLocalization(
						name));
			else
				sb.append(name);
		} else
			sb.append(stack.getDisplayName());
		return sb.toString();
	}
	
	public static int getDistance(double startX, double startY,
			double startZ, double endX, double endY, double endZ) {
		double x = endX - startX;
		double y = endY - startY;
		double z = endZ - startZ;
		return (int)Math.round(Math.sqrt(x * x + y * y + z * z));
	}

	public static int getDistanceFloor(double startX, double startY,
			double startZ, double endX, double endY, double endZ) {
		double x = endX - startX;
		double y = endY - startY;
		double z = endZ - startZ;
		return (int)Math.floor(Math.sqrt(x * x + y * y + z * z));
	}
	
	public static int getDistanceCeil(double startX, double startY,
			double startZ, double endX, double endY, double endZ) {
		double x = endX - startX;
		double y = endY - startY;
		double z = endZ - startZ;
		return (int)Math.ceil(Math.sqrt(x * x + y * y + z * z));
	}
}
