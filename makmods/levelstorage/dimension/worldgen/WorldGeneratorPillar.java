package makmods.levelstorage.dimension.worldgen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.logic.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.IWorldGenerator;

public class WorldGeneratorPillar implements IWorldGenerator {

	public static List<Block> toGenerate = Lists.newArrayList();

	static {
		Block[] defaults = { Block.oreCoal, Block.oreDiamond, Block.oreEmerald,
				Block.oreGold, Block.oreIron, Block.oreLapis, Block.oreRedstone };
		for (Block def : defaults)
			toGenerate.add(def);
		try {
			Class oreDictClass = OreDictionary.class;
			Field fieldOreIds = oreDictClass.getDeclaredField("oreIDs");
			fieldOreIds.setAccessible(true);
			Map<String, Integer> oreIDs = (HashMap<String, Integer>) fieldOreIds
					.get(null);
			for (String s : oreIDs.keySet()) {
				try {
					if (s.startsWith("ore")) {
						ArrayList<ItemStack> stacksForGiven = OreDictionary
								.getOres(s);
						for (ItemStack st : stacksForGiven) {
							if (st == null)
								continue;
							if (st.getItem() instanceof ItemBlock)
								toGenerate.add(Block.blocksList[st.itemID]);
						}
					}
				} catch (Exception e) {
					LogHelper
							.severe("Exception when trying to dynamically allocate more ores to generate");
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			LogHelper
					.severe("Exception when trying to dynamically allocate more ores to generate");
			e.printStackTrace();
		}
	}
	
	public static int RARITY = 16;

	public static Block wrapper = LSBlockItemList.blockUnstableQuartz;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (random.nextInt(RARITY) == 0) {
			int genX = chunkX * 16 + random.nextInt(16);
			int genZ = chunkZ * 16 + random.nextInt(16);
			int genY = 0;
			for (int i = 256; i >= 0; i--) {
				if (!world.isAirBlock(genX, i, genZ)) {
					genY = i;
					break;
				}
			}
			if (genY > 0) {
				int height = random.nextInt(150) + 80;
				int THICKNESS = (int) Math.ceil((float) height / (float) 40
						* (float) 3);
				for (int i = 0; i < height; i++) {
					for (int x = -(THICKNESS / 2) + 1; x < THICKNESS / 2; x++) {
						for (int z = -(THICKNESS / 2) + 1; z < THICKNESS / 2; z++) {
							int xT = genX + x;
							int yT = genY + i;
							int zT = genZ + z;
							int idToGenerate = toGenerate.get(random
									.nextInt(toGenerate.size())).blockID;
							world.setBlock(xT, yT, zT, idToGenerate);
						}
					}
					for (int x = -(THICKNESS / 2); x <= THICKNESS / 2; x++) {
						for (int z = -(THICKNESS / 2); z <= THICKNESS / 2; z++) {
							int xT = genX + x;
							int yT = genY + i;
							int zT = genZ + z;
							if (world.isAirBlock(xT, yT, zT))
								world.setBlock(xT, yT, zT, wrapper.blockID);
						}
					}
				}
			}
		}
	}

}
