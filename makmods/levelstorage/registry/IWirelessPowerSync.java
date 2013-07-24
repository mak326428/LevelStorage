package makmods.levelstorage.registry;

import net.minecraft.world.World;

/**
 * Basic interface for Wireless Power Syncs.
 * 
 * @author mak326428
 */
public interface IWirelessPowerSync {
	public int getFreq();

	public World getWorld();

	public int getX();

	public int getY();

	public int getZ();

	public SyncType getType();

	public void updateState();

	public int receiveEnergy(int amount);

	public boolean doesNeedEnergy();
}