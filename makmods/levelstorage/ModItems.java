package makmods.levelstorage;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Level;

import makmods.levelstorage.item.ItemAdvancedScanner;
import makmods.levelstorage.item.ItemFrequencyCard;
import makmods.levelstorage.item.ItemLevelStorageBook;
import makmods.levelstorage.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModItems {
	public static final ModItems instance = new ModItems();

	public ItemLevelStorageBook itemLevelStorageBook;
	public ItemAdvancedScanner itemAdvScanner;
	public ItemFrequencyCard itemFreqCard;

	private int incr = 0;

	private ModItems() {
	}

	private void initItems() {
		itemLevelStorageBook = new ItemLevelStorageBook(
				LevelStorage.itemLevelStorageBookSpace);
		itemAdvScanner = new ItemAdvancedScanner();
		itemFreqCard = new ItemFrequencyCard();
	}

	private void addRecipes() {
		// Book
		ItemStack stackDepleted = new ItemStack(itemLevelStorageBook, 1, 0);
		stackDepleted.stackTagCompound = new NBTTagCompound();
		ItemStack stackBook = new ItemStack(Item.book);
		ItemStack stackGoldBlock = new ItemStack(Block.blockGold);
		ItemStack stackEnchTable = new ItemStack(Block.enchantmentTable);
		GameRegistry.addShapelessRecipe(stackDepleted, stackBook,
				stackGoldBlock, stackEnchTable);
		// Scanner
		ItemStack ovScanner = Items.getItem("ovScanner");
		ItemStack uum = Items.getItem("matter");
		ItemStack energyCrystal = Items.getItem("energyCrystal");
		ItemStack advCircuit = Items.getItem("advancedCircuit");
		ItemStack glassFiber = Items.getItem("glassFiberCableItem");
		ItemStack advScanner = new ItemStack(itemAdvScanner);
		Recipes.advRecipes.addRecipe(advScanner, "ucu", "asa", "ggg",
				Character.valueOf('u'), uum, Character.valueOf('g'),
				glassFiber, Character.valueOf('a'), advCircuit,
				Character.valueOf('c'), energyCrystal, Character.valueOf('s'),
				ovScanner);

		// Frequency card
		ItemStack frequencyTr = Items.getItem("frequencyTransmitter");
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(itemFreqCard),
				frequencyTr, new ItemStack(Item.paper));
		// To get rid of card data
		Recipes.advRecipes.addShapelessRecipe(new ItemStack(itemFreqCard),
				new ItemStack(itemFreqCard));
		CraftingManager.getInstance().getRecipeList()
				.add(new ExperienceRecipe());
	}

	private void addCustomFeatures() {
		for (Field f : ChestGenHooks.class.getDeclaredFields()) {
			try {
				String category = (String) f.get(null);

				Random rnd = new Random();
				rnd.setSeed(rnd.nextInt() + this.incr);

				ItemStack bookStack = new ItemStack(itemLevelStorageBook);
				bookStack.stackTagCompound = new NBTTagCompound();
				bookStack.stackTagCompound.setInteger(
						ItemLevelStorageBook.STORED_XP_NBT,
						rnd.nextInt(LevelStorage.itemLevelStorageBookSpace));
				bookStack.setItemDamage(ItemLevelStorageBook
						.calculateDurability(bookStack));
				ChestGenHooks.addItem(category, new WeightedRandomChestContent(
						bookStack, 0, 1, 10));
				this.incr++;
			} catch (Exception e) {
			}
		}
	}

	private void addItemNames() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getName() != "instance") {
				try {
					Item currItem = (Item) f.get(ModItems.instance);
					String name = (String) currItem.getClass()
							.getField("NAME").get(null);
					LanguageRegistry.addName(currItem, name);
				} catch (ClassCastException e) {
				} catch (Exception e) {
					FMLLog.log(Level.SEVERE, Reference.MOD_NAME
							+ ": failed to name item");
					e.printStackTrace();
				}
			}
		}
	}

	public void init() {
		this.initItems();
		this.addRecipes();
		this.addItemNames();
		this.addCustomFeatures();
	}
}
