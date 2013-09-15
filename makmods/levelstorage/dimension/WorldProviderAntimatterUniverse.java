package makmods.levelstorage.dimension;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderAntimatterUniverse extends WorldProvider {
	
	public WorldProviderAntimatterUniverse() {
		this.terrainType = WorldType.FLAT;
		this.worldChunkMgr = new WorldChunkManagerAntimatterUniverse();
		this.dimensionId = LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID;
	}
	
	public void registerWorldChunkManager()
	{
		this.worldChunkMgr = new WorldChunkManagerAntimatterUniverse();
		this.dimensionId = LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID;
	}
	
	@Override
	public String getWelcomeMessage() {
		return getDimensionName();
	}
	
    public IChunkProvider createChunkGenerator()
    {
        return new AntimatterUniverseChunkProvider(worldObj, getSeed());
    }
	
	@Override
	public boolean getWorldHasVoidParticles() {
		return true;
	}

	@Override
	public String getDimensionName() {
		return LSDimensions.getAntimatterUniverseDimName();
	}

}
