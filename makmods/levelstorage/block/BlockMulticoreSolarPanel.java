package makmods.levelstorage.block;

import ic2.api.recipe.Recipes;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.BlockTextureRegistry;
import makmods.levelstorage.tileentity.TileEntityMulticoreSolarPanel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
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
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}

	public static void addCraftingRecipe() {
		if (!LevelStorage.isAnySolarModLoaded()) {
			LogHelper
					.warning("No solar mods loaded. Not adding Multicore Solar Panel's recipe");
			return;
		}
		try {
			Recipes.advRecipes
					.addRecipe(new ItemStack(
							LSBlockItemList.blockMulticoreSolarPanel), "csc",
							"sns", "csc", Character.valueOf('c'),
							IC2Items.CARBON_PLATE.copy(), Character
									.valueOf('n'), "itemEnergizedStar",
							Character.valueOf('s'), "solarPanelHV");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			Recipes.advRecipes
					.addRecipe(new ItemStack(
							LSBlockItemList.blockMulticoreSolarPanel), "csc",
							"sns", "csc", Character.valueOf('c'),
							IC2Items.CARBON_PLATE.copy(), Character
									.valueOf('n'), "itemEnergizedStar",
							Character.valueOf('s'), "craftingSolarPanelHV");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return LSBlockItemList.blockMulticoreSolarPanel.blockID;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		CommonHelper.dropBlockItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return CommonHelper.handleMachineRightclick(world, x, y, z, player);
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