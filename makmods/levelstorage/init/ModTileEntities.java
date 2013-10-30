package makmods.levelstorage.init;

import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.tileentity.TileEntityLavaFabricator;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import makmods.levelstorage.tileentity.TileEntityMolecularHeater;
import makmods.levelstorage.tileentity.TileEntityMulticoreSolarPanel;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
import makmods.levelstorage.tileentity.TileEntitySuperconductorCable;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Initialization methods for LevelStorage's TileEntities. Currently everything
 * here is done by hand.
 * 
 * @author mak326428
 * 
 */
public class ModTileEntities {
	public static final ModTileEntities instance = new ModTileEntities();

	public static Map<Class<? extends TileEntity>, String> tileEntities = Maps
			.newHashMap();

	static {
		tileEntities.put(TileEntityXpGenerator.class, "tileXpGenerator");
		tileEntities.put(TileEntityXpCharger.class, "tileXpCharger");
		tileEntities.put(TileEntityWirelessPowerSynchronizer.class,
				"tileWirelessPowerSync");
		tileEntities.put(TileEntityWirelessConductor.class,
				"tileWirelessConductor");
		tileEntities.put(TileEntitySuperconductorCable.class,
				"tileSuperconductor");
		tileEntities.put(TileEntityMolecularHeater.class, "tileMolHeater");
		tileEntities.put(TileEntityMulticoreSolarPanel.class,
				"tileMulticoreSolarPanel");
		tileEntities.put(TileEntityParticleAccelerator.class,
				"tileParticleAccelerator");
		tileEntities.put(TileEntityRockDesintegrator.class,
				"tileRockDesintegrator");
		tileEntities.put(TileEntityLavaFabricator.class, "tileLavaFabricator");
		tileEntities.put(TileEntityMassMelter.class, "tileMassMelter");
	}

	private ModTileEntities() {
		;
	}

	public void init() {
		for (Entry<Class<? extends TileEntity>, String> entry : tileEntities
				.entrySet()) {
			GameRegistry.registerTileEntity(entry.getKey(), entry.getValue());
		}
	}
}
