package makmods.levelstorage.block;

import ic2.api.item.Items;

import java.util.ArrayList;
import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockMachineStandart extends BlockContainer {

	public BlockMachineStandart(int par1) {
		super(par1, Material.iron);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);
	}

	private Icon[] icons = new Icon[6];

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return CommonHelper.handleMachineRightclick(world, x, y, z, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int par2) {
		return icons[side];
	}

	/*
	 * public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,
	 * int z, int metadata, int fortune) { ArrayList<ItemStack> dropped =
	 * Lists.newArrayList();
	 * dropped.add(Items.getItem("advancedMachine").copy()); TileEntity
	 * tileEntity = world.getBlockTileEntity(x, y, z); if (!(tileEntity
	 * instanceof IInventory)) return dropped; IInventory inventory =
	 * (IInventory) tileEntity;
	 * 
	 * for (int i = 0; i < inventory.getSizeInventory(); i++) { ItemStack item =
	 * inventory.getStackInSlot(i);
	 * 
	 * if (item != null && item.stackSize > 0) { dropped.add(item.copy());
	 * item.stackSize = 0; } } return dropped; }
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		this.dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory))
			return;
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z
						+ rz, new ItemStack(item.itemID, item.stackSize,
						item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound(
							(NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String blockName = this.getUnlocalizedName().replace("tile.", "");
		for (int i = 0; i < 6; i++) {
			String sname = ForgeDirection.VALID_DIRECTIONS[i].name()
					.toLowerCase();
			icons[i] = iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(blockName + "/" + sname));
		}
	}

}
