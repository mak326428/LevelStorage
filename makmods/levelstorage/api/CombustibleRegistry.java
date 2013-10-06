package makmods.levelstorage.api;

import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.api.event.CombustibleRegisterEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.Maps;

/**
 * An API for Combustible Generator's fuels.
 * 
 * @author mak326428
 * 
 */
public class CombustibleRegistry {

	/**
	 * Returned by {@link #getBurningValue(Fluid)} when fluid is not registered.
	 */
	public static final int NOT_FOUND = -1;

	/**
	 * Invoked during LevelStorage's Post-Init phase. Do not call!
	 */
	public static void registerDefaults() {
		registeredCombustibles.clear();
		registerCombustible(FluidRegistry.LAVA, 15000);
		if (FluidRegistry.isFluidRegistered("oil"))
			registerCombustible(FluidRegistry.getFluid("oil"), 100000);
		if (FluidRegistry.isFluidRegistered("fuel"))
			registerCombustible(FluidRegistry.getFluid("fuel"), 600000);
	}

	/**
	 * Fluid -> EU
	 */
	private static Map<Fluid, Integer> registeredCombustibles = Maps
			.newHashMap();

	/**
	 * Returns EU you get from boiling a fluid in a Combustible Generator.
	 * 
	 * @param fluid
	 *            Fluid
	 * @return EU
	 */
	public static int getBurningValue(Fluid fluid) {
		for (Entry<Fluid, Integer> entry : registeredCombustibles.entrySet()) {
			if (entry.getKey().getID() == fluid.getID())
				return entry.getValue();
		}
		return NOT_FOUND;
	}

	/**
	 * Registers combustible with this registry
	 * 
	 * @param fluid
	 *            Fluid you want to register
	 * @param value
	 *            EU player'll get when he/she boils a bucket of it in
	 *            Combustible Generator
	 */
	public static void registerCombustible(Fluid fluid, int value) {
		CombustibleRegisterEvent event = new CombustibleRegisterEvent(fluid,
				value);
		if (!event.isCanceled())
			registeredCombustibles.put(fluid, value);
	}

	/**
	 * No comments.
	 * 
	 * @param fluid
	 *            Fluid.
	 */
	public static void removeCombustible(Fluid fluid) {
		registeredCombustibles.remove(fluid);
	}
}
