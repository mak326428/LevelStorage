package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.BlockTextureRegistry;
import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
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

public class BlockLavaFabricator extends BlockMachineStandart implements IHasRecipe {

	public BlockLavaFabricator(int id) {
		super(id);
	}

	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockLavaFabricator), "aca", "pgp", Character
				.valueOf('a'), IC2Items.ADV_CIRCUIT, Character.valueOf('p'),
				IC2Items.ADV_ALLOY, Character.valueOf('c'),
				IC2Items.ADV_MACHINE, Character.valueOf('g'), Items
						.getItem("geothermalGenerator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLavaFabricator();
	}
}
