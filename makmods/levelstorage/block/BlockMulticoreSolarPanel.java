package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.Helper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.BlockTextureRegistry;
import makmods.levelstorage.tileentity.TileEntityMulticoreSolarPanel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMulticoreSolarPanel extends BlockContainer {

	public BlockMulticoreSolarPanel(int id) {
		super(id, Material.iron);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}

	public static void addCraftingRecipe() {
		// TODO: add recipe
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return LSBlockItemList.blockMulticoreSolarPanel.blockID;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Helper.dropBlockItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
	        EntityPlayer player, int par6, float par7, float par8, float par9) {
		return Helper.handleMachineRightclick(world, x, y, z, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		return BlockTextureRegistry.instance.getIcon(side,
		        ClientProxy.MULTICORE_SOLAR_PANEL_TEXTURE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		BlockTextureRegistry.instance.registerIcons(iconRegister,
		        ClientProxy.MULTICORE_SOLAR_PANEL_TEXTURE);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityMulticoreSolarPanel();
	}

}