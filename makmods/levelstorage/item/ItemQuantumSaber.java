package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemQuantumSaber extends Item implements IElectricItem {

	public static final int TIER = 3;
	public static final int DAMAGE = 30;
	public static final int STORAGE = 1000000;
	public static final int ENERGY_PER_USE = 1000;

	public ItemQuantumSaber(int id) {
		super(id);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.QUANTUM_SABER_TEXTURE);
	}

	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase mob,
	        EntityLivingBase player) {
		if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
			ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE, player);
			if (player instanceof EntityPlayer) {
				mob.attackEntityFrom(
				        DamageSource.causePlayerDamage((EntityPlayer) player),
				        DAMAGE);
			}
		}
		return true;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemQuantumSaber), "ai ", "ai ", "cnl",
		        Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
		                .valueOf('a'), IC2Items.ADV_ALLOY, Character
		                .valueOf('c'), IC2Items.ADV_CIRCUIT, Character
		                .valueOf('l'), IC2Items.LAPOTRON_CRYSTAL, Character
		                .valueOf('n'), Items.getItem("nanoSaber"));

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
