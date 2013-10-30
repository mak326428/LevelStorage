package makmods.levelstorage.tileentity;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import makmods.levelstorage.logic.util.BlockLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileEntitySuperconductorCable extends TileEntity implements
        IEnergyConductor {

	public int connectivity;

	public float getCableThickness() {
		return 0.25f;
	}

	public static double getCableThickness(int meta) {
		return 0.25f;
	}

	public int[] retextureRefId;
	public int[] retextureRefMeta;
	public int[] retextureRefSide;
	public int renderSide;

	public boolean retexture(int side, int referencedBlockId,
	        int referencedMeta, int referencedSide) {
		boolean ret = false;
		boolean updateAll = false;

		if (this.retextureRefId == null) {
			this.retextureRefId = new int[6];
			this.retextureRefMeta = new int[6];
			this.retextureRefSide = new int[6];
			updateAll = true;
		}

		if ((this.retextureRefId[side] != referencedBlockId) || (updateAll)) {
			this.retextureRefId[side] = referencedBlockId;
			ret = true;
		}

		if ((this.retextureRefMeta[side] != referencedMeta) || (updateAll)) {
			this.retextureRefMeta[side] = referencedMeta;
			ret = true;
		}

		if ((this.retextureRefSide[side] != referencedSide) || (updateAll)) {
			this.retextureRefSide[side] = referencedSide;
			ret = true;
		}

		return ret;
	}

	public boolean needsUpdate = true;

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToENet = true;
			}
		}
		onNeighborBlockChange();
	}

	public void remove() {
		if (addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
	}

	@Override
	public void invalidate() {
		remove();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		remove();
		super.onChunkUnload();
	}

	public void onNeighborBlockChange() {
		byte newConnectivity = 0;
		byte newRenderSide = 0;

		int mask = 1;

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			// TileEntity neighbor = EnergyNet.getForWorld(this.worldObj)
			// .getNeighbor(this, direction);
			BlockLocation currLocation = new BlockLocation(
			        this.worldObj.provider.dimensionId, this.xCoord,
			        this.yCoord, this.zCoord);
			BlockLocation nextLoc = currLocation.move(
			        direction, 1);
			TileEntity neighbor = worldObj.getBlockTileEntity(nextLoc.getX(),
			        nextLoc.getY(), nextLoc.getZ());

			if ((((neighbor instanceof IEnergyAcceptor)) && (((IEnergyAcceptor) neighbor)
			        .acceptsEnergyFrom(this, direction.getOpposite())))
			        || (((neighbor instanceof IEnergyEmitter)) && (((IEnergyEmitter) neighbor)
			                .emitsEnergyTo(this, direction.getOpposite())))) {
				newConnectivity = (byte) (newConnectivity | mask);

				if (((neighbor instanceof TileEntitySuperconductorCable))
				        && (((TileEntitySuperconductorCable) neighbor)
				                .getCableThickness() < getCableThickness())) {
					newRenderSide = (byte) (newRenderSide | mask);
				}
			}

			mask *= 2;
		}

		if (this.connectivity != newConnectivity) {
			this.connectivity = newConnectivity;
		}

		if (this.renderSide != newRenderSide) {
			this.renderSide = newRenderSide;
		}
	}

	public boolean addedToENet = false;

	@Override
	public double getConductionLoss() {
		return 0;
	}

	@Override
	public int getInsulationEnergyAbsorption() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getInsulationBreakdownEnergy() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getConductorBreakdownEnergy() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void removeInsulation() {

	}

	@Override
	public void removeConductor() {

	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
	        ForgeDirection direction) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}
}
