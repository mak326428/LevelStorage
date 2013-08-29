package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPortableTeleporter extends Item implements IElectricItem {

	public static final String COOLDOWN_NBT = "cooldown";

	public static final int COOLDOWN = 20;
	public static final int TIER = 3;
	public static final int STORAGE = 2000000;
	public static final int COOLDOWN_PERIOD = 20;
	public static final int ENERGY_PER_USE = 100000;

	public ItemPortableTeleporter(int id) {
		super(id);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
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
		        .registerIcon(ClientProxy.COMPACT_TELEPORTER_TEXTURE);
	}

	public static boolean hasDest(ItemStack stack) {
		return BlockLocation.readFromNBT(stack.stackTagCompound) != null;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (!hasDest(par1ItemStack) || par3EntityPlayer.isSneaking()) {
				BlockLocation location = new BlockLocation();
				location.setDimId(par2World.provider.dimensionId);
				location.setX((int) (Math.floor(par3EntityPlayer.posX)));
				location.setY((int) (Math.floor(par3EntityPlayer.posY + 1F)));
				location.setZ((int) (Math.floor(par3EntityPlayer.posZ)));
				BlockLocation.writeToNBT(par1ItemStack.stackTagCompound,
				        location);
				LevelStorage.proxy.messagePlayer(par3EntityPlayer,
				        "Teleport position set.", new Object[0]);
			} else {
				if (NBTHelper.getInteger(par1ItemStack, COOLDOWN_NBT) != 0)
					return par1ItemStack;
				NBTHelper.setInteger(par1ItemStack, COOLDOWN_NBT, COOLDOWN);
				if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
					ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
					        par3EntityPlayer);
				} else
					return par1ItemStack;

				LevelStorage.proxy.messagePlayer(par3EntityPlayer,
				        "Teleported.", new Object[0]);

				BlockLocation dest = BlockLocation
				        .readFromNBT(par1ItemStack.stackTagCompound);
				if (par3EntityPlayer.dimension != dest.getDimId()) {
					par3EntityPlayer.travelToDimension(dest.getDimId());
				}

				par3EntityPlayer.setPositionAndUpdate(dest.getX(),
				        dest.getY() + 1, dest.getZ());
			}
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
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		if (hasDest(par1ItemStack)) {
			BlockLocation loc = BlockLocation
			        .readFromNBT(par1ItemStack.stackTagCompound);
			par3List.add("Linked to: ");
			par3List.add("    Dimension: "
			        + DimensionManager.getProvider(loc.getDimId())
			                .getDimensionName());
			par3List.add("    X: " + loc.getX());
			par3List.add("    Y: " + loc.getY());
			par3List.add("    Z: " + loc.getZ());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes
		        .addShapelessRecipe(new ItemStack(
		                LSBlockItemList.itemCompactTeleporter), Items
		                .getItem("teleporter"), Items.getItem("teleporter"),
		                Items.getItem("frequencyTransmitter"), Items
		                        .getItem("frequencyTransmitter"), Items
		                        .getItem("lapotronCrystal"), Items
		                        .getItem("lapotronCrystal"), Items
		                        .getItem("lapotronCrystal"), Items
		                        .getItem("iridiumPlate"), Items
		                        .getItem("advancedCircuit"));

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
