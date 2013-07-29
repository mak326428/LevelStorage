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
		LanguageRegistry.addName(this.blockXpCharger, "XP Charger");
		LanguageRegistry
				.addName(this.blockWlessConductor, "Wireless Conductor");
		LanguageRegistry.addName(this.blockXpGen, "XP Generator");
		LanguageRegistry.addName(this.blockWlessPowerSync,
				"Wireless Power Synchronizer");

	}

	private void addRecipes() {
		ItemStack blockXpGenStack = new ItemStack(this.blockXpGen);
		ItemStack blockXpChargerStack = new ItemStack(this.blockXpCharger);
		ItemStack advMachine = Items.getItem("advancedMachine");
		ItemStack machine = Items.getItem("machine");
		ItemStack generator = Items.getItem("generator");
		ItemStack refIron = Items.getItem("refinedIronIngot");
		ItemStack goldIngot = new ItemStack(Item.ingotGold);
		GameRegistry.addRecipe(blockXpGenStack, "iai", "geg", "imi",
				Character.valueOf('i'), refIron, Character.valueOf('g'),
				goldIngot, Character.valueOf('a'), advMachine,
				Character.valueOf('m'), machine, Character.valueOf('e'),
				generator);

		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"enableChargerRecipe", true).getBoolean(true)) {
			GameRegistry.addRecipe(blockXpChargerStack, "iai", "geg", "imi",
					Character.valueOf('i'), refIron, Character.valueOf('g'),
					goldIngot, Character.valueOf('a'), advMachine,
					Character.valueOf('m'), machine, Character.valueOf('e'),
					blockXpGenStack);
		}

		ItemStack frequencyTr = Items.getItem("frequencyTransmitter");
		ItemStack transformerHv = Items.getItem("hvTransformer");
		ItemStack enderPearl = new ItemStack(Item.enderPearl);
		ItemStack advCircuit = Items.getItem("advancedCircuit");
		ItemStack sync = new ItemStack(blockWlessPowerSync, 4);

		Recipes.advRecipes.addRecipe(new ItemStack(this.blockWlessConductor),
				"tmt", "cec", "chc", Character.valueOf('t'), frequencyTr,
				Character.valueOf('e'), enderPearl, Character.valueOf('c'),
				advCircuit, Character.valueOf('h'), transformerHv,
				Character.valueOf('m'), advMachine);

		Recipes.advRecipes.addRecipe(sync, "ccc", "ama", "ccc",
				Character.valueOf('a'), advCircuit, Character.valueOf('c'),
				new ItemStack(this.blockWlessConductor),
				Character.valueOf('m'), advMachine);

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
