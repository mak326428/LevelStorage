package makmods.levelstorage.tileentity;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements IHasTextBoxes {

	public int frequency;
	
	public TileEntityWirelessPowerSynchronizer() {
		
	}
	
	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			
		}
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			frequency = Integer.parseInt(newText);
		} catch (NumberFormatException e) {
		}
	}

}
