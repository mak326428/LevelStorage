package makmods.levelstorage.proxy;

import makmods.levelstorage.gui.ContainerAdvancedMiner;
import makmods.levelstorage.gui.ContainerMassInfuser;
import makmods.levelstorage.gui.ContainerMolecularHeater;
import makmods.levelstorage.gui.ContainerPowerSync;
import makmods.levelstorage.gui.ContainerWirelessConductor;
import makmods.levelstorage.gui.ContainerXpCharger;
import makmods.levelstorage.gui.ContainerXpGenerator;
import makmods.levelstorage.gui.GuiAdvancedMiner;
import makmods.levelstorage.gui.GuiMassInfuser;
import makmods.levelstorage.gui.GuiMolecularHeater;
import makmods.levelstorage.gui.GuiWirelessConductor;
import makmods.levelstorage.gui.GuiWirelessPowerSync;
import makmods.levelstorage.gui.GuiXpCharger;
import makmods.levelstorage.gui.GuiXpGenerator;
import makmods.levelstorage.tileentity.TileEntityAdvancedMiner;
import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import makmods.levelstorage.tileentity.TileEntityMolecularHeater;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
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
		if (tileEntity instanceof TileEntityXpGenerator)
			return new ContainerXpGenerator(player.inventory,
			        (TileEntityXpGenerator) tileEntity);
		if (tileEntity instanceof TileEntityXpCharger)
			return new ContainerXpCharger(player.inventory,
			        (TileEntityXpCharger) tileEntity);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new ContainerWirelessConductor(player.inventory,
			        (TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new ContainerPowerSync(player.inventory,
			        (TileEntityWirelessPowerSynchronizer) tileEntity);
		if (tileEntity instanceof TileEntityMassInfuser)
			return new ContainerMassInfuser(player.inventory,
			        (TileEntityMassInfuser) tileEntity);
		if (tileEntity instanceof TileEntityAdvancedMiner)
			return new ContainerAdvancedMiner(player.inventory,
			        (TileEntityAdvancedMiner) tileEntity);
		if (tileEntity instanceof TileEntityMolecularHeater)
			return new ContainerMolecularHeater(player.inventory,
			        (TileEntityMolecularHeater) tileEntity);

		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
	        int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityXpGenerator)
			return new GuiXpGenerator(player.inventory,
			        (TileEntityXpGenerator) tileEntity);
		if (tileEntity instanceof TileEntityXpCharger)
			return new GuiXpCharger(player.inventory,
			        (TileEntityXpCharger) tileEntity);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new GuiWirelessConductor(player.inventory,
			        (TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new GuiWirelessPowerSync(player.inventory,
			        (TileEntityWirelessPowerSynchronizer) tileEntity);
		if (tileEntity instanceof TileEntityMassInfuser)
			return new GuiMassInfuser(player.inventory,
			        (TileEntityMassInfuser) tileEntity);
		if (tileEntity instanceof TileEntityAdvancedMiner)
			return new GuiAdvancedMiner(player.inventory,
			        (TileEntityAdvancedMiner) tileEntity);
		if (tileEntity instanceof TileEntityMolecularHeater)
			return new GuiMolecularHeater(player.inventory,
			        (TileEntityMolecularHeater) tileEntity);

		return null;

	}
}
