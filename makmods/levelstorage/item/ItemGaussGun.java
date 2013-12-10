package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.EntityUtil;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGaussGun extends Item implements IElectricItem, IHasRecipe {
	public static final int TIER = 3;
	public static final int STORAGE = 1 * 1000 * 1000;

	public static Map<ItemStack, Integer> itemDamage = Maps.newHashMap();

	public static String NBT_AMMO_TYPE = "ammo_type";
	public static String NBT_AMMO_AMOUNT = "ammoCount";

	static {
		addDamage(new ItemStack(Item.goldNugget), 15);
		addDamage("nuggetLead", 19);
		addDamage("nuggetIron", 8);
		addDamage(Items.getItem("smallUran235"), 1);
	}

	public static void addDamage(ItemStack is, int dmg) {
		itemDamage.put(is, dmg);
	}

	public static void addDamage(String odName, int dmg) {
		for (ItemStack is : OreDictionary.getOres(odName))
			addDamage(is, dmg);
	}

	public static int getAmmoDamage(ItemStack is) {
		for (Entry<ItemStack, Integer> iss : itemDamage.entrySet()) {
			if (CommonHelper.areStacksEqual(iss.getKey(), is))
				return iss.getValue();
		}
		return 0;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		if (world.isRemote)
			return itemStack;
		if (!ElectricItem.manager.canUse(itemStack, 30000))
			return itemStack;
		Entity target = EntityUtil.getTarget(world, player, 128);
		if (target == null)
			return itemStack;
		int stored = itemStack.getTagCompound().getInteger(NBT_AMMO_AMOUNT);
		if (stored > 0) {
			ItemStack type = ItemStack.loadItemStackFromNBT(itemStack
					.getTagCompound().getCompoundTag(NBT_AMMO_TYPE));
			type.getItem().onUpdate(type, world, target, 0, false);
			target.attackEntityFrom(DamageSource.causePlayerDamage(player)
					.setProjectile(), getAmmoDamage(type));
			ElectricItem.manager.use(itemStack, 30000, player);
			stored -= 1;
			if (stored == 0)
				itemStack.stackTagCompound.removeTag(NBT_AMMO_TYPE);
			itemStack.stackTagCompound.setInteger(NBT_AMMO_AMOUNT, stored);
			
		} else
			itemStack.stackTagCompound.setInteger(NBT_AMMO_AMOUNT, 0);
		return itemStack;
	}

	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if (par1ItemStack.stackTagCompound == null)
			return;
		int stored = par1ItemStack.getTagCompound().getInteger(NBT_AMMO_AMOUNT);
		ItemStack type = ItemStack.loadItemStackFromNBT(par1ItemStack
				.getTagCompound().getCompoundTag(NBT_AMMO_TYPE));
		par3List.add(stored > 0 ? String.format("Ammo: %d of %s", stored,
				CommonHelper.getStackNameNoStackSize(type)) : "Ammo: none.");
	}

	public ItemGaussGun(int id) {
		super(id);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public void addCraftingRecipe() {
		CraftingManager.getInstance().getRecipeList()
				.add(new GaussGunRechargeRecipe());
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
		return 100000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.GAUSS_GUN_TEXTURE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
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
