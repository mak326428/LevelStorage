package makmods.levelstorage.tileentity;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;

import java.util.List;

import com.google.common.collect.Lists;

import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class LSTileEntity extends TileEntity implements IEnergySink, IEnergyStorage, IEnergySource {

	/**
	 * Module type
	 * 
	 * @author mak326428
	 * 
	 */
	public interface ITileEntityModule {
		/**
		 * Handle a method with the given name and parameters
		 * 
		 * @param methodName
		 *            Name of the method (e.g. acceptsEnergyFrom)
		 * @param arguments
		 *            Arguments of the method.
		 * @return Null if module doesn't have anything to do with the method,
		 *         otherwise return type for the method
		 */
		public Object handleMethod(String methodName, LSTileEntity te, Object... arguments);
	}

	public List<ITileEntityModule> modules;

	public LSTileEntity(List<ITileEntityModule> modules) {
		this.modules = modules;
	}
	
	public LSTileEntity() {
		this.modules = Lists.newArrayList();
	}

	protected void addModule(ITileEntityModule module) {
		modules.add(module);
	}
	
	@Override
    public void updateEntity() {
		for (ITileEntityModule module : modules) {
			module.handleMethod("updateEntity", this);
		}
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
	        ForgeDirection direction) {
		for (ITileEntityModule module : modules) {
			Object retParam = module.handleMethod("acceptsEnergyFrom", this, emitter,
			        direction);
			if (retParam == null) {
				// Module has nothing to do with this method, go on
				continue;
			}
			if (!(retParam instanceof Boolean)) {
				LogHelper
				        .severe("LSTileEntity: "
				                + module
				                + ": wrong return type, expected boolean, got: "
				                + retParam.getClass().getSimpleName()
				                + ", in method acceptsEnergyFrom(TileEntity, ForgeDirection)");
			} else {
				if ((Boolean) retParam) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		for (ITileEntityModule module : modules) {
			Object retParam = module.handleMethod("emitsEnergyTo", this, receiver,
			        direction);
			if (retParam == null) {
				// Module has nothing to do with this method, go on
				continue;
			}
			if (!(retParam instanceof Boolean)) {
				LogHelper
				        .severe("LSTileEntity: "
				                + module
				                + ": wrong return type, expected boolean, got: "
				                + retParam.getClass().getSimpleName()
				                + ", in method emitsEnergyTo(TileEntity, ForgeDirection)");
			} else {
				if ((Boolean) retParam) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public double getOfferedEnergy() {
		int offeredEnergy = 0;
		for (ITileEntityModule module : modules) {
			Object retParam = module.handleMethod("getOfferedEnergy", this);
			if (retParam == null) {
				// Module has nothing to do with this method, go on
				continue;
			}
			if (!(retParam instanceof Integer)) {
				LogHelper
				        .severe("LSTileEntity: "
				                + module
				                + ": wrong return type, expected Integer, got: "
				                + retParam.getClass().getSimpleName()
				                + ", in method getOfferedEnergy()");
			} else {
				offeredEnergy += (Integer)retParam;
			}
		}
		return offeredEnergy;
	}

	@Override
	public void drawEnergy(double amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStored() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStored(int energy) {
		// TODO Auto-generated method stub

	}

	@Override
	public int addEnergy(int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutput() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double demandedEnergyUnits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxSafeInput() {
		// TODO Auto-generated method stub
		return 0;
	}

}
