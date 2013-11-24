package makmods.levelstorage.item;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSuperconductor extends Item implements IHasRecipe {

	public ItemSuperconductor(int id) {
		super(id);

		this.setNoRepair();
		this.setMaxStackSize(16);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		Property pEnable = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"superConductorOreDictCompatible", true);
		pEnable.comment = "Determines whether or not LevelStorage's superconductors are oredict-compatible with other superconductors (e.g. GraviSuite or GregTech)";
		if (pEnable.getBoolean(true))
			OreDictionary.registerOre("itemSuperconductor", this);
	}

	public void addCraftingRecipe() {
		Property pOutput = LevelStorage.configuration
				.get(Configuration.CATEGORY_GENERAL,
						"superConductorRecipeOutput", 6);
		pOutput.comment = "Determines how much superconductors you get from one recipe";

		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemSuperconductor, pOutput.getInt(6)), "ccc",
				"iai", "ccc", Character.valueOf('c'), Items
						.getItem("glassFiberCableItem"),
				Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
						.valueOf('a'), Items.getItem("advancedMachine"));

	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ITEM_SUPERCONDUCTOR_TEXTURE);
	}
}
