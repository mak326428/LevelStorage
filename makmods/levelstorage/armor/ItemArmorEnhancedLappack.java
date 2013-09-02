package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorEnhancedLappack extends ItemArmor implements
        ISpecialArmor, IMetalArmor, IElectricItem {
	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ENH_LAPPACK_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 900;

	public ItemArmorEnhancedLappack(int id) {
		/*
		 * Possible fix:
		 * add a dummy in commonproxy
		 * and if a side is not client
		 * return 0
		 */
		super(id, EnumArmorMaterial.DIAMOND,
		        LevelStorage.proxy.getArmorIndexFor(CommonProxy.ENH_LAPPACK_DUMMY), 1);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableEnhancedLappackCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        LSBlockItemList.itemEnhLappack), "ccc", "lal", "apa",
			        Character.valueOf('l'), IC2Items.LAPOTRON_CRYSTAL,
			        Character.valueOf('a'), IC2Items.ADV_CIRCUIT, Character
			                .valueOf('p'), Items.getItem("lapPack"), Character
			                .valueOf('c'), IC2Items.CARBON_PLATE);
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ENHANCED_LAPPACK_TEXTURE);
	}

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
	        ItemStack armor, DamageSource source, double damage, int slot) {
		return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);
	}

	private double getBaseAbsorptionRatio() {
		return 0.0D;
	}

	public double getDamageAbsorptionRatio() {
		return 0.0D;
	}

	public void damageArmor(EntityLivingBase entity, ItemStack stack,
	        DamageSource source, int damage, int slot) {

	}

	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 0;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return true;
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
		return 3000;
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