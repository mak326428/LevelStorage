package makmods.levelstorage.worldgen;

import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import cpw.mods.fml.common.IWorldGenerator;

public class LSWorldGenerator implements IWorldGenerator {

	public static LSWorldGenerator instance;
	public static int oreDensityFactor;
	public static float GENERATON_THRESHOLD = 10.0F;

	public static final String WORLDGEN_CATEGORY = "worldgen";

	public LSWorldGenerator() {
		oreDensityFactor = LevelStorage.configuration.get(WORLDGEN_CATEGORY,
				"oreDensity", 1).getInt();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int baseChanceOfGeneration = Math.round(5.0F * oreDensityFactor);
		int chanceOfGeneration = baseChanceOfGeneration + random.nextInt(15);
		BiomeGenBase biomegenbase = world.getWorldChunkManager().getBiomeGenAt(
				chunkX * 16 + 16, chunkZ * 16 + 16);
		if (biomegenbase != null && (biomegenbase.biomeName != null)) {
			if (BiomeDictionary.isBiomeOfType(biomegenbase,
					BiomeDictionary.Type.END)) {
				chanceOfGeneration += 25 + random.nextInt(7);
			}
			if (BiomeDictionary.isBiomeOfType(biomegenbase,
					BiomeDictionary.Type.PLAINS)) {
				chanceOfGeneration += 2 + random.nextInt(5);
			}
		}
		if (chanceOfGeneration > GENERATON_THRESHOLD) {
			int x = chunkX + random.nextInt(16);
			int y = random.nextInt(60);
			int z = chunkZ * 16 + random.nextInt(16);
			System.out
					.println(String.format("generating at %d:%d:%d", x, y, z));
			new WorldGenMinable(LSBlockItemList.blockAntimatterStone.blockID,
					30).generate(world, random, x, y, z);
		}
	}
}
