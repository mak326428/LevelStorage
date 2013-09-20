package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRemoteAccessor extends Item implements IElectricItem {

	public static final int TIER = 3;
	public static final int STORAGE = 10000000;
	public static final int COOLDOWN_PERIOD = 20;
	public static int ENERGY_PER_USE;

	static {
		ENERGY_PER_USE = LevelStorage.configuration.get(
		        LevelStorage.BALANCE_CATEGORY, "remoteAccessorEnergyPerUse",
		        100000).getInt();
	}

	public ItemRemoteAccessor(int id) {
		super(id);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemRemoteAccessor), "ccc", "eae", "ppp",
		        Character.valueOf('p'), IC2Items.CARBON_PLATE.copy(), Character
		                .valueOf('c'), new ItemStack(
		                LSBlockItemList.itemStorageFourMillion), Character
		                .valueOf('a'), IC2Items.ADV_CIRCUIT, Character
		                .valueOf('e'), new ItemStack(Item.enderPearl));
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\2472Linked to:");
		NBTHelper.checkNBT(par1ItemStack);
		if (!par1ItemStack.getTagCompound().hasKey(
		        BlockLocation.BLOCK_LOCATION_NBT)) {
			par3List.add("\247cNowhere.");
		} else {
			par3List.add("\2472"
			        + BlockLocation.readFromNBT(par1ItemStack.stackTagCompound)
			                .toString());
		}
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	        int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (LevelStorage.isSimulating()) {
			if (!player.isSneaking()) {
				return true;
			}
			BlockLocation location = new BlockLocation();
			location.setDimId(world.provider.dimensionId);
			location.setX(x);
			location.setY(y);
			location.setZ(z);
			NBTHelper.checkNBT(stack);
			BlockLocation.writeToNBT(stack.stackTagCompound, location);
			LevelStorage.proxy.messagePlayer(player, "\2472Linked to: "
			        + location.toString(), new Object[0]);
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (LevelStorage.isSimulating()) {
			if (par3EntityPlayer.isSneaking())
				return par1ItemStack;
			NBTHelper.checkNBT(par1ItemStack);
			if (!par1ItemStack.getTagCompound().hasKey(
			        BlockLocation.BLOCK_LOCATION_NBT)) {
				LevelStorage.proxy
				        .messagePlayer(
				                par3EntityPlayer,
				                "\247cNot linked to anything. Press LSHIFT and right click the block to tune the device.",
				                new Object[0]);
				return par1ItemStack;
			} else {
				BlockLocation location = BlockLocation
				        .readFromNBT(par1ItemStack.getTagCompound());
				World w = DimensionManager.getWorld(location.getDimId());
				if (w == null) {
					LevelStorage.proxy
					        .messagePlayer(
					                par3EntityPlayer,
					                "\247cWorld this device is linked to is unloaded completely. You need to chunkload it.",
					                new Object[0]);
					return par1ItemStack;
				}
				if (w.isAirBlock(location.getX(), location.getY(),
				        location.getZ())) {
					LevelStorage.proxy.messagePlayer(par3EntityPlayer,
					        "\247cBlock you're trying to access doesn't exist",
					        new Object[0]);
				}
				Block b = Block.blocksList[w.getBlockId(location.getX(),
				        location.getY(), location.getZ())];
				if (b != null) {
					if (ElectricItem.manager.canUse(par1ItemStack,
					        ENERGY_PER_USE)) {
						ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
						        par3EntityPlayer);
						b.onBlockActivated(w, location.getX(), location.getY(),
						        location.getZ(), par3EntityPlayer,
						        ForgeDirection.UP.ordinal(), 0, 0, 0);
						return par1ItemStack;
					} else {
						LevelStorage.proxy.messagePlayer(par3EntityPlayer,
						        "\247cOut of energy.", new Object[0]);
						return par1ItemStack;
					}
				}
				LevelStorage.proxy.messagePlayer(par3EntityPlayer,
				        "\247cUnknown error.", new Object[0]);
			}

		}
		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			Cooldownable.onUpdate(par1ItemStack, COOLDOWN_PERIOD);
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
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
		return 20000;
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.REMOTE_ACESSOR_TEXTURE);
	}
}
