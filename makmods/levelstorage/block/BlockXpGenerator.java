package makmods.levelstorage.block;

import ic2.api.item.Items;

import java.util.Random;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockXpGenerator extends BlockContainer {
	public BlockXpGenerator(int id) {
		super(id, Material.iron);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setUnlocalizedName("blockXpGenerator");
		this.setStepSound(Block.soundMetalFootstep);
		this.setHardness(3.0F);

	}

	private Icon down;
	private Icon up;
	private Icon side;

	@Override
	public TileEntity createNewTileEntity(World worldObj) {
		return new TileEntityXpGenerator();
	}

	public ItemStack advMachine = Items.getItem("advancedMachine");

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return this.advMachine.itemID;
	}

	@Override
	public int damageDropped(int par1) {
		return this.advMachine.getItemDamage();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {

		if (player.isSneaking())
			return false;
		else {
			if (!world.isRemote) {
				TileEntityXpGenerator tileXpGen = (TileEntityXpGenerator) world
						.getBlockTileEntity(x, y, z);
				if (tileXpGen != null) {
					player.openGui(LevelStorage.instance, 50, world, x, y, z);
				}
			}

			return true;
		}
		// return false;
	}

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

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public Icon getBlockTexture(IBlockAccess world,
	 * int x, int y, int z, int side) { ForgeDirection orientation =
	 * ForgeDirection.VALID_DIRECTIONS[side]; TileEntity tile =
	 * world.getBlockTileEntity(x, y, z); if (tile instanceof
	 * TileEntityXpGenerator) { if (orientation == ForgeDirection.DOWN) { return
	 * down; } if (orientation == ForgeDirection.UP) { return up; } if
	 * (orientation == ForgeDirection.NORTH || orientation ==
	 * ForgeDirection.WEST || orientation == ForgeDirection.SOUTH || orientation
	 * == ForgeDirection.EAST) { if (((TileEntityXpGenerator) tile).isWorking) {
	 * return side_processing; } else { return side_idle; }
	 * 
	 * }
	 * 
	 * } return null; }
	 */

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
		this.side = iconRegister.registerIcon(ClientProxy.XP_GEN_TEXTURE
				+ "Side");
		this.up = iconRegister.registerIcon(ClientProxy.XP_GEN_TEXTURE + "Up");
		this.down = iconRegister.registerIcon(ClientProxy.XP_GEN_TEXTURE
				+ "Down");
	}
}
