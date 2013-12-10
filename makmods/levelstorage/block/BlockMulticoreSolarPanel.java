package makmods.levelstorage.block;

import ic2.api.recipe.Recipes;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
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

public class BlockMulticoreSolarPanel extends BlockMachineStandart implements IHasRecipe {

	public BlockMulticoreSolarPanel(int id) {
		super(id);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}

	public void addCraftingRecipe() {
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
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityMulticoreSolarPanel();
	}

}