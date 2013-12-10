package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorAntimatterBase extends ItemArmor implements
		ISpecialArmor, IElectricItem {

	public static int RENDER_ID;
	public static final int HELMET = 0;
	public static final int CHESTPLATE = 1;
	public static final int LEGGINGS = 2;
	public static final int BOOTS = 3;
	public static final int ENERGY_PER_DAMAGE = 1000;
	public static final int STORAGE = 120000000;

	public ItemArmorAntimatterBase(int id, int armorType) {
		super(id, EnumArmorMaterial.DIAMOND, RENDER_ID, armorType);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}
	
	public static final int EU_PER_TELEPORT = 300000;
	public static final int EU_PER_TICK_WATERWALK = 100;
	

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		if (this.armorType == CHESTPLATE)
			ArmorFunctions.extinguish(player, world);
		else if (this.armorType == BOOTS) {
			ArmorFunctions.jumpBooster(world, player, itemStack);
			ArmorFunctions.fly(ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK,
					player, itemStack, world);
			ArmorFunctions.walkWater(world, player, itemStack);
		} else if (this.armorType == LEGGINGS) {
			ArmorFunctions.speedUp(player, itemStack);
			ArmorFunctions.antimatterLeggingsFunctions(world, player, itemStack);
		}
		else if (this.armorType == HELMET) {
			ArmorFunctions.helmetFunctions(world, player, itemStack,
					ItemArmorTeslaHelmet.RAY_COST,
					ItemArmorTeslaHelmet.FOOD_COST);
		}
	}

	public double getDamageAbsorptionRatio() {
		if (this.armorType == CHESTPLATE)
			return 1.1D;
		return 1.0D;
	}

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		double absorptionRatio = getBaseAbsorptionRatio()
				* getDamageAbsorptionRatio();
		int energyPerDamage = ENERGY_PER_DAMAGE;

		int damageLimit = energyPerDamage > 0 ? 25
				* ElectricItem.manager.getCharge(armor) / energyPerDamage : 0;

		return new ISpecialArmor.ArmorProperties(0, absorptionRatio,
				damageLimit);
	}

	private double getBaseAbsorptionRatio() {
		switch (this.armorType) {
		case 0:
			return 0.15D;
		case 1:
			return 0.4D;
		case 2:
			return 0.3D;
		case 3:
			return 0.15D;
		}
		return 0.0D;
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		if (this.armorType == HELMET)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_HELMET_TEXTURE);
		else if (this.armorType == CHESTPLATE)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_CHESTPLATE_TEXTURE);
		else if (this.armorType == LEGGINGS)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_LEGGINGS_TEXTURE);
		else if (this.armorType == BOOTS)
			this.itemIcon = par1IconRegister
					.registerIcon(ClientProxy.ANTIMATTER_BOOTS_TEXTURE);

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
		if (this.armorType == CHESTPLATE)
			return true;
		else
			return false;
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
		return 4;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 1000000;
	}

}
