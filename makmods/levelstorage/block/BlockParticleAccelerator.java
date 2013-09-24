package makmods.levelstorage.block;

import ic2.api.item.Items;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.logic.util.Helper;
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

	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityParticleAccelerator();
	}

}