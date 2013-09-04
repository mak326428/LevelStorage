package makmods.levelstorage.item;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFrequencyCard extends Item {

	public ItemFrequencyCard(int id) {
		super(id);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		// Frequency card
		ItemStack frequencyTr = Items.getItem("frequencyTransmitter");
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
		        LSBlockItemList.itemFreqCard), frequencyTr, new ItemStack(
		        Item.paper));
		// To get rid of card data
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(
		        LSBlockItemList.itemFreqCard), new ItemStack(
		        LSBlockItemList.itemFreqCard));
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTHelper.checkNBT(par1ItemStack);
		if (par1ItemStack.getTagCompound().hasKey(
		        BlockLocation.BLOCK_LOCATION_NBT)) {
			boolean isValid = isValid(par1ItemStack);
			BlockLocation location = BlockLocation.readFromNBT(par1ItemStack
			        .getTagCompound());
			par3List.add(StatCollector
			        .translateToLocal("tooltip.freqCard.location")
			        + " "
			        + location);
			par3List.add(StatCollector
			        .translateToLocal("tooltip.freqCard.isValid")
			        + " "
			        + (isValid ? StatCollector.translateToLocal("other.true")
			                : StatCollector.translateToLocal("other.false")));
		}
	}

	public static boolean isValid(ItemStack stack) {
		if (hasCardData(stack)) {
			BlockLocation loc = BlockLocation.readFromNBT(stack
			        .getTagCompound());
			if (BlockLocation.isDimIdValid(loc.getDimId())) {
				WorldServer w = DimensionManager.getWorld(loc.getDimId());

				if (w.getBlockId(loc.getX(), loc.getY(), loc.getZ()) == LSBlockItemList.blockWlessConductor.blockID)
					return true;

			}
		}
		return false;
	}

	/**
	 * just if you want to easier get rid of invalid cards
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (par1ItemStack != null) {
				if (par3EntityPlayer.isSneaking()) {
					NBTHelper.checkNBT(par1ItemStack);
					if (!isValid(par1ItemStack)) {
						par1ItemStack = new ItemStack(
						        LSBlockItemList.itemFreqCard);
						NBTHelper.checkNBT(par1ItemStack);
					}
				}
			}
		}
		return par1ItemStack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	        int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (!world.isRemote) {
			if (world.getBlockId(x, y, z) == LSBlockItemList.blockWlessConductor.blockID) {
				NBTHelper.checkNBT(stack);
				BlockLocation loc = new BlockLocation(
				        world.provider.dimensionId, x, y, z);
				BlockLocation.writeToNBT(stack.stackTagCompound, loc);
			}
		}
		return false;
	}

	public static boolean hasCardData(ItemStack stack) {
		NBTTagCompound cardNBT = stack.stackTagCompound;
		return cardNBT.hasKey(BlockLocation.BLOCK_LOCATION_NBT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.FREQUENCY_CARD_TEXTURE);
	}
}
