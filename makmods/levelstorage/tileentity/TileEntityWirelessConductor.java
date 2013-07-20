package makmods.levelstorage.tileentity;

import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.registry.WirelessConductorRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWirelessConductor extends TileEntity implements IWirelessConductor {
	
	public ConductorType type;
	
	// This will be changed when a new valid (!!!) card is inserted.
	public IWirelessConductor pair = null;
	
	public int receiveEnergy(int amount) {
		// What the heck are you asking me, i am source!
		if (this.type == ConductorType.SOURCE)
			return Integer.MAX_VALUE;
		return 0;
	}
	
	public IWirelessConductor getPair() {
		return this.pair;
	}
	
	public int getDimId() {
		return this.worldObj.provider.dimensionId;
	}
	
	public int getX() {
		return this.xCoord;
	}
	
	public int getY() {
		return this.yCoord;
	}
	
	public int getZ() {
		return this.zCoord;
	}

	public TileEntityWirelessConductor() {
		this.type = ConductorType.SOURCE;
	}
	
	@Override
	public void onChunkUnload()
    {
		WirelessConductorRegistry.instance.removeFromRegistry(this);
    }
	
	@Override
	public void invalidate() {
		WirelessConductorRegistry.instance.removeFromRegistry(this);
		super.invalidate();
	}
	
	@Override
	public void updateEntity() {
		if (!WirelessConductorRegistry.instance.isAddedToRegistry(this))
			WirelessConductorRegistry.instance.addConductorToRegistry(this, type);
		if (WirelessConductorRegistry.instance.getConductorType(this) != type)
			WirelessConductorRegistry.instance.setConductorType(this, type);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
	}

}
