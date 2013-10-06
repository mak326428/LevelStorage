package makmods.levelstorage.dimension.worldgen;

import java.util.Random;

import makmods.levelstorage.LevelStorage;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGeneratorUUMFountain implements IWorldGenerator {

	public static int UUM;

	static {
		UUM = LevelStorage.IC2UUM.getBlockID();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// ~once eight chunks
		if (random.nextInt(RARITY) == 0) {
			int y = -1;
			int x = chunkX * 16 + random.nextInt(16);
			int z = chunkZ * 16 + random.nextInt(16);
			for (int i = 256; i >= 0; i--) {
				if (!world.isAirBlock(x, i, z)) {
					y = i;
					break;
				}
			}
			if (y > 0)
				// If it's on surface, go!
				generateFountain(world, random, x, y, z);
		}
	}

	public static final int RARITY = 8;

	public void generateFountain(World world, Random rand, int x, int y, int z) {

	}

}
