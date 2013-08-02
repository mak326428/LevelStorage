package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.NBTHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTeslaWeatherStaff extends Item implements IElectricItem {

	public enum Mode {
		TOGGLE_RAIN, SUMMON_LIGHTNING
	}

	public static final String UNLOCALIZED_NAME = "teslaStaffWeather";
	public static final String NAME = "Tesla's weather staff";
	public static final int ENERGY_PER_USE = 5000000;
	public static final String COOLDOWN_NBT = "cooldown";
	public static final int COOLDOWN = 40;
	public static final int TIER = 3;
	public static final int STORAGE = 10000000;

	public static void addCraftingRecipe() {

	}

	public ItemTeslaWeatherStaff() {
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

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
				true, false);
		par3List.add(var4);
		par3List.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	// IElectricItem stuff

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
		return 100000;
	}

	public static final String NBT_MODE = "mode";

	public void saveModeToNBT(ItemStack stack, Mode mode) {
		if (stack == null)
			return;
		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();

		NBTTagCompound nbt = stack.stackTagCompound;

		switch (mode) {
		case SUMMON_LIGHTNING:
			NBTHelper.setInteger(stack, NBT_MODE, 0);
			break;
		case TOGGLE_RAIN:
			NBTHelper.setInteger(stack, NBT_MODE, 1);
			break;
		}

		stack.stackTagCompound = nbt;
	}

	public static Mode getModeForStack(ItemStack stack) {
		if (stack == null)
			return null;
		if (stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
		
		int mode = NBTHelper.getInteger(stack, NBT_MODE);
		switch (mode) {
		case 0:
			return Mode.SUMMON_LIGHTNING;
		case 1:
			return Mode.TOGGLE_RAIN;
		}
		
		return null;
	}
	
	
	
	public static void changeMode(ItemStack stack) {
		int noExcLength = Mode.values().length;
		int currIndex = Integer.MIN_VALUE;
		for (int i = 0; i < Mode.values().length; i++) {
			if ((Mode.values()[i]) == getModeForStack(stack))
				currIndex = i;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			
			if (NBTHelper.getInteger(par1ItemStack, COOLDOWN_NBT) != 0)
				return par1ItemStack;
			NBTHelper.setInteger(par1ItemStack, COOLDOWN_NBT, COOLDOWN);
			if (par3EntityPlayer.isSneaking()) {
				changeMode(par1ItemStack);
				return par1ItemStack;
			}
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
						par3EntityPlayer);
			} else
				return par1ItemStack;
			
			
		}

		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			NBTHelper.decreaseInteger(par1ItemStack, COOLDOWN_NBT, 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.TESLA_STAFF_TEXTURE);
	}

}
