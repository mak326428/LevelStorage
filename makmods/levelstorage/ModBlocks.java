package makmods.levelstorage;

import ic2.api.item.Items;
import makmods.levelstorage.block.BlockXpCharger;
import makmods.levelstorage.block.BlockXpGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModBlocks {
	public static final ModBlocks instance = new ModBlocks();

	public BlockXpGenerator blockXpGen;
	public BlockXpCharger blockXpCharger;

	private ModBlocks() {
	}

	private void initBlocks() {
		blockXpGen = new BlockXpGenerator(
				BlockItemIds.instance.getIdFor("blockXpGenId"));
		blockXpCharger = new BlockXpCharger(
				BlockItemIds.instance.getIdFor("blockXpChargerId"));
	}

	private void registerBlocks() {
		GameRegistry.registerBlock(blockXpGen, "tile.blockXpGen");
		GameRegistry.registerBlock(blockXpCharger, "tile.blockXpCharger");
	}

	private void addBlockNames() {
		LanguageRegistry.addName(blockXpGen, "XP Generator");
		LanguageRegistry.addName(blockXpCharger, "XP Charger");
	}

	private void addRecipes() {
		ItemStack blockXpGenStack = new ItemStack(blockXpGen);
		ItemStack blockXpChargerStack = new ItemStack(blockXpCharger);
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
		GameRegistry.addRecipe(blockXpChargerStack, "iai", "geg", "imi",
				Character.valueOf('i'), refIron, Character.valueOf('g'),
				goldIngot, Character.valueOf('a'), advMachine,
				Character.valueOf('m'), machine, Character.valueOf('e'),
				blockXpGenStack);
	}
	
	private void setMiningLevels() {
		MinecraftForge.setBlockHarvestLevel(blockXpGen, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(blockXpCharger, "pickaxe", 1);
	}

	public void init() {
		this.initBlocks();
		this.registerBlocks();
		this.addBlockNames();
		this.addRecipes();
		this.setMiningLevels();
	}
}
