package makmods.levelstorage.proxy;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModFluids;
import makmods.levelstorage.ModTileEntities;
import makmods.levelstorage.ModUniversalInitializer;
import makmods.levelstorage.armor.ArmorTicker;
import makmods.levelstorage.item.ItemCraftingIngredients;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	// public static final int WIRELESS_CHARGER_GUI_PLUS = 60;

	public static final int ARMOR_STORAGE = 8 * 1000 * 1000;
	public static final int ENH_LAPPACK_STORAGE = 2000000;

	// Special exceptional items (like cross-mod compatiblity) will land here
	// public static ItemUltimateWirelessAccessTerminal accessTerminal;

	public void addSimpleCraftingRecipes() {
		// Osmiridium alloy -> osmiridium plate
		ItemStack rec1 = ItemCraftingIngredients.instance.getIngredient(2);
		rec1.stackSize = 4;
		Recipes.compressor.addRecipe(rec1,
		        ItemCraftingIngredients.instance.getIngredient(3));
		// 4 tiny osmium dusts -> 1 dust
		GameRegistry.addRecipe(
		        ItemCraftingIngredients.instance.getIngredient(1), "SS", "SS",
		        Character.valueOf('S'),
		        ItemCraftingIngredients.instance.getIngredient(0));

		// Osmium dust -> osmium ingot
		ItemStack osmIngot = ItemCraftingIngredients.instance.getIngredient(4);
		ItemStack osmDust = ItemCraftingIngredients.instance.getIngredient(1);
		FurnaceRecipes.smelting().addSmelting(osmDust.itemID,
		        osmDust.getItemDamage(), osmIngot, 20.0F);

		// Osmium Ingots + Iridium Ingots = Osmiridium Alloy
		Recipes.advRecipes.addRecipe(
		        ItemCraftingIngredients.instance.getIngredient(2), "OOO",
		        "III", "   ", Character.valueOf('O'), "ingotOsmium",
		        Character.valueOf('I'), "ingotIridium");
		Recipes.advRecipes.addRecipe(
		        ItemCraftingIngredients.instance.getIngredient(2), "   ",
		        "OOO", "III", Character.valueOf('O'), "ingotOsmium",
		        Character.valueOf('I'), "ingotIridium");

		// Iridium Ore -> Iridium Ingot
		if (LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
		        "addIridiumOreToIngotCompressorRecipe", true).getBoolean(true)) {
			Recipes.compressor.addRecipe(Items.getItem("iridiumOre"),
			        ItemCraftingIngredients.instance.getIngredient(5));
		}

		// UUM -> Osmium pile
		GameRegistry.addRecipe(
		        ItemCraftingIngredients.instance.getIngredient(0), "U U",
		        "UUU", "U U", Character.valueOf('U'), Items.getItem("matter"));
	}

	public void init() {
		NetworkRegistry.instance().registerGuiHandler(LevelStorage.instance,
		        new GuiHandler());
		// TODO: mess around with this neat thingy
		MinecraftForge.EVENT_BUS.register(new LevelStorageEventHandler());
		ItemCraftingIngredients.instance = new ItemCraftingIngredients();
		addSimpleCraftingRecipes();
		//LSBlockItemList.init();
		//LSBlockItemList.init();
		ModUniversalInitializer.instance.init();
		ModTileEntities.instance.init();
		ModFluids.instance.init();
		// TODO: reenable when ready
		//ModAchievements.instance.init();
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
		TickRegistry.registerTickHandler(new ArmorTicker(), Side.SERVER);
	}

	public void postInit() {
		XpStackRegistry.instance.initCriticalNodes();
		XpStackRegistry.instance.printRegistry();

		LevelStorage.configuration.save();

		if (Loader.isModLoaded("gregtech_addon")) {
			FMLLog.info("GregTech detected. Performing needed changes. (mostly nerfs.. you know the drill)");
			LevelStorage.detectedGT = true;
			XpStackRegistry.UUM_XP_CONVERSION.setValue(1300);
		}
	}

	public void messagePlayer(EntityPlayer player, String message, Object[] args) {
		if ((player instanceof EntityPlayerMP)) {
			ChatMessageComponent msg;
			if (args.length > 0) {
				msg = ChatMessageComponent.func_111082_b(message, args);
			} else {
				msg = ChatMessageComponent.func_111077_e(message);
			}

			((EntityPlayerMP) player).sendChatToPlayer(msg);
		}
	}
}
