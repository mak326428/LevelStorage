package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.entity.EntityTeslaRay;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorTeslaHelmet extends ItemArmor implements ISpecialArmor,
		IMetalArmor, IElectricItem {

	public static final String UNLOCALIZED_NAME = "armorTeslaHelmet";
	public static final String NAME = "Tesla Helmet";

	public static final int TIER = 3;
	public static final int STORAGE = 3 * 1000 * 1000;
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
	
	public static void onTeslaRayImpact(EntityTeslaRay ray, EntityPlayer shootingEntity, double posX, double posY, double posZ) {
		if (!ray.worldObj.isRemote) {
			
		}
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		if (player.isSneaking() && IC2Access.instance.isKeyDown("Boost", player)) {
			if (ElectricItem.manager.canUse(itemStack, RAY_COST)) {
				if (!world.isRemote)
					ElectricItem.manager.use(itemStack, RAY_COST, player);
				EntityTeslaRay ray = new EntityTeslaRay(world, player);
				world.spawnEntityInWorld(ray);
			}
		}
		if (!world.isRemote) {
			if (player.getFoodStats().getFoodLevel() < 8) {
				if (ElectricItem.manager.canUse(itemStack, FOOD_COST)) {
					ElectricItem.manager.use(itemStack, FOOD_COST, player);
					player.getFoodStats().setFoodLevel(20);
					player.getFoodStats().setFoodSaturationLevel(10F);
				}
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

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			int layer) {
		return ClientProxy.ARMOR_LEVITATION_BOOTS_TEXTURE;
	}

	public static void addCraftingRecipe() {

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
