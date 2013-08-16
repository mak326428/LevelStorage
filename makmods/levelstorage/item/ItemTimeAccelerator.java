package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.NBTHelper;
import makmods.levelstorage.logic.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTimeAccelerator extends Item implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "itemTimeAccelerator";
	public static final String NAME = "Time Accelerator";

	public static final int STORAGE = 4000000;
	public static final int ENERGY_PER_USE = 5000;
	public static final String NBT_ON = "on";

	public static final int TURN_ON_OFF_COOLDOWN = 10;
	public static final int TICKS_SKIP = 15;

	public static final int TIER = 3;

	public ItemTimeAccelerator() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
		        LevelStorage.getAndIncrementCurrId()).getInt());
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableTimeAcceleratorCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        ModItems.instance.itemTimeAccelerator), "iii", "ses",
			        "iii", Character.valueOf('i'), IC2Items.IRIDIUM_PLATE
			                .copy(), Character.valueOf('e'), new ItemStack(
			                ModItems.instance.itemStorageFourMillion).copy(),
			        Character.valueOf('s'), new ItemStack(
			                ModItems.instance.itemSuperconductor).copy());
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (Cooldownable.use(par1ItemStack, TURN_ON_OFF_COOLDOWN)) {
				NBTHelper.setBoolean(par1ItemStack, NBT_ON,
				        !NBTHelper.getBoolean(par1ItemStack, NBT_ON));
				LevelStorage.proxy
				        .messagePlayer(
				                par3EntityPlayer,
				                "Active: "
				                        + (NBTHelper.getBoolean(par1ItemStack,
				                                NBT_ON) ? "yes" : "no"),
				                new Object[0]);
			}
		}
		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {

		if (par3Entity instanceof EntityLivingBase) {
			if (!par2World.isRemote)
				Cooldownable.onUpdate(par1ItemStack, TURN_ON_OFF_COOLDOWN);
			if (NBTHelper.getBoolean(par1ItemStack, NBT_ON)) {
				if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
					if (!par2World.isRemote)
						ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
						        (EntityLivingBase) par3Entity);
					for (int i = 0; i < TICKS_SKIP; i++) {
						par2World.tick();
					}
				}
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
		        .registerIcon(ClientProxy.TIME_ACCELERATOR_TEXTURE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.epic;
	}

	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\"I want to tell time what time it is.\"");
		par3List.add("    - direwolf20, SSP Season 3");
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
