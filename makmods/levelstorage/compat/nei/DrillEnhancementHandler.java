package makmods.levelstorage.compat.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.item.ItemEnhancedDiamondDrill.EnhancementUtility;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class DrillEnhancementHandler extends TemplateRecipeHandler {

	public class CachedShapedRecipe extends CachedRecipe {
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;

		public CachedShapedRecipe(int width, int height, Object[] items,
				ItemStack out) {
			result = new PositionedStack(out, 119, 24);
			ingredients = new ArrayList<PositionedStack>();
			setIngredients(width, height, items);
		}

		public CachedShapedRecipe(ShapedRecipes recipe) {
			this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems,
					recipe.getRecipeOutput());
		}

		/**
		 * @param width
		 * @param height
		 * @param items
		 *            an ItemStack[] or ItemStack[][]
		 */
		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (items[y * width + x] == null)
						continue;

					PositionedStack stack = new PositionedStack(items[y * width
							+ x], 25 + x * 18, 6 + y * 18, false);
					stack.setMaxSize(1);
					ingredients.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		public PositionedStack getResult() {
			return result;
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();

			result.generatePermutations();
		}
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18),
				"crafting"));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiCrafting.class;
	}

	@Override
	public String getRecipeName() {
		return StatCollector
				.translateToLocal("other.drillEnhancementRecipeNEI");
	}

	/*
	 * @Override public void loadCraftingRecipes(String outputId, Object...
	 * results) { if (outputId.equals("crafting")) { List<IRecipe> allrecipes =
	 * CraftingManager.getInstance() .getRecipeList(); for (IRecipe irecipe :
	 * allrecipes) { CachedShapedRecipe recipe = null; if (irecipe instanceof
	 * ShapedRecipes) recipe = new CachedShapedRecipe((ShapedRecipes) irecipe);
	 * 
	 * if (recipe == null) continue;
	 * 
	 * recipe.computeVisuals(); arecipes.add(recipe); } } else {
	 * super.loadCraftingRecipes(outputId, results); } }
	 */

	public ItemStack createEnchantedBook(Enchantment ench, int level) {
		if (ench == null)
			return null;
		ItemStack book = new ItemStack(Item.enchantedBook);
		book.stackTagCompound = new NBTTagCompound();
		NBTTagList storedEnchs = new NBTTagList();
		NBTTagCompound enchanted = new NBTTagCompound();
		enchanted.setShort("id", (short) ench.effectId);
		enchanted.setShort("lvl", (short) level);
		storedEnchs.appendTag(enchanted);
		book.getTagCompound().setTag("StoredEnchantments", storedEnchs);
		return book;
	}

	public ItemStack createBookForEnhancement(EnhancementUtility ut) {
		if (ut == EnhancementUtility.SILKTOUCH)
			return createEnchantedBook(Enchantment.silkTouch, 1);
		else if (ut == EnhancementUtility.FORTUNE_1)
			return createEnchantedBook(Enchantment.fortune, 1);
		else if (ut == EnhancementUtility.FORTUNE_2)
			return createEnchantedBook(Enchantment.fortune, 2);
		else if (ut == EnhancementUtility.FORTUNE_3)
			return createEnchantedBook(Enchantment.fortune, 3);
		return null;
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		EnhancementUtility enhPresent = EnhancementUtility
				.readDrillEnhancement(result);
		if (enhPresent == null) {
			return;
		}
		ItemStack drill = new ItemStack(LSBlockItemList.itemEnhDiamondDrill);
		ItemStack enchantedBook = createBookForEnhancement(enhPresent);
		if (enchantedBook == null) {
			System.out
					.println("[DrillEnhancementHandler] (NEI support): enchantedBook is null! This must be a bug!");
			return;
		} else {
			arecipes.add(new CachedShapedRecipe(2, 2, new Object[] { drill, null, null, enchantedBook },
					enhPresent.createDrill()));
		}

	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
	}

	@Override
	public String getGuiTexture() {
		return "textures/gui/container/crafting_table.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "crafting";
	}

	public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
		return super.hasOverlay(gui, container, recipe) || isRecipe2x2(recipe)
				&& RecipeInfo.hasDefaultOverlay(gui, "crafting2x2");
	}

	@Override
	public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui,
			int recipe) {
		IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
		if (renderer != null)
			return renderer;

		IStackPositioner positioner = RecipeInfo.getStackPositioner(gui,
				"crafting2x2");
		if (positioner == null)
			return null;
		return new DefaultOverlayRenderer(getIngredientStacks(recipe),
				positioner);
	}

	@Override
	public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
		IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
		if (handler != null)
			return handler;

		return RecipeInfo.getOverlayHandler(gui, "crafting2x2");
	}

	public boolean isRecipe2x2(int recipe) {
		for (PositionedStack stack : getIngredientStacks(recipe))
			if (stack.relx > 43 || stack.rely > 24)
				return false;

		return true;
	}

}
