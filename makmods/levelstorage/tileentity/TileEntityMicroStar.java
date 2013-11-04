package makmods.levelstorage.tileentity;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.logic.util.BlockLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityMicroStar extends TileEntity {

	public int getBlockID(BlockLocation bl) {
		return worldObj.getBlockId(bl.getX(), bl.getY(), bl.getZ());
	}

	public boolean checkMultiblock() {
		BlockLocation currLoc = new BlockLocation(this.xCoord, this.yCoord,
				this.zCoord);
		boolean glassExists = (getBlockID(currLoc.move(ForgeDirection.UP, 1)) == LSBlockItemList.fusionGlass.blockID)
				&& (getBlockID(currLoc.move(ForgeDirection.DOWN, 1)) == LSBlockItemList.fusionGlass.blockID)
				&& (getBlockID(currLoc.move(ForgeDirection.EAST, 1)) == LSBlockItemList.fusionGlass.blockID)
				&& (getBlockID(currLoc.move(ForgeDirection.WEST, 1)) == LSBlockItemList.fusionGlass.blockID)
				&& (getBlockID(currLoc.move(ForgeDirection.NORTH, 1)) == LSBlockItemList.fusionGlass.blockID)
				&& (getBlockID(currLoc.move(ForgeDirection.SOUTH, 1)) == LSBlockItemList.fusionGlass.blockID);
		
		boolean sidingExists = true;
		return glassExists && sidingExists;
	}

	@Override
	public void updateEntity() {
		//System.out.println(checkMultiblock());
	}

}
