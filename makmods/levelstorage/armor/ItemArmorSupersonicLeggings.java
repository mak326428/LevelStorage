package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.HashMap;
import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorSupersonicLeggings extends ItemArmor implements
		ISpecialArmor, IMetalArmor, IElectricItem {

	public static final String UNLOCALIZED_NAME = "armorSupersonicLeggings";
	public static final String NAME = "Supersonic Leggings";

	public static final int TIER = 3;
	public static final int STORAGE = 3 * 1000 * 1000;
	public static final int ENERGY_PER_DAMAGE = 900;

	public ItemArmorSupersonicLeggings() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(),
				EnumArmorMaterial.DIAMOND, 5, 2);
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
	}

	public static HashMap<EntityPlayer, Integer> speedTickerMap = new HashMap<EntityPlayer, Integer>();

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
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
				if (IC2Access.instance.isKeyDown("Jump", player))
					player.motionY += 0.1000000014901161D;
			}

			if (speed > 0.0F)
				player.moveFlying(0.0F, 1.0F, speed);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			int layer) {
		return ClientProxy.ARMOR_SUPERSONIC_LEGGINGS_TEXTURE;
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"enableSupersonicLeggingsCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
					ModItems.instance.itemSupersonicLeggings), "ggg", "iqi",
					"lll", Character.valueOf('g'), new ItemStack(
							Block.glowStone), Character.valueOf('i'),
					IC2Items.IRIDIUM_PLATE, Character.valueOf('q'),
					IC2Items.QUANTUM_LEGGINGS, Character.valueOf('l'),
					IC2Items.LAPOTRON_CRYSTAL);
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
				.registerIcon(ClientProxy.SUPERSONIC_LEGGINGS_TEXTURE);
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
		return 0.3D;
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
