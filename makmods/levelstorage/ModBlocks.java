package makmods.levelstorage;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Field;
import java.util.logging.Level;

import makmods.levelstorage.block.BlockWirelessConductor;
import makmods.levelstorage.block.BlockWirelessPowerSynchronizer;
import makmods.levelstorage.block.BlockXpCharger;
import makmods.levelstorage.block.BlockXpGenerator;
import makmods.levelstorage.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModBlocks {
	public static final ModBlocks instance = new ModBlocks();

	public BlockXpGenerator blockXpGen;
	public BlockXpCharger blockXpCharger;
	public BlockWirelessConductor blockWlessConductor;
	public BlockWirelessPowerSynchronizer blockWlessPowerSync;

	private ModBlocks() {
	}

	private void initBlocks() {

	}

	private void createBlocks() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				Class c = f.getType();
				f.set(ModBlocks.instance, c.newInstance());
			} catch (ClassCastException e) {
			} catch (Exception e) {
				FMLLog.log(Level.SEVERE, Reference.MOD_NAME
						+ ": failed to initialize block");
				e.printStackTrace();
			}
		}
	}

	private void registerBlocks() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				Block currBlock = (Block) f.get(ModBlocks.instance);
				String name = (String) currBlock.getClass()
						.getField("UNLOCALIZED_NAME").get(null);
				GameRegistry.registerBlock(currBlock, name);
			} catch (ClassCastException e) {
			} catch (Exception e) {
				FMLLog.log(Level.SEVERE, Reference.MOD_NAME
						+ ": failed to register block");
				e.printStackTrace();
			}
		}

	}

	private void addBlockNames() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				Block currBlock = (Block) f.get(ModBlocks.instance);
				String name = (String) currBlock.getClass()
						.getField("NAME").get(null);
				LanguageRegistry.addName(currBlock, name);
			} catch (ClassCastException e) {
			} catch (Exception e) {
				FMLLog.log(Level.SEVERE, Reference.MOD_NAME
						+ ": failed to name block");
				e.printStackTrace();
			}
		}
	}

	private void addRecipes() {
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				f.getType().getMethod("addCraftingRecipe").invoke(null);
			} catch (ClassCastException e) {
			} catch (Exception e) {
				FMLLog.log(Level.SEVERE, Reference.MOD_NAME
						+ ": failed to add recipe");
				e.printStackTrace();
			}
		}
	}

	private void setMiningLevels() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				MinecraftForge.setBlockHarvestLevel(
						(Block) f.get(ModBlocks.instance), "pickaxe", 1);
			} catch (ClassCastException e) {
			} catch (Exception e) {
				FMLLog.log(Level.SEVERE, Reference.MOD_NAME
						+ ": failed to register block");
				e.printStackTrace();
			}
		}
	}

	public void init() {
		this.createBlocks();
		this.initBlocks();
		this.registerBlocks();
		this.addBlockNames();
		this.addRecipes();
		this.setMiningLevels();
	}
}
