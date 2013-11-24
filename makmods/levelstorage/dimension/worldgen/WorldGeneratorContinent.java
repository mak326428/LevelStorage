package makmods.levelstorage.dimension.worldgen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGeneratorContinent implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (random.nextInt(30) == 0) {
			long msStart = System.currentTimeMillis();
			int baseX = chunkX * 16 + random.nextInt(16);
			int baseZ = chunkZ * 16 + random.nextInt(16);
			int baseY = random.nextInt(64) + 32;

			int size = random.nextInt(16) + 8;
			// we need odd number
			if (size % 2 == 0)
				size++;
			// int sizeEven - size--;
			for (int i = 0; i < size; i++) {

			}

			System.out.println("Generating a continent took "
					+ (System.currentTimeMillis() - msStart) + "ms.");
		}
	}

}
