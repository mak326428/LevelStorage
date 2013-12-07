package makmods.levelstorage.proxy;

import makmods.levelstorage.gui.client.GuiMolecularHeater;
import makmods.levelstorage.gui.client.GuiWirelessConductor;
import makmods.levelstorage.gui.client.GuiWirelessPowerSync;
import makmods.levelstorage.gui.container.ContainerMolecularHeater;
import makmods.levelstorage.gui.container.ContainerPowerSync;
import makmods.levelstorage.gui.container.ContainerWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityMolecularHeater;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import makmods.levelstorage.tileentity.template.ITEHasGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class LSGUIHandler implements IGuiHandler {

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
	        int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new ContainerWirelessConductor(player.inventory,
			        (TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new ContainerPowerSync(player.inventory,
			        (TileEntityWirelessPowerSynchronizer) tileEntity);
		if (tileEntity instanceof TileEntityMolecularHeater)
			return new ContainerMolecularHeater(player.inventory,
			        (TileEntityMolecularHeater) tileEntity);
		if (tileEntity instanceof ITEHasGUI)
			return ((ITEHasGUI)tileEntity).getContainer(player, world, x, y, z);
		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
	        int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new GuiWirelessConductor(player.inventory,
			        (TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new GuiWirelessPowerSync(player.inventory,
			        (TileEntityWirelessPowerSynchronizer) tileEntity);
		if (tileEntity instanceof TileEntityMolecularHeater)
			return new GuiMolecularHeater(player.inventory,
			        (TileEntityMolecularHeater) tileEntity);
		if (tileEntity instanceof ITEHasGUI)
			return ((ITEHasGUI)tileEntity).getGUI(player, world, x, y, z);
		return null;

	}
}
