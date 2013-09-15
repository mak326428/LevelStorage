package makmods.levelstorage.item;

import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElectricLighter extends ItemBasicElectric {

	public ItemElectricLighter(int id) {
		super(id, 2, 100000, 1000, 500);
	}

	public boolean addFire(World world, int x, int y, int z, int sideHit) {
		boolean addedFire = false;
		BlockLocation currBlockLocation = new BlockLocation(x, y, z);
		BlockLocation shifted = currBlockLocation.move(
				ForgeDirection.getOrientation(sideHit), 1);
		if (!world.isAirBlock(shifted.getX(), shifted.getY(), shifted.getZ())) {
			return false;
		}
		world.setBlock(shifted.getX(), shifted.getY(), shifted.getZ(),
				Block.fire.blockID);
		addedFire = true;
		return addedFire;
	}

	@Override
	public boolean onBlockClick(ItemStack item, World world,
			EntityPlayer player, int x, int y, int z, int side) {
		int blockID = world.getBlockId(x, y, z);
		if (blockID == 0)
			return addFire(world, x, y, z, side);
		int blockMeta = world.getBlockMetadata(x, y, z);
		ItemStack smResult = FurnaceRecipes.smelting().getSmeltingResult(
				new ItemStack(blockID, 1, blockMeta));
		if (smResult == null)
			return addFire(world, x, y, z, side);
		if (smResult.getItem() instanceof ItemBlock)
			world.setBlock(x, y, z, smResult.itemID, smResult.getItemDamage(),
					3);
		else
			return addFire(world, x, y, z, side);
		return true;
	}

	@Override
	public boolean onRightClick(ItemStack item, World world, EntityPlayer player) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemTexture() {
		return ClientProxy.ELECTRIC_LIGHTER_TEXTURE;
	}

}
