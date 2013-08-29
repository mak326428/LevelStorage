package makmods.levelstorage;

import makmods.levelstorage.api.ItemAPI.SimpleItemAPI;
import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorSupersonicLeggings;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.Helper;
import makmods.levelstorage.network.PacketHandler;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.registry.FlightRegistry;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

//@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "Forge@[9.10.0.804,);required-after:IC2")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:IC2@[1.118.401-lf,)")
@NetworkMod(channels = { Reference.MOD_ID }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class LevelStorage {

	@Instance(Reference.MOD_ID)
	public static LevelStorage instance;

	@SidedProxy(clientSide = "makmods.levelstorage.proxy.ClientProxy", serverSide = "makmods.levelstorage.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static int itemLevelStorageBookSpace;
	public static Configuration configuration;
	public static boolean chargerOnlyUUM;
	public static boolean experienceRecipesOn;
	public static int currentIds = 450;
	public static boolean fancyGraphics;
	public static boolean recipesHardmode = false;
	public static final String BALANCE_CATEGORY = "balance";
	public static final String RECIPES_CATEGORY = "recipes";
	public static final String IDS_CATEGORY = "ids";
	
	public static boolean detectedGT = false;

	public static int getAndIncrementCurrId() {
		currentIds += 1;
		return currentIds;
	}

	public static void logFailure(String text) {
		FMLLog.severe(Reference.MOD_NAME + ": " + text);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Pre-Initialization...");
		Configuration config = new Configuration(
		        event.getSuggestedConfigurationFile());
		configuration = config;
		configuration.load();
		LevelStorage.itemLevelStorageBookSpace = config.get(
		        Configuration.CATEGORY_GENERAL, "bookCapacity",
		        2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2) // 16384
		        .getInt();
		Property p = config.get(Configuration.CATEGORY_GENERAL,
		        "chargerOnlyUsesUUM", true);
		p.comment = "If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM)";
		LevelStorage.chargerOnlyUUM = p.getBoolean(true);

		Property p2 = config.get(Configuration.CATEGORY_GENERAL,
		        "experienceRecipesEnabled", true);
		p2.comment = "Whether or not experience recipes are enabled";
		LevelStorage.experienceRecipesOn = p2.getBoolean(true);

		Property p3 = config.get(Configuration.CATEGORY_GENERAL,
		        "fancyGraphics", false);
		p3.comment = "Whether or not fancy graphics for various energy rays are enabled";
		LevelStorage.fancyGraphics = p3.getBoolean(true);

		Property p4 = config.get(Configuration.CATEGORY_GENERAL, "hardRecipes",
		        false);
		p4.comment = "If set to true, armors (and other) will require hard-to-get materials (f.e. full set of armor will require 72 stacks of UUM)";
		LevelStorage.recipesHardmode = p4.getBoolean(false);
	}

	public static boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		// Clearing all the stuff, so we don't occasionally do bad things
		// like wasting energy for nothing.
		WirelessPowerSynchronizerRegistry.instance.registry.clear();
		WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		WirelessPowerSynchronizerRegistry.instance.registry.clear();
		WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Initialization...");
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Post-Initialization...");
		proxy.postInit();
		ItemStack stack = SimpleItemAPI.getSimpleItem("ingotIridium");
		System.out.println(Helper.getNiceStackName(stack));
	}

	public static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}
}
