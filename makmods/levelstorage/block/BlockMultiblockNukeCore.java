package makmods.levelstorage.block;

import ic2.api.item.Items;

import java.util.Random;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMultiblockNukeCore extends BlockContainer {

	public static final String UNLOCALIZED_NAME = "blockMultinukeCore";
	public static final String NAME = "Multiblock Nuke Core";

	public BlockMultiblockNukeCore() {
		super(LevelStorage.configuration.getBlock(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(), Material.iron);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}
	
	public static void addCraftingRecipe() {
		
	}
	
	private Icon down;
	private Icon up;
	private Icon side;

	@Override
	public TileEntity createNewTileEntity(World worldObj) {
		return new TileEntityXpCharger();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		ForgeDirection orientation = ForgeDirection.VALID_DIRECTIONS[side];
		if (orientation == ForgeDirection.DOWN)
			return this.down;
		if (orientation == ForgeDirection.UP)
			return this.up;
		if (orientation == ForgeDirection.NORTH
				|| orientation == ForgeDirection.WEST
				|| orientation == ForgeDirection.SOUTH
				|| orientation == ForgeDirection.EAST)
			return this.side;
		return null;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.side = iconRegister
				.registerIcon(ClientProxy.MULTINUKE_CORE_TEXTURE+ "Side");
		this.up = iconRegister
				.registerIcon(ClientProxy.MULTINUKE_CORE_TEXTURE + "Up");
		this.down = iconRegister
				.registerIcon(ClientProxy.MULTINUKE_CORE_TEXTURE + "Down");
	}
}