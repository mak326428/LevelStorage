package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.util.Keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import makmods.levelstorage.api.event.BootsFlyingEvent;
import makmods.levelstorage.api.event.TeslaRayEvent;
import makmods.levelstorage.armor.ArmorFunctions.IForcefieldChestplate.WearType;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.network.PacketTeslaRay;
import makmods.levelstorage.network.PacketTypeHandler;
import makmods.levelstorage.proxy.LSKeyboard;
import makmods.levelstorage.registry.FlightRegistry;
import makmods.levelstorage.registry.FlightRegistry.Flight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
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

	static {
		MinecraftForge.EVENT_BUS.register(new ForcefieldEventHandler());
	}

	public interface IForcefieldChestplate {
		int energyPerTick();

		WearType getWearType();

		public enum WearType {
			INVENTORY, ARMOR
		}
	}

	public static class ForcefieldEventHandler {

		public static final int ENTITY_MAX_DISTANCE = 16;

		public ItemStack playerGetItem(EntityPlayer p) {
			InventoryPlayer inv = p.inventory;
			for (ItemStack stack : inv.mainInventory) {
				if (stack != null) {
					if (stack.getItem() instanceof IForcefieldChestplate) {
						if (((IForcefieldChestplate) stack.getItem())
								.getWearType() == WearType.INVENTORY)
							return stack;
					}
				}
			}
			for (ItemStack stack : inv.armorInventory) {
				if (stack != null) {
					if (stack.getItem() instanceof IForcefieldChestplate) {
						if (((IForcefieldChestplate) stack.getItem())
								.getWearType() == WearType.ARMOR)
							return stack;
					}
				}
			}
			return null;
		}

		@ForgeSubscribe(priority = EventPriority.HIGHEST)
		public void onLivingUpdate(LivingUpdateEvent event) {
			World w = event.entityLiving.worldObj;
			if (!w.isRemote) {
				if (event.entityLiving instanceof EntityPlayer)
					return;
				for (Object playerObj : w.playerEntities) {
					EntityPlayer p = (EntityPlayer) playerObj;
					ItemStack armor = playerGetItem(p);
					if (armor != null) {
						double newPosX = event.entityLiving.posX
								+ event.entityLiving.motionX;
						double newPosY = event.entityLiving.posY
								+ event.entityLiving.motionY;
						double newPosZ = event.entityLiving.posZ
								+ event.entityLiving.motionZ;

						double distanceX = Math.abs(p.posX - newPosX);
						double distanceY = Math.abs(p.posY - newPosY);
						double distanceZ = Math.abs(p.posZ - newPosZ);
						double totalDistance = distanceX + distanceY
								+ distanceZ;
						if (totalDistance > ENTITY_MAX_DISTANCE) {
							if (totalDistance < ENTITY_MAX_DISTANCE) {
								if (ElectricItem.manager.canUse(armor,
										((IForcefieldChestplate) armor
												.getItem()).energyPerTick())) {
									if (event.entityLiving instanceof EntityMob) {
										event.entityLiving
												.attackEntityFrom(
														LSDamageSource.forcefieldArmorInstaKill,
														40);
										ElectricItem.manager.use(armor,
												((IForcefieldChestplate) armor
														.getItem())
														.energyPerTick(), p);
									}
								}
							}
						}
					}
				}
			}
		}
	}

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
		double var21 = 128.0D;
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

	public static void helmetFunctions(World world, EntityPlayer player,
			ItemStack itemStack, int RAY_COST, int ENTITY_HIT_COST,
			int FOOD_COST) {
		if (LSKeyboard.getInstance().isKeyDown(player,
				LSKeyboard.RAY_SHOOT_KEY_NAME)) {
			if (ElectricItem.manager.canUse(itemStack, RAY_COST)) {
				if (!world.isRemote)
					ElectricItem.manager.use(itemStack, RAY_COST, player);
				int x = 0, y = 0, z = 0;
				MovingObjectPosition mop = getMovingObjectPositionFromPlayer(
						world, player, true);
				if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
					float xOff = (float) (mop.blockX - player.posX);
					float yOff = (float) (mop.blockY - player.posY);
					float zOff = (float) (mop.blockZ - player.posZ);
					x = mop.blockX;
					y = mop.blockY;
					z = mop.blockZ;

					// EnergyRayFX p = new EnergyRayFX(world, player.posX,
					// player.posY, player.posZ, x, y, z, 48, 141, 255, 40);
					if (!world.isRemote) {
						PacketTeslaRay packet = new PacketTeslaRay();
						packet.initX = player.posX;
						packet.initY = player.posY + 1.6f;
						packet.initZ = player.posZ;
						packet.tX = x;
						packet.tY = y;
						packet.tZ = z;
						TeslaRayEvent tre = new TeslaRayEvent(world, player,
								packet.initX, packet.initY, packet.initZ,
								packet.tX, packet.tY, packet.tZ);
						MinecraftForge.EVENT_BUS.post(tre);
						if (!tre.isCanceled()) {
							packet.initX = tre.getInitX();
							packet.initY = tre.getInitY();
							packet.initZ = tre.getInitZ();
							packet.tX = tre.getTargetX();
							packet.tY = tre.getTargetY();
							packet.tZ = tre.getTargetZ();
							PacketDispatcher.sendPacketToAllAround(player.posX,
									player.posY, player.posZ, 128.0F,
									world.provider.dimensionId,
									PacketTypeHandler.populatePacket(packet));
							ArrayList<Object> entities = new ArrayList<Object>();
							for (Object e : world.loadedEntityList) {
								if (!(e instanceof EntityPlayer)) {
									double distanceX = Math
											.abs(((Entity) e).posX - x);
									double distanceY = Math
											.abs(((Entity) e).posY - y);
									double distanceZ = Math
											.abs(((Entity) e).posZ - z);
									if ((distanceX + distanceY + distanceZ) < 4.0F) {
										entities.add(e);
									}
								}
							}
							if (entities.size() == 0) {
								if (new Random().nextBoolean()) {
									if (ElectricItem.manager.canUse(itemStack,
											ENTITY_HIT_COST)) {
										ElectricItem.manager.use(itemStack,
												ENTITY_HIT_COST, player);
										CommonHelper.spawnLightning(world, x,
												y, z, false);
									}
								}
							}
							for (Object obj : entities) {
								if (ElectricItem.manager.canUse(itemStack,
										ENTITY_HIT_COST)) {
									ElectricItem.manager.use(itemStack,
											ENTITY_HIT_COST, player);
									((Entity) obj).attackEntityFrom(
											LSDamageSource.teslaRay,
											20 + new Random().nextInt(15));
								}

							}
						}
					}
				}
			}
		}
		if (!world.isRemote) {
			if (player.getFoodStats().getFoodLevel() < 18) {
				if (ElectricItem.manager.canUse(itemStack, FOOD_COST)) {
					ElectricItem.manager.use(itemStack, FOOD_COST, player);
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
						ElectricItem.manager.use(itemStack, cost.intValue(),
								null);
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
				ElectricItem.manager.use(itemStack, 1000, null);
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
				ElectricItem.manager.use(itemStack, 4000, null);
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
			FlightRegistry.instance.modEnabledFlights.clear();
			FlightRegistry.instance.modEnabledFlights.put(Reference.MOD_ID,
					new Flight(player, true));
			player.capabilities.allowFlying = true;
			if (player.capabilities.isFlying) {
				if (Keys.instance.isBoostKeyDown(player)) {
					float boost = 0.44f;
					player.moveFlying(0.0F, 1.0F, boost);
					if (!world.isRemote) {
						ElectricItem.manager.use(itemStack, energy * 3, player);
					}
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode) {
						ElectricItem.manager.use(itemStack, energy, player);

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
