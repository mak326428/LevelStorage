package makmods.levelstorage.item;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.block.BlockCableSuperconductor;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSuperconductor extends Item {

	public static final String UNLOCALIZED_NAME = "itemSuperconductor";
	public static final String NAME = "Superconductor Cable";

	public ItemSuperconductor(int id) {
		super(id);

		this.setNoRepair();
		this.setMaxStackSize(16);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		Property pEnable = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "superConductorOreDictCompatible", true);
		pEnable.comment = "Determines whether or not LevelStorage's superconductors are oredict-compatible with other superconductors (e.g. GraviSuite or GregTech)";
		if (pEnable.getBoolean(true))
			OreDictionary.registerOre("itemSuperconductor", this);
	}

	public static void addCraftingRecipe() {
		Property pOutput = LevelStorage.configuration
		        .get(Configuration.CATEGORY_GENERAL,
		                "superConductorRecipeOutput", 6);
		pOutput.comment = "Determines how much superconductors you get from one recipe";

		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemSuperconductor, pOutput.getInt(6)), "ccc",
		        "iai", "ccc", Character.valueOf('c'), Items
		                .getItem("glassFiberCableItem"),
		        Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
		                .valueOf('a'), Items.getItem("advancedMachine"));

	}

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
	        World world, int x, int y, int z, int side, float a, float b,
	        float c) {
		int blockId = world.getBlockId(x, y, z);

		if (blockId > 0) {
			if (blockId == Block.snow.blockID)
				side = 1;
			else if ((blockId != Block.vine.blockID)
			        && (blockId != Block.tallGrass.blockID)
			        && (blockId != Block.deadBush.blockID)
			        && ((Block.blocksList[blockId] == null) || (!Block.blocksList[blockId]
			                .isBlockReplaceable(world, x, y, z)))) {
				switch (side) {
					case 0:
						y--;
						break;
					case 1:
						y++;
						break;
					case 2:
						z--;
						break;
					case 3:
						z++;
						break;
					case 4:
						x--;
						break;
					case 5:
						x++;
				}
			}

		}

		BlockCableSuperconductor block = (BlockCableSuperconductor) Block.blocksList[LSBlockItemList.blockSuperconductor.blockID];

		if (((blockId == 0) || (world.canPlaceEntityOnSide(
		        LSBlockItemList.blockSuperconductor.blockID, x, y, z, false,
		        side, entityplayer, itemstack)))
		        && (world.checkNoEntityCollision(block
		                .getCollisionBoundingBoxFromPool(world, x, y, z)))
		        && (world.setBlock(x, y, z, block.blockID,
		                itemstack.getItemDamage(), 3))) {
			block.onPostBlockPlaced(world, x, y, z, side);
			block.onBlockPlacedBy(world, x, y, z, entityplayer, itemstack);
			if (!entityplayer.capabilities.isCreativeMode)
				itemstack.stackSize -= 1;

			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ITEM_SUPERCONDUCTOR_TEXTURE);
	}
}
