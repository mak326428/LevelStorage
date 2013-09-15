package makmods.levelstorage.network.tilesync;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Must be implemented by TileEntities.
 * Used for server-client sync.
 * @author mak326428
 */
public interface INetworkEventListener {
	void handleNetworkEvent(int id, NBTTagCompound data);
}
