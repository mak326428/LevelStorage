package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.Helper;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.network.PacketTeslaRay;
import makmods.levelstorage.network.PacketTypeHandler;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.render.EnergyRayFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//import makmods.levelstorage.render.EnergyRayFX;

public class ItemArmorTeslaHelmet extends ItemArmor implements ISpecialArmor,
        IMetalArmor, IElectricItem {

	public static final String UNLOCALIZED_NAME = "armorTeslaHelmet";
	public static final String NAME = "Tesla Helmet";

	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ARMOR_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 900;
	public static final int FOOD_COST = 10000;
	public static final int RAY_COST = 100;

	public ItemArmorTeslaHelmet() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
		        LevelStorage.getAndIncrementCurrId()).getInt(),
		        EnumArmorMaterial.DIAMOND, 5, 0);
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
	}

	public static HashMap<Integer, Integer> potionRemovalCost = new HashMap<Integer, Integer>();

	static {
		potionRemovalCost.put(Integer.valueOf(Potion.poison.id),
		        Integer.valueOf(10000));
		potionRemovalCost.put(Integer.valueOf(Potion.wither.id),
		        Integer.valueOf(25000));
	}

	public static ItemStack playerGetArmor(EntityPlayer p) {
		InventoryPlayer inv = p.inventory;
		ItemStack found = null;

		for (ItemStack st : inv.armorInventory) {
			if (st != null && st.getItem() instanceof ItemArmorTeslaHelmet)
				found = st;
		}

		return found;
	}

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

	public static final int ENTITY_HIT_COST = 10000;
	public static final int HEAL_COST = 1000;

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
	        ItemStack itemStack) {
		if (player.isSneaking()
		        && IC2Access.instance.isKeyDown("Boost", player)) {
			if (ElectricItem.manager.canUse(itemStack, RAY_COST)) {
				if (!world.isRemote)
					ElectricItem.manager.use(itemStack, RAY_COST, player);
				int x = 0, y = 0, z = 0;
				MovingObjectPosition mop = this
				        .getMovingObjectPositionFromPlayer(world, player, true);
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
						PacketDispatcher.sendPacketToAllAround(player.posX,
						        player.posY, player.posZ, 128.0F,
						        world.provider.dimensionId,
						        PacketTypeHandler.populatePacket(packet));
						ArrayList<Object> entities = new ArrayList<Object>();
						for (Object e : world.loadedEntityList) {
							if (!(e instanceof EntityPlayer)) {
								double distanceX = Math.abs(((Entity) e).posX
								        - x);
								double distanceY = Math.abs(((Entity) e).posY
								        - y);
								double distanceZ = Math.abs(((Entity) e).posZ
								        - z);
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
									Helper.spawnLightning(world, x, y, z, false);
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
		if (!world.isRemote) {
			if (player.getFoodStats().getFoodLevel() < 18) {
				if (ElectricItem.manager.canUse(itemStack, FOOD_COST)) {
					ElectricItem.manager.use(itemStack, FOOD_COST, player);
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
			// if (player.shouldHeal()) {
			// if (ElectricItem.manager.canUse(itemStack, HEAL_COST)) {
			// ElectricItem.manager.use(itemStack, HEAL_COST,
			// player);
			// player.addPotionEffect(new PotionEffect(Potion.regeneration.id,
			// 100, 3));
			// // The following line heals player like crazy, making armor
			// obsolete. I won't use it.
			// player.heal(0.5f);
			// }
			// }
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
	        int layer) {
		return ClientProxy.ARMOR_LEVITATION_BOOTS_TEXTURE;
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableTeslaHelmetCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        ModItems.instance.itemArmorTeslaHelmet), "tit", "iqi",
			        "lil", Character.valueOf('t'), IC2Items.TESLA_COIL,
			        Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
			                .valueOf('q'), IC2Items.QUANTUM_HELMET, Character
			                .valueOf('l'), new ItemStack(
			                ModItems.instance.itemStorageFourMillion));
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.TESLA_HELMET_TEXTURE);
	}

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
	        ItemStack armor, DamageSource source, double damage, int slot) {
		if (source.isUnblockable())
			return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);

		double absorptionRatio = getBaseAbsorptionRatio()
		        * getDamageAbsorptionRatio();
		int energyPerDamage = ENERGY_PER_DAMAGE;

		int damageLimit = energyPerDamage > 0 ? 25
		        * ElectricItem.manager.getCharge(armor) / energyPerDamage : 0;

		return new ISpecialArmor.ArmorProperties(0, absorptionRatio,
		        damageLimit);
	}

	private double getBaseAbsorptionRatio() {
		return 0.15D;
	}

	public double getDamageAbsorptionRatio() {
		return 1.0D;
	}

	public void damageArmor(EntityLivingBase entity, ItemStack stack,
	        DamageSource source, int damage, int slot) {
		ElectricItem.manager.discharge(stack, damage * ENERGY_PER_DAMAGE,
		        2147483647, true, false);
	}

	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (ElectricItem.manager.getCharge(armor) >= ENERGY_PER_DAMAGE) {
			return (int) Math.round(20.0D * getBaseAbsorptionRatio()
			        * getDamageAbsorptionRatio());
		}
		return 0;
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
		return 10000;
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

	@Override
	public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
		return true;
	}

}
