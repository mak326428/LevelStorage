package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityInventoryProvider;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;

public class BlockInventoryProvider extends BlockContainer {

	public static final String UNLOCALIZED_NAME = "blockInventoryProvider";
	public static final String NAME = "Inventory Provider";

	public BlockInventoryProvider() {
		super(LevelStorage.configuration.getBlock(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(), Material.iron);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
		this.setBlockBounds(0F, 0F, 0F, 1F, 0.375F, 1F);
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"enableInventoryProviderCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			
		}
	}

	private Icon down;
	private Icon up;
	private Icon side;

	// You don't want the normal render type, or it wont render properly.
	@Override
	public int getRenderType() {
		return -1;
	}

	// It's not an opaque cube, so you need this.
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	// It's not a normal block, so you need this too.
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityInventoryProvider();
	}

}