package makmods.levelstorage.tileentity;

import makmods.levelstorage.logic.Helper;
import makmods.levelstorage.registry.SyncType;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWirelessPowerSynchronizer extends TileEntity implements
		IHasTextBoxes, IHasButtons {

	public int frequency;
	public SyncType type;

	public TileEntityWirelessPowerSynchronizer() {
		type = SyncType.RECEIVER;
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			System.out.println(type.ordinal());
		}
	}

	@Override
	public void handleTextChange(String newText) {
		try {
			frequency = Integer.parseInt(newText);
		} catch (NumberFormatException e) {
		}
	}

	@Override
	public void handleButtonClick(int buttonId) {
		if (buttonId == 1) {
			this.type = Helper.invertType(this.type);
		}
		
	}

}
