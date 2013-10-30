package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Field;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.logic.util.SimpleMode;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElectricMagnet extends Item implements IElectricItem, IHasRecipe {

	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_TICK = 10;
	public static final int TURN_ON_OFF_COOLDOWN = 10;
	public static final double RANGE = 32;

	public ItemElectricMagnet(int id) {
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
		return 1000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ELECTRIC_MAGNET_TEXTURE);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		// par3List.add("\2472"
		// + StatCollector.translateToLocal("tooltip.electricMagnet"));
		par3List.add(getStringState(par1ItemStack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.uncommon;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			Cooldownable.onUpdate(par1ItemStack, TURN_ON_OFF_COOLDOWN);
			writeInitialMode(par1ItemStack);

			if (SimpleMode.readFromNBT(par1ItemStack.stackTagCompound).boolValue) {
				if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_TICK)) {
					boolean used = false;
					for (Object obj : par2World.loadedEntityList) {
						if (obj instanceof EntityItem
								|| obj instanceof EntityXPOrb) {
							Entity item = (Entity) obj;
							double distanceX = Math.abs(par3Entity.posX
									- item.posX);
							double distanceY = Math.abs(par3Entity.posY
									- item.posY);
							double distanceZ = Math.abs(par3Entity.posZ
									- item.posZ);
							double distanceTotal = distanceX + distanceY
									+ distanceZ;
							if (distanceTotal < RANGE) {
								used = true;
								item.setPosition(par3Entity.posX,
										par3Entity.posY, par3Entity.posZ);
							}
						}
					}
					if (par3Entity instanceof EntityLivingBase)
						if (used)
							ElectricItem.manager.use(par1ItemStack,
									ENERGY_PER_TICK,
									(EntityLivingBase) par3Entity);
				}
			}
		}
	}

	public static void writeInitialMode(ItemStack stack) {
		NBTHelper.checkNBT(stack);
		if (!stack.stackTagCompound.hasKey(SimpleMode.NBT_COMPOUND_NAME)) {
			SimpleMode.OFF.writeToNBT(stack.stackTagCompound);
		}
	}

	public String getStringState(ItemStack par1ItemStack) {
		return "Active: "
				+ (SimpleMode.readFromNBT(par1ItemStack.stackTagCompound).boolValue ? EnumChatFormatting.DARK_GREEN
						+ "yes"
						: EnumChatFormatting.DARK_RED + "no");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			writeInitialMode(par1ItemStack);
			SimpleMode.readFromNBT(par1ItemStack.stackTagCompound).getReverse()
					.writeToNBT(par1ItemStack.stackTagCompound);
			LevelStorage.proxy.messagePlayer(par3EntityPlayer,
					getStringState(par1ItemStack), new Object[0]);
		}
		return par1ItemStack;
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemElectricMagnet), "cc ", "cic", " cb",
				Character.valueOf('c'), "plateCopper", Character.valueOf('i'),
				"plateIron", Character.valueOf('b'), Items
						.getItem("powerunitsmall"));

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
