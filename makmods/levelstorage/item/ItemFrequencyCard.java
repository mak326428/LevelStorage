package makmods.levelstorage.item;

import java.util.List;

import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.block.BlockWirelessConductor;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFrequencyCard extends Item {

	public static final String NBT_WAS_USED = "wasUsed";
	public static final String NBT_DIM_ID = "dimId";
	public static final String NBT_X_POS = "xPos";
	public static final String NBT_Y_POS = "yPos";
	public static final String NBT_Z_POS = "zPos";

	public ItemFrequencyCard(int par1) {
		super(par1);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		this.setMaxStackSize(1);
		this.setUnlocalizedName("item.freqCard");
	}

	public static boolean isDimIdValid(int idToCheck) {
		Integer[] ids = DimensionManager.getIDs();
		for (int id : ids) {
			if (id == idToCheck)
				return true;
		}
		return false;
	}

	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		verifyStack(par1ItemStack);
		if (par1ItemStack.stackTagCompound.getBoolean(NBT_WAS_USED)) {
			par3List.add("Location: "
					+ par1ItemStack.stackTagCompound.getInteger(NBT_DIM_ID)
					+ ";"
					+ par1ItemStack.stackTagCompound.getInteger(NBT_X_POS)
					+ ":"
					+ par1ItemStack.stackTagCompound.getInteger(NBT_Y_POS)
					+ ":"
					+ par1ItemStack.stackTagCompound.getInteger(NBT_Z_POS));
			par3List.add("Is valid: " + (isValid(par1ItemStack) ? "yes" : "no"));
		}
	}

	public static boolean isValid(ItemStack stack) {
		NBTTagCompound cardNBT = stack.getTagCompound();

		if (hasCardData(stack)) {
			if (isDimIdValid(cardNBT.getInteger(NBT_DIM_ID))) {
				WorldServer w = DimensionManager.getWorld(cardNBT
						.getInteger(NBT_DIM_ID));
				int x = cardNBT.getInteger(NBT_X_POS);
				int y = cardNBT.getInteger(NBT_Y_POS);
				int z = cardNBT.getInteger(NBT_Z_POS);

				if (w.getBlockId(x, y, z) == ModBlocks.instance.blockWlessConductor.blockID) {
					return true;
				}

			}
		}
		return false;
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (!world.isRemote) {
			if (world.getBlockId(x, y, z) == ModBlocks.instance.blockWlessConductor.blockID) {
				verifyStack(stack);
				if (!stack.stackTagCompound.getBoolean(NBT_WAS_USED)) {
					// System.out.println("Setting frequency card up");
					stack.stackTagCompound.setInteger(NBT_DIM_ID,
							world.provider.dimensionId);
					stack.stackTagCompound.setInteger(NBT_X_POS, x);
					stack.stackTagCompound.setInteger(NBT_Y_POS, y);
					stack.stackTagCompound.setInteger(NBT_Z_POS, z);
					stack.stackTagCompound.setBoolean(NBT_WAS_USED, true);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasCardData(ItemStack stack) {
		NBTTagCompound cardNBT = stack.stackTagCompound;
		return (cardNBT.getBoolean(NBT_WAS_USED)) && (cardNBT.hasKey(NBT_DIM_ID) && cardNBT.hasKey(NBT_X_POS)
				&& cardNBT.hasKey(NBT_Y_POS) && cardNBT.hasKey(NBT_Z_POS));
	}

	public static void verifyStack(ItemStack stack) {
		// Just in case... Whatever!
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
			if (!stack.stackTagCompound.hasKey(NBT_WAS_USED)) {
				stack.stackTagCompound.setBoolean(NBT_WAS_USED, false);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.FREQUENCY_CARD_TEXTURE);
	}
}
