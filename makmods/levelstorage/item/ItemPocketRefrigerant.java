package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPocketRefrigerant extends Item implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "itemPocketRefrigerant";
	public static final String NAME = "Pocket Refrigerant";

	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_USE = 500;

	public static final int TIER = 1;

	public ItemPocketRefrigerant(int id) {
		super(id);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes
		        .addShapelessRecipe(new ItemStack(
		                LSBlockItemList.itemPocketRefrigerant), new ItemStack(
		                Block.ice), new ItemStack(Block.ice), new ItemStack(
		                Block.ice), new ItemStack(Block.ice), Items
		                .getItem("advancedCircuit"), Items.getItem("reBattery"));
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
		        LSBlockItemList.itemPocketRefrigerant),
		        new ItemStack(Block.ice), new ItemStack(Block.ice),
		        new ItemStack(Block.ice), new ItemStack(Block.ice), Items
		                .getItem("advancedCircuit"), Items
		                .getItem("chargedReBattery"));

	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {
		if (par3Entity.isBurning()) {
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
				        (EntityLivingBase) par3Entity);
				par3Entity.extinguish();
			}
		}
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
		        .registerIcon(ClientProxy.POCKET_REFRIGERANT_TEXTURE);
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
