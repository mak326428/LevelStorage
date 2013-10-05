package makmods.levelstorage.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
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
	
    public ChunkCoordinates getSpawnPoint()
    {
    	return new ChunkCoordinates(0, 255, 0);
    }
	
    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
    {
    	return worldObj.getWorldVec3Pool().getVecFromPool(0.0F, 0.0F, 0.0F);
    }
    
    public Vec3 getFogColor(float par1, float par2)
    {
    	return worldObj.getWorldVec3Pool().getVecFromPool(0.0F, 0.0F, 0.0F);
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
