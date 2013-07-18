package makmods.levelstorage;

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
	}
}
