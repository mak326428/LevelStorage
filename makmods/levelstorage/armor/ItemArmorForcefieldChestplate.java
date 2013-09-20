package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.armor.ArmorFunctions.IForcefieldChestplate;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.CommonProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorForcefieldChestplate extends ItemArmor implements
        ISpecialArmor, IMetalArmor, IElectricItem, IForcefieldChestplate {

	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ARMOR_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 900;
	public static final int ENTITY_MAX_DISTANCE = 16;
	public static final int ENERGY_PER_TICK_ENTITIES = 100;

	public ItemArmorForcefieldChestplate(int id) {
		super(id, EnumArmorMaterial.DIAMOND,
				LevelStorage.proxy.getArmorIndexFor(CommonProxy.SUPERSONIC_DUMMY), 1);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ItemStack playerGetArmor(EntityPlayer p) {
		InventoryPlayer inv = p.inventory;
		ItemStack found = null;

		for (ItemStack st : inv.armorInventory) {
			if (st != null
			        && st.getItem() instanceof ItemArmorForcefieldChestplate)
				found = st;
		}

		return found;
	}

	public static final String FORCEFIELD_NBT = "forcefield";
	public static int OMISSION = 0;

	static {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "forcefieldChestplateRayOmission", 1);
		p.comment = "Determines the omission of Rays when rendering forcefield (set to 361 to completely remove the forcefield rendering)";
		OMISSION = p.getInt(1);
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
	        ItemStack itemStack) {
		ArmorFunctions.extinguish(player, world);
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableForcefieldChestplateCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			if (LevelStorage.recipesHardmode) {
				Recipes.advRecipes.addRecipe(new ItemStack(
				        LSBlockItemList.itemArmorForcefieldChestplate), "ttt",
				        "iqi", "lil", Character.valueOf('t'),
				        IC2Items.TESLA_COIL, Character.valueOf('i'),
				        SimpleItems.instance.getIngredient(3), Character
				                .valueOf('q'), IC2Items.QUANTUM_CHESTPLATE,
				        Character.valueOf('l'), new ItemStack(
				                LSBlockItemList.itemStorageFourMillion));
			} else {
				Recipes.advRecipes.addRecipe(new ItemStack(
				        LSBlockItemList.itemArmorForcefieldChestplate), "ttt",
				        "iqi", "lil", Character.valueOf('t'),
				        IC2Items.TESLA_COIL, Character.valueOf('i'),
				        IC2Items.IRIDIUM_PLATE, Character.valueOf('q'),
				        IC2Items.QUANTUM_CHESTPLATE, Character.valueOf('l'),
				        new ItemStack(LSBlockItemList.itemStorageFourMillion));
			}
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
		        .registerIcon(ClientProxy.FORCEFIELD_CHESTPLATE_TEXTURE);
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
		return 0.4D;
	}

	public double getDamageAbsorptionRatio() {
		return 1.1D;
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

	@Override
	public int energyPerTick() {
		return ENERGY_PER_TICK_ENTITIES;
	}

	@Override
	public WearType getWearType() {
		return WearType.ARMOR;
	}

}