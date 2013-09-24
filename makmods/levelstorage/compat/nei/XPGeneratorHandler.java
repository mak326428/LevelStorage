package makmods.levelstorage.compat.nei;

import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.gui.client.GuiXpGenerator;
import makmods.levelstorage.registry.XPStackRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class XPGeneratorHandler extends TemplateRecipeHandler {

	public class CachedXPGenRecipe extends CachedRecipe {

		public XPStack stack;

		public CachedXPGenRecipe(XPStack stack) {
			this.stack = stack;
		}

		@Override
		public PositionedStack getResult() {
			return new PositionedStack(stack.stack, 75, 24);
		}

	}

	@Override
	public String getRecipeName() {
		return "XP Generator";
	}

	@Override
	public String getGuiTexture() {
		return "textures/gui/xpgen_nei.png";
	}

	public Class<? extends GuiContainer> getGuiClass() {
		return GuiXpGenerator.class;
	}

	@Override
	public String getOverlayIdentifier() {
		return "xpgen";
	}

	@Override
	public void drawExtras(int recipe) {
		CachedXPGenRecipe rec = ((CachedXPGenRecipe) arecipes.get(recipe));
		int eu = rec.stack.value * XPStackRegistry.XP_EU_CONVERSION.getValue() / XPStackRegistry.XP_EU_CONVERSION.getKey();
		int baseX = 88;
		int baseY = 75;
		String str = String.format("Produces %d EU", eu);
		FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		renderer.drawString(str, baseX - renderer.getStringWidth(str) / 2, baseY, 0xFF404040);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (XPStack stack : XPStackRegistry.instance.entries) {
			CachedXPGenRecipe recipe = new CachedXPGenRecipe(stack);

			if (stack.stack.itemID == ingredient.itemID
			        && stack.stack.getItemDamage() == ingredient
			                .getItemDamage()) {
				arecipes.add(recipe);
			}
		}
	}
}
