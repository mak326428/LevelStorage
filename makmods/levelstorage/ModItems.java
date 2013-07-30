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
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getName() != "instance") {
				try {
					Class c = f.getType();
					f.set(ModItems.instance, c.newInstance());
				} catch (ClassCastException e) {
				} catch (Exception e) {
					FMLLog.log(Level.SEVERE, Reference.MOD_NAME
							+ ": failed to initialize item");
					e.printStackTrace();
				}
			}
		}
	}

	private void addRecipes() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getName() != "instance") {
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
