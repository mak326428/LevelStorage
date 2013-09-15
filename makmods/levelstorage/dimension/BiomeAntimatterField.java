package makmods.levelstorage.dimension;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeAntimatterField extends BiomeGenBase {

	public BiomeAntimatterField(int id) {
		super(id);
		setColor(16421912);
		setBiomeName("Antimatter Field");
		setDisableRain();
		setTemperatureRainfall(2.0F, 0.0F);
		setMinMaxHeight(0.1F, 0.2F);
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.topBlock = 0;
        this.fillerBlock = 0;
		BiomeDictionary.registerBiomeType(this, Type.PLAINS);
	}

	@Override
	public void decorate(World par1World, Random par2Random, int par3, int par4) {
	}
}
