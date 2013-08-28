package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.BootsFlyingEvent;
import makmods.levelstorage.armor.ItemArmorForcefieldChestplate;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorSupersonicLeggings;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.Helper;
import makmods.levelstorage.network.PacketTeslaRay;
import makmods.levelstorage.network.PacketTypeHandler;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemQuantumRing extends Item implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "itemQuantumRing";
	public static final String NAME = "Quantum Ring";

	public static final int TIER = 3;
	public static final int STORAGE = 8 * 1000 * 1000;
	// Energy per 1 damage
	public static final int ENERGY_PER_DAMAGE = 100;

	public ItemQuantumRing() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
		        LevelStorage.getAndIncrementCurrId()).getInt());
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onDamage(LivingHurtEvent event) {
		// if (event.source.isUnblockable() && event.source ==
		// DamageSource.fall))
		// return;
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ItemStack ring = getRing(player);
			if (ring != null) {
				int energy = (int) (event.ammount * ENERGY_PER_DAMAGE);
				if (ElectricItem.manager.canUse(ring, energy)) {
					ElectricItem.manager.use(ring, energy, player);
					event.setCanceled(true);
				}
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onLivingUpdate(LivingUpdateEvent event) {
		World w = event.entityLiving.worldObj;
		if (!w.isRemote) {
			if (event.entityLiving instanceof EntityPlayer)
				return;
			for (Object playerObj : w.playerEntities) {
				EntityPlayer p = (EntityPlayer) playerObj;
				ItemStack armor = getRing(p);
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
					double totalDistance = distanceX + distanceY + distanceZ;
					// if (totalDistance > ENTITY_MAX_DISTANCE) {
					if (totalDistance < ItemArmorForcefieldChestplate.ENTITY_MAX_DISTANCE) {
						if (ElectricItem.manager
						        .canUse(armor,
						                ItemArmorForcefieldChestplate.ENERGY_PER_TICK_ENTITIES)) {
							if (event.entityLiving instanceof EntityMob) {
								event.entityLiving
								        .attackEntityFrom(
								                LSDamageSource.forcefieldArmorInstaKill,
								                40);
								ElectricItem.manager
								        .use(armor,
								                ItemArmorForcefieldChestplate.ENERGY_PER_TICK_ENTITIES,
								                p);
							}
						}
					}
				}
			}
		}
	}

	public static ItemStack getRing(EntityPlayer ep) {
		InventoryPlayer inventory = ep.inventory;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack != null)
				if (stack.getItem() instanceof ItemQuantumRing)
					return stack;
		}
		return null;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 100000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.QUANTUM_RING_TEXTURE);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\247bThe most endgame item. Probably.");
		par3List.add("For people who don't want to wear");
		par3List.add("ultimate armor, but still want to");
		par3List.add("access its features");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	private float jumpCharge;

	@Override
	protected MovingObjectPosition getMovingObjectPositionFromPlayer(
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

	public void onUpdate(ItemStack itemStack, World world, Entity par3Entity,
	        int par4, boolean par5) {

		if (par3Entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) par3Entity;

			// CHESTPLATE ABILIIES
			if (!world.isRemote)
				player.extinguish();
			// END OF CHESTPLATE ABILITIES

			// BOOTS SPECIAL ABILITIES
			boolean boostKey = IC2Access.instance.isKeyDown("Boost", player);
			// QUANTUM SUIT ABILITIES
			if (!ItemArmorLevitationBoots.onGroundMap.containsKey(player))
				ItemArmorLevitationBoots.onGroundMap.put(player, true);
			if (!world.isRemote) {
				boolean wasOnGround = ItemArmorLevitationBoots.onGroundMap
				        .containsKey(player) ? ((Boolean) ItemArmorLevitationBoots.onGroundMap
				        .get(player)).booleanValue() : true;

				if ((wasOnGround) && (!player.onGround)
				        && (IC2Access.instance.isKeyDown("Jump", player))
				        && (IC2Access.instance.isKeyDown("Boost", player))) {
					ElectricItem.manager.use(itemStack, 4000, null);
				}
				ItemArmorLevitationBoots.onGroundMap.remove(player);
				ItemArmorLevitationBoots.onGroundMap.put(player,
				        Boolean.valueOf(player.onGround));
			} else {
				if ((ElectricItem.manager.canUse(itemStack, 4000))
				        && (player.onGround))
					this.jumpCharge = 2.0F;

				if ((player.motionY >= 0.0D) && (this.jumpCharge > 0.0F)
				        && (!player.isInWater())) {
					if ((IC2Access.instance.isKeyDown("Jump", player) && (IC2Access.instance
					        .isKeyDown("Boost", player)))) {
						if (this.jumpCharge == 2.0F) {
							player.motionX *= 3.5D;
							player.motionZ *= 3.5D;
						}

						player.motionY += this.jumpCharge * 0.3F;
						this.jumpCharge = ((float) (this.jumpCharge * 0.75D));
					} else if (this.jumpCharge < 1.0F) {
						this.jumpCharge = 0.0F;
					}

				}// END OF QUANTUM SUIT ABILITIES

			}
			if (ElectricItem.manager.canUse(itemStack,
			        ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK)) {
				player.capabilities.allowFlying = true;
				if (player.capabilities.isFlying) {
					if (boostKey) {
						float boost = 0.44f;
						player.moveFlying(0.0F, 1.0F, boost);
						if (!world.isRemote) {
							ElectricItem.manager
							        .use(itemStack,
							                ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK * 3,
							                player);
						}
					}
					if (!world.isRemote) {
						if (!player.capabilities.isCreativeMode) {
							ElectricItem.manager
							        .use(itemStack,
							                ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK,
							                player);

							MinecraftForge.EVENT_BUS.post(new BootsFlyingEvent(
							        player, itemStack));
						}
					}
				}
			} else {
				player.capabilities.allowFlying = false;
				if (player.capabilities.isFlying)
					player.capabilities.isFlying = false;
			}
			// END OF BOOTS SPECIAL ABILITIES

			// HELMET SPECIAL ABILITIES
			if (player.isSneaking()
			        && IC2Access.instance.isKeyDown("Boost", player)) {
				if (ElectricItem.manager.canUse(itemStack,
				        ItemArmorTeslaHelmet.RAY_COST)) {
					if (!world.isRemote)
						ElectricItem.manager.use(itemStack,
						        ItemArmorTeslaHelmet.RAY_COST, player);
					int x = 0, y = 0, z = 0;
					MovingObjectPosition mop = this
					        .getMovingObjectPositionFromPlayer(world, player,
					                true);
					if (mop != null
					        && mop.typeOfHit == EnumMovingObjectType.TILE) {
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
									if (ElectricItem.manager
									        .canUse(itemStack,
									                ItemArmorTeslaHelmet.ENTITY_HIT_COST)) {
										ElectricItem.manager
										        .use(itemStack,
										                ItemArmorTeslaHelmet.ENTITY_HIT_COST,
										                player);
										Helper.spawnLightning(world, x, y, z,
										        false);
									}
								}
							}
							for (Object obj : entities) {
								if (ElectricItem.manager.canUse(itemStack,
								        ItemArmorTeslaHelmet.ENTITY_HIT_COST)) {
									ElectricItem.manager
									        .use(itemStack,
									                ItemArmorTeslaHelmet.ENTITY_HIT_COST,
									                player);
									((Entity) obj).attackEntityFrom(
									        LSDamageSource.teslaRay,
									        20 + new Random().nextInt(15));
								}

							}
						}
					}
				}
			}
			if (!world.isRemote) {
				if (player.getFoodStats().getFoodLevel() < 18) {
					if (ElectricItem.manager.canUse(itemStack,
					        ItemArmorTeslaHelmet.FOOD_COST)) {
						ElectricItem.manager.use(itemStack,
						        ItemArmorTeslaHelmet.FOOD_COST, player);
						player.getFoodStats().setFoodLevel(20);
						player.getFoodStats().setFoodSaturationLevel(40F);
					}
				}
				if (player.getAir() < 100) {
					player.setAir(200);
				}
				LinkedList<PotionEffect> lk = new LinkedList(
				        player.getActivePotionEffects());
				for (PotionEffect effect : lk) {
					int id = effect.getPotionID();

					Integer cost = (Integer) ItemArmorTeslaHelmet.potionRemovalCost
					        .get(Integer.valueOf(id));
					if (cost != null) {
						cost = Integer.valueOf(cost.intValue()
						        * (effect.getAmplifier() + 1));
						if (ElectricItem.manager.canUse(itemStack,
						        cost.intValue())) {
							ElectricItem.manager.use(itemStack,
							        cost.intValue(), null);
							player.removePotionEffect(id);
						}
					}
				}
				// if (player.shouldHeal()) {
				// if (ElectricItem.manager.canUse(itemStack, HEAL_COST)) {
				// ElectricItem.manager.use(itemStack, HEAL_COST,
				// player);
				// player.addPotionEffect(new
				// PotionEffect(Potion.regeneration.id,
				// 100, 3));
				// // The following line heals player like crazy, making armor
				// obsolete. I won't use it.
				// player.heal(0.5f);
				// }
				// }
			}
			// END OF HELMET SPECIAL ABILITIES

			// LEGGINGS ABILITIES
			if (!ItemArmorSupersonicLeggings.speedTickerMap.containsKey(player))
				ItemArmorSupersonicLeggings.speedTickerMap.put(player, 0);
			float speed = 0.66F;
			if ((ElectricItem.manager.canUse(itemStack, 1000))
			        && ((player.onGround) || (player.isInWater()))
			        && (player.isSprinting())) {
				int speedTicker = ItemArmorSupersonicLeggings.speedTickerMap
				        .containsKey(player) ? ((Integer) ItemArmorSupersonicLeggings.speedTickerMap
				        .get(player)).intValue() : 0;
				speedTicker++;

				if (speedTicker >= 10) {
					speedTicker = 0;
					ElectricItem.manager.use(itemStack, 1000, null);
				}
				ItemArmorSupersonicLeggings.speedTickerMap.remove(player);
				ItemArmorSupersonicLeggings.speedTickerMap.put(player,
				        Integer.valueOf(speedTicker));

				if (player.isInWater()) {
					speed = 0.1F;
					if (IC2Access.instance.isKeyDown("Jump", player))
						player.motionY += 0.1000000014901161D;
				}

				if (speed > 0.0F)
					player.moveFlying(0.0F, 1.0F, speed);
			}
			// END OF LEGGINGS ABILITIES
		}

	}

	public static void addCraftingRecipe() {
		if (LevelStorage.recipesHardmode) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        LSBlockItemList.itemQuantumRing), "hic", "iei", "bil",
			        Character.valueOf('i'), ItemCraftingIngredients.instance
			                .getIngredient(3), Character.valueOf('e'),
			        new ItemStack(LSBlockItemList.itemStorageFourMillion),
			        Character.valueOf('h'), new ItemStack(
			                LSBlockItemList.itemArmorTeslaHelmet), Character
			                .valueOf('c'), new ItemStack(
			                LSBlockItemList.itemArmorForcefieldChestplate),
			        Character.valueOf('b'), new ItemStack(
			                LSBlockItemList.itemLevitationBoots), Character
			                .valueOf('l'), new ItemStack(
			                LSBlockItemList.itemSupersonicLeggings));
		} else {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        LSBlockItemList.itemQuantumRing), "hic", "iei", "bil",
			        Character.valueOf('i'), IC2Items.IRIDIUM_PLATE.copy(),
			        Character.valueOf('e'), new ItemStack(
			                LSBlockItemList.itemStorageFourMillion),
			        Character.valueOf('h'), new ItemStack(
			                LSBlockItemList.itemArmorTeslaHelmet), Character
			                .valueOf('c'), new ItemStack(
			                LSBlockItemList.itemArmorForcefieldChestplate),
			        Character.valueOf('b'), new ItemStack(
			                LSBlockItemList.itemLevitationBoots), Character
			                .valueOf('l'), new ItemStack(
			                LSBlockItemList.itemSupersonicLeggings));
		}

	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
	        List par3List) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
		        true, false);
		par3List.add(var4);
		par3List.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
}
