package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.util.Keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import makmods.levelstorage.api.event.BootsFlyingEvent;
import makmods.levelstorage.api.event.TeslaRayEvent;
import makmods.levelstorage.armor.antimatter.ItemArmorAntimatterBase;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.EntityUtil;
import makmods.levelstorage.network.packet.PacketTeslaRay;
import makmods.levelstorage.network.packet.PacketTypeHandler;
import makmods.levelstorage.proxy.LSKeyboard;
import makmods.levelstorage.registry.FlightRegistry;
import makmods.levelstorage.registry.FlightRegistry.Flight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * Refactored armor functions
 * 
 * @author mak326428
 * 
 */
public class ArmorFunctions {

	public static HashMap<EntityPlayer, Boolean> onGroundMap = new HashMap<EntityPlayer, Boolean>();
	private static float jumpCharge;
	public static HashMap<EntityPlayer, Integer> speedTickerMap = new HashMap<EntityPlayer, Integer>();

	public static void extinguish(EntityPlayer ep, World w) {
		if (!w.isRemote) {
		}
		ep.extinguish();
	}

	public static MovingObjectPosition getMovingObjectPositionFromPlayer(
			World par1World, EntityPlayer par2EntityPlayer, boolean par3) {
		float var4 = 1.0F;
		float var5 = par2EntityPlayer.prevRotationPitch
				+ (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch)
				* var4;
		float var6 = par2EntityPlayer.prevRotationYaw
				+ (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw)
				* var4;
		double var7 = par2EntityPlayer.prevPosX
				+ (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * var4;
		double var9 = par2EntityPlayer.prevPosY
				+ (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * var4
				+ 1.62D - par2EntityPlayer.yOffset;
		double var11 = par2EntityPlayer.prevPosZ
				+ (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * var4;
		Vec3 var13 = par1World.getWorldVec3Pool().getVecFromPool(var7, var9,
				var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 1024.0D;
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20
				* var21);
		return par1World.rayTraceBlocks_do_do(var13, var23, par3, !par3);
	}

	public static HashMap<Integer, Integer> potionRemovalCost = new HashMap<Integer, Integer>();

	static {
		potionRemovalCost.put(Integer.valueOf(Potion.poison.id),
				Integer.valueOf(10000));
		potionRemovalCost.put(Integer.valueOf(Potion.wither.id),
				Integer.valueOf(25000));
	}

	public static void antimatterLeggingsFunctions(World world,
			EntityPlayer player, ItemStack armor) {
		if (world.isRemote)
			return;
		if (LSKeyboard.getInstance().isKeyDown(player,
				LSKeyboard.RAY_SHOOT_KEY_NAME)
				&& player.isSneaking()) {
			int x = 0, y = 0, z = 0;
			MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world,
					player, true);
			if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
				if (ElectricItem.manager.canUse(armor,
						ItemArmorAntimatterBase.EU_PER_TELEPORT)) {
					ElectricItem.manager.discharge(armor, ItemArmorAntimatterBase.EU_PER_TELEPORT, Integer.MAX_VALUE, true, false);
				}
				x = mop.blockX;
				y = mop.blockY;
				z = mop.blockZ;
				int sideHit = mop.sideHit;
				BlockLocation bl = new BlockLocation(x, y, z);
				bl = bl.move(ForgeDirection.getOrientation(sideHit), 1);
				player.setPositionAndUpdate(bl.getX(), bl.getY(), bl.getZ());
			}
		}
	}

	public static void helmetFunctions(World world, EntityPlayer player,
			ItemStack itemStack, int RAY_COST,
			int FOOD_COST) {
		if (LSKeyboard.getInstance().isKeyDown(player,
				LSKeyboard.RAY_SHOOT_KEY_NAME)
				&& !player.isSneaking()) {
			if (ElectricItem.manager.canUse(itemStack, RAY_COST)) {
				if (!world.isRemote) {
					Entity toShoot = EntityUtil.getTarget(world, player, 128);
					if (toShoot != null) {
						ElectricItem.manager.discharge(itemStack, RAY_COST, Integer.MAX_VALUE, true, false);
						PacketTeslaRay.issue(player.posX, player.posY + 1.6,
								player.posZ, toShoot.posX, toShoot.posY,
								toShoot.posZ);
						toShoot.attackEntityFrom(LSDamageSource.teslaRay,
								world.rand.nextInt(5) + world.rand.nextInt(6)
										+ world.rand.nextInt(12) + 2);
					}
				}

			}
		}

		if (!world.isRemote) {
			if (player.getFoodStats().getFoodLevel() < 18) {
				if (ElectricItem.manager.canUse(itemStack, FOOD_COST)) {
					ElectricItem.manager.discharge(itemStack, FOOD_COST, Integer.MAX_VALUE, true, false);
					player.getFoodStats().addStats(20, 20);
				}
			}
			if (player.getAir() < 100) {
				player.setAir(200);
			}
			LinkedList<PotionEffect> lk = new LinkedList(
					player.getActivePotionEffects());
			for (PotionEffect effect : lk) {
				int id = effect.getPotionID();

				Integer cost = (Integer) potionRemovalCost.get(Integer
						.valueOf(id));

				if (cost != null) {
					cost = Integer.valueOf(cost.intValue()
							* (effect.getAmplifier() + 1));

					if (ElectricItem.manager.canUse(itemStack, cost.intValue())) {
						ElectricItem.manager.discharge(itemStack, cost.intValue(), Integer.MAX_VALUE, true, false);
						player.removePotionEffect(id);
					}
				}
			}
		}
	}

	public static void speedUp(EntityPlayer player, ItemStack itemStack) {
		if (!speedTickerMap.containsKey(player))
			speedTickerMap.put(player, 0);
		float speed = 0.66F;
		if ((ElectricItem.manager.canUse(itemStack, 1000))
				&& ((player.onGround) || (player.isInWater()))
				&& (player.isSprinting())) {
			int speedTicker = speedTickerMap.containsKey(player) ? ((Integer) speedTickerMap
					.get(player)).intValue() : 0;
			speedTicker++;

			if (speedTicker >= 10) {
				speedTicker = 0;
				ElectricItem.manager.discharge(itemStack, 1000, Integer.MAX_VALUE, true, false);
			}
			speedTickerMap.remove(player);
			speedTickerMap.put(player, Integer.valueOf(speedTicker));

			if (player.isInWater()) {
				speed = 0.1F;
				if (Keys.instance.isJumpKeyDown(player))
					player.motionY += 0.1000000014901161D;
			}

			if (speed > 0.0F)
				player.moveFlying(0.0F, 1.0F, speed);
		}
	}

	public static void jumpBooster(World world, EntityPlayer player,
			ItemStack itemStack) {
		boolean boostKey = Keys.instance.isBoostKeyDown(player);

		if (!onGroundMap.containsKey(player))
			onGroundMap.put(player, true);
		if (!world.isRemote) {
			boolean wasOnGround = onGroundMap.containsKey(player) ? ((Boolean) onGroundMap
					.get(player)).booleanValue() : true;

			if ((wasOnGround) && (!player.onGround)
					&& (Keys.instance.isJumpKeyDown(player))
					&& (Keys.instance.isBoostKeyDown(player))) {
				ElectricItem.manager.discharge(itemStack, 4000, Integer.MAX_VALUE, true, false);
			}
			onGroundMap.remove(player);
			onGroundMap.put(player, Boolean.valueOf(player.onGround));
		} else {
			if ((ElectricItem.manager.canUse(itemStack, 4000))
					&& (player.onGround))
				jumpCharge = 2.0F;

			if ((player.motionY >= 0.0D) && (jumpCharge > 0.0F)
					&& (!player.isInWater())) {
				if ((Keys.instance.isJumpKeyDown(player) && (Keys.instance
						.isBoostKeyDown(player)))) {
					if (jumpCharge == 2.0F) {
						player.motionX *= 5.0D;
						player.motionZ *= 5.0D;
					}

					player.motionY += jumpCharge * 0.3F;
					jumpCharge = ((float) (jumpCharge * 0.75D));
				} else if (jumpCharge < 1.0F) {
					jumpCharge = 0.0F;
				}

			}
		}
	}

	public static void fly(int energy, EntityPlayer player,
			ItemStack itemStack, World world) {
		if (ElectricItem.manager.canUse(itemStack, energy)) {
			// TODO: the clear() part might cause issues in SMP
			// TODO: investigate and fix if problem really exists
			FlightRegistry.instance.modEnabledFlights.clear();
			FlightRegistry.instance.modEnabledFlights.put(Reference.MOD_ID,
					new Flight(player, true));
			player.capabilities.allowFlying = true;
			if (player.capabilities.isFlying) {
				if (Keys.instance.isBoostKeyDown(player)) {
					float boost = 0.44f;
					player.moveFlying(0.0F, 1.0F, boost);
					if (!world.isRemote) {
						ElectricItem.manager.discharge(itemStack, energy * 3, Integer.MAX_VALUE, true, false);
					}
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode) {
						ElectricItem.manager.discharge(itemStack, energy, Integer.MAX_VALUE, true, false);
						MinecraftForge.EVENT_BUS.post(new BootsFlyingEvent(
								player, itemStack));
					}
				}
			}
		} else {
			FlightRegistry.instance.modEnabledFlights.clear();
			FlightRegistry.instance.modEnabledFlights.put(Reference.MOD_ID,
					new Flight(player, false));
		}
	}

}
