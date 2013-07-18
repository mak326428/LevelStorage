package makmods.levelstorage.proxy;

import makmods.levelstorage.gui.ContainerXpCharger;
import makmods.levelstorage.gui.ContainerXpGenerator;
import makmods.levelstorage.gui.GuiXpCharger;
import makmods.levelstorage.gui.GuiXpGenerator;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityXpGenerator) {
			return new ContainerXpGenerator(player.inventory,
					(TileEntityXpGenerator) tileEntity);
		}
		if (tileEntity instanceof TileEntityXpCharger) {
			return new ContainerXpCharger(player.inventory,
					(TileEntityXpCharger) tileEntity);
		}
		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityXpGenerator) {
			return new GuiXpGenerator(player.inventory,
					(TileEntityXpGenerator) tileEntity);
		}
		if (tileEntity instanceof TileEntityXpCharger) {
			return new GuiXpCharger(player.inventory,
					(TileEntityXpCharger) tileEntity);
		}
		return null;

	}
}
