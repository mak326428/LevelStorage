package makmods.levelstorage;

import makmods.levelstorage.tileentity.TileEntitySuperconductorCable;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModTileEntities {
	public static final ModTileEntities instance = new ModTileEntities();

	private ModTileEntities() {
	}

	public void init() {
		GameRegistry.registerTileEntity(TileEntityXpGenerator.class,
				"tileXpGenerator");
		GameRegistry.registerTileEntity(TileEntityXpCharger.class,
				"tileXpCharger");
		GameRegistry.registerTileEntity(TileEntityWirelessConductor.class,
				"tileWirelessConductor");
		GameRegistry.registerTileEntity(
				TileEntityWirelessPowerSynchronizer.class,
				"tileWirelessPowerSync");
		GameRegistry.registerTileEntity(TileEntitySuperconductorCable.class, "tileSuperconductor");

	}
}
