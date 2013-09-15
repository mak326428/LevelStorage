package makmods.levelstorage.proxy;

import ic2.api.item.Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModAchievements;
import makmods.levelstorage.ModFluids;
import makmods.levelstorage.dimension.BiomeAntimatterField;
import makmods.levelstorage.dimension.LSDimensions;
import makmods.levelstorage.init.CompatibilityInitializer;
import makmods.levelstorage.init.LocalizationInitializer;
import makmods.levelstorage.init.ModTileEntities;
import makmods.levelstorage.init.ModUniversalInitializer;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.registry.FlightRegistry;
import makmods.levelstorage.registry.XPStackRegistry;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer.PowerSyncRegistry;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer.WChargerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	// public static final int WIRELESS_CHARGER_GUI_PLUS = 60;

	public static final int ARMOR_STORAGE = 40 * 1000 * 1000;
	public static final int ENH_LAPPACK_STORAGE = 2000000;
	public static int ARMOR_SUPERSONIC_RENDER_INDEX;
	public static int ARMOR_ENHANCED_LAPPACK_RENDER_INDEX;
	// Sorry for the dirty code, server didn't start without this
	public static final String SUPERSONIC_DUMMY = "supersonic";
	public static final String ENH_LAPPACK_DUMMY = "enhlapp";

	public static BiomeAntimatterField biomeAntimatterField;

	// Special exceptional items (like cross-mod compatiblity) will land here
	// public static ItemUltimateWirelessAccessTerminal accessTerminal;

	public void addSimpleCraftingRecipes() {
		// Osmiridium alloy -> osmiridium plate
		ItemStack rec1 = SimpleItems.instance.getIngredient(2);
		rec1.stackSize = 4;
		Recipes.compressor.addRecipe(new RecipeInputOreDict(
				"itemOsmiridiumAlloy"), null, SimpleItems.instance
				.getIngredient(3));
		// 4 tiny osmium dusts -> 1 dust
		GameRegistry.addRecipe(SimpleItems.instance.getIngredient(1), "SS",
				"SS", Character.valueOf('S'),
				SimpleItems.instance.getIngredient(0));

		// Osmium dust -> osmium ingot
		ItemStack osmIngot = SimpleItems.instance.getIngredient(4);
		ItemStack osmDust = SimpleItems.instance.getIngredient(1);
		FurnaceRecipes.smelting().addSmelting(osmDust.itemID,
				osmDust.getItemDamage(), osmIngot, 20.0F);

		// Osmium Ingots + Iridium Ingots = Osmiridium Alloy
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(2),
				"OOO", "III", "   ", Character.valueOf('O'), "ingotOsmium",
				Character.valueOf('I'), "ingotIridium");
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(2),
				"   ", "OOO", "III", Character.valueOf('O'), "ingotOsmium",
				Character.valueOf('I'), "ingotIridium");

		// Iridium Ore -> Iridium Ingot
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"addIridiumOreToIngotCompressorRecipe", true).getBoolean(true)) {
			Recipes.compressor.addRecipe(
					new RecipeInputItemStack(Items.getItem("iridiumOre")),
					null, SimpleItems.instance.getIngredient(5));
		}

		// UUM -> Osmium pile
		GameRegistry.addRecipe(SimpleItems.instance.getIngredient(0), "U U",
				"UUU", "U U", Character.valueOf('U'), Items.getItem("matter"));

		Recipes.advRecipes.addRecipe(
				OreDictionary.getOres("itemAntimatterTinyPile").get(0), "ppp",
				"ppp", "ppp", Character.valueOf('p'), "itemAntimatterMolecule");
		Recipes.advRecipes.addRecipe(OreDictionary
				.getOres("itemAntimatterGlob").get(0), "ppp", "ppp", "ppp",
				Character.valueOf('p'), "itemAntimatterTinyPile");
		Recipes.advRecipes.addRecipe(SimpleItems.instance.getIngredient(7)
				.copy(), " a ", "ana", " a ", Character.valueOf('a'),
				SimpleItems.instance.getIngredient(10).copy(), Character
						.valueOf('n'), new ItemStack(Item.netherStar).copy());

	}

	public int getArmorIndexFor(String forWhat) {
		return 0;
	}

	public void init() {
		NetworkRegistry.instance().registerGuiHandler(LevelStorage.instance,
				new GUIHandler());
		// TODO: mess around with this neat thingy
		MinecraftForge.EVENT_BUS.register(new LevelStorageEventHandler());
		LocalizationInitializer.instance.init();
		SimpleItems.instance = new SimpleItems();
		addSimpleCraftingRecipes();
		// LSBlockItemList.init();
		// LSBlockItemList.init();
		ModUniversalInitializer.instance.init();
		ModTileEntities.instance.init();
		ModFluids.instance.init();
		LSDimensions.init();
		biomeAntimatterField = new BiomeAntimatterField(
				LevelStorage.configuration.get("dimension",
						"biomeAntimatterFieldId", 40).getInt());
		PowerSyncRegistry.instance = new PowerSyncRegistry();
		WChargerRegistry.instance = new WChargerRegistry();
		// TODO: reenable when ready
		// 
		FlightRegistry.instance = new FlightRegistry();
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	public void postInit() {
		XPStackRegistry.instance.initCriticalNodes();
		XPStackRegistry.instance.printRegistry();
		ModAchievements.instance.init();
		LevelStorage.configuration.save();
		CompatibilityInitializer.instance.init();
		// TODO: move this to an external compat class
		if (Loader.isModLoaded("gregtech_addon")) {
			LogHelper.severe("GregTech detected. Performing needed changes. (mostly nerfs.. you know the drill)");
			LevelStorage.detectedGT = true;
			XPStackRegistry.UUM_XP_CONVERSION.setValue(1300);
		}
		// LSBlockItemList.itemCapFluidCell.fillMetaListWithFluids();
	}

	public void messagePlayer(EntityPlayer player, String message, Object[] args) {
		if ((player instanceof EntityPlayerMP)) {
			ChatMessageComponent msg = ChatMessageComponent
					.createFromText(message);

			((EntityPlayerMP) player).sendChatToPlayer(msg);
		}
	}
}
