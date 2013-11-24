package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDemolisher extends ItemPickaxe implements IElectricItem,
		IHasRecipe {

	public static final int EU_PER_BLOCK = 2000;
	public static final int TIER = 4;
	public static final int STORAGE = 40000000;
	public static final int TRANSFER_LIMIT = 1000000;

	public ItemDemolisher(int id) {
		super(id, EnumToolMaterial.EMERALD);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient())
			this.setCreativeTab(LSCreativeTab.instance);
		this.setMaxStackSize(1);
		MinecraftForge.setToolClass(this, "pickaxe", 5);
		MinecraftForge.setToolClass(this, "shovel", 2);
		MinecraftForge.setToolClass(this, "axe", 4);
		this.damageVsEntity = 2.0f;
		// MinecraftForge.setToolClass(this, "shovel", 3);
		this.efficiencyOnProperMaterial = 10000;
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemDemolisher), "iei", "idi", "ici", 'i',
				IC2Items.IRIDIUM_PLATE.copy(), 'd', new ItemStack(
						LSBlockItemList.itemEnhDiamondDrill), 'c',
				new ItemStack(LSBlockItemList.itemAtomicDisassembler), 'e',
				new ItemStack(LSBlockItemList.itemStorageFourtyMillion));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.DEMOLISHER_TEXTURE);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block, int meta) {
		return ElectricItem.manager.canUse(stack, EU_PER_BLOCK) ? 10000 : 1.0F;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World,
	        int par3, int par4, int par5, int par6,
	        EntityLivingBase par7EntityLivingBase) {
		if (!par2World.isRemote) {
			if (ElectricItem.manager.canUse(par1ItemStack, EU_PER_BLOCK))
				return ElectricItem.manager.use(par1ItemStack, EU_PER_BLOCK, par7EntityLivingBase);
		}
		return false;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return itemID;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.epic;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return itemID;
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
	public int getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return TRANSFER_LIMIT;
	}

}
