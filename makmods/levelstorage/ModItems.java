package makmods.levelstorage;

import java.lang.reflect.Field;
import java.util.Random;

import makmods.levelstorage.item.ItemLevelStorageBook;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModItems {
	public static final ModItems instance = new ModItems();
	
	public static ItemLevelStorageBook itemLevelStorageBook;
	
	private int incr = 0;
	
	private ModItems() {
	}
	
	private void initItems() {
		itemLevelStorageBook = new ItemLevelStorageBook(BlockItemIds.instance.getIdFor("itemXPBookId"),
				LevelStorage.itemLevelStorageBookSpace);
	}
	
	private void addRecipes() {
		ItemStack stackDepleted = new ItemStack(itemLevelStorageBook, 1, 0);
		stackDepleted.stackTagCompound = new NBTTagCompound();
		ItemStack stackBook = new ItemStack(Item.book);
		ItemStack stackGoldBlock = new ItemStack(Block.blockGold);
		ItemStack stackEnchTable = new ItemStack(Block.enchantmentTable);
		GameRegistry.addShapelessRecipe(stackDepleted, stackBook,
				stackGoldBlock, stackEnchTable);
		CraftingManager.getInstance().getRecipeList()
		.add(new ExperienceRecipe());
	}
	
	private void addCustomFeatures() {
		for (Field f : ChestGenHooks.class.getDeclaredFields()) {
			try {
				String category = (String)f.get(null);
				
				Random rnd = new Random();
				rnd.setSeed(rnd.nextInt() + incr);
				
				ItemStack bookStack = new ItemStack(this.itemLevelStorageBook);
				bookStack.stackTagCompound = new NBTTagCompound();
				bookStack.stackTagCompound.setInteger(ItemLevelStorageBook.STORED_XP_NBT, rnd.nextInt(LevelStorage.itemLevelStorageBookSpace));
				bookStack.setItemDamage(ItemLevelStorageBook.calculateDurability(bookStack));
				
				ChestGenHooks.addItem(category, new WeightedRandomChestContent(bookStack, 0, 1, 10));
				
				this.incr++;
			} catch (Exception e) { }
		}
	}
	
	private void addItemNames() {
		LanguageRegistry.addName(itemLevelStorageBook, "XP Storage Book");
	}
	
	public void init() {
		this.initItems();
		this.addRecipes();
		this.addItemNames();
		this.addCustomFeatures();
	}
}
