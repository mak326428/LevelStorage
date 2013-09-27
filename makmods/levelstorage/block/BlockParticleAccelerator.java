package makmods.levelstorage.block;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.BlockTextureRegistry;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
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

public class BlockParticleAccelerator extends BlockMachineStandart {

	public BlockParticleAccelerator(int id) {
		super(id);
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.blockParticleAccelerator), "isi", "crc", "mpm",
				Character.valueOf('i'), IC2Items.IRIDIUM_PLATE.copy(),
				Character.valueOf('c'), IC2Items.ADV_CIRCUIT, Character
						.valueOf('m'), IC2Items.ADV_MACHINE, Character
						.valueOf('s'), Items.getItem("scanner"), Character
						.valueOf('p'), Items.getItem("patternstorage"),
				Character.valueOf('r'), Items.getItem("replicator"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityParticleAccelerator();
	}

}