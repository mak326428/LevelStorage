package makmods.levelstorage;

import java.util.List;
import java.util.logging.Logger;

import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.command.CommandChargeItems;
import makmods.levelstorage.init.LSIMCHandler;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.network.PacketHandler;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.proxy.LSKeyboard;
import makmods.levelstorage.registry.FlightRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

//@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "Forge@[9.10.0.804,);required-after:IC2")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:IC2@[2.0,)")
@NetworkMod(channels = { Reference.MOD_ID, LSKeyboard.PACKET_KEYBOARD_CHANNEL,
		Reference.CUSTOM_PACKET_CHANNEL }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class LevelStorage {

	@Instance(Reference.MOD_ID)
	public static LevelStorage instance;

	@SidedProxy(clientSide = "makmods.levelstorage.proxy.ClientProxy", serverSide = "makmods.levelstorage.proxy.CommonProxy")
	public static CommonProxy proxy;

	@SidedProxy(clientSide = "makmods.levelstorage.proxy.LSKeyboardClient", serverSide = "makmods.levelstorage.proxy.LSKeyboard")
	public static LSKeyboard keyboard;

	public static int itemLevelStorageBookSpace;
	public static Configuration configuration;
	public static boolean chargerOnlyUUM;
	public static boolean experienceRecipesOn;
	public static boolean fancyGraphics;
	public static boolean recipesHardmode = false;
	public static final String BALANCE_CATEGORY = "balance";
	public static final String RECIPES_CATEGORY = "recipes";
	public static final String PERFORMANCE_CATEGORY = "performance";
	
	public static final String STORAGE_CATEGORY = "electricitemstorage";
	public static final String IDS_CATEGORY = "ids";
	public static Logger logger;
	public static float powerExplosionAntimatterBomb;
	private long initTimeMeter;

	public static boolean detectedGT = false;

	public static boolean isAnySolarModLoaded() {
		return Loader.isModLoaded("AdvancedSolarPanel") || detectedGT;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		initTimeMeter = System.currentTimeMillis();
		LogHelper.info("Pre-Initialization...");
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		configuration = config;
		configuration.load();
		LevelStorage.itemLevelStorageBookSpace = config.get(
				Configuration.CATEGORY_GENERAL, "bookCapacity",
				2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2) // 16384
				.getInt();
		Property p = config.get(LevelStorage.BALANCE_CATEGORY,
				"chargerOnlyUsesUUM", true);
		p.comment = "If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM)";
		LevelStorage.chargerOnlyUUM = p.getBoolean(true);

		Property p2 = config.get(LevelStorage.BALANCE_CATEGORY,
				"experienceRecipesEnabled", true);
		p2.comment = "Whether or not experience recipes are enabled";
		LevelStorage.experienceRecipesOn = p2.getBoolean(true);
		Property p4 = config.get(LevelStorage.BALANCE_CATEGORY, "hardRecipes",
				false);
		p4.comment = "If set to true, armors (and other) will require hard-to-get materials (f.e. full set of armor will require 72 stacks of UUM)";
		LevelStorage.recipesHardmode = p4.getBoolean(false);

		Property p5 = config.get(LevelStorage.BALANCE_CATEGORY,
				"explosionPowerAntimatterBomb", false);
		p5.comment = "Explosion power of Antimatter Bomb, where TNT is 4";
		LevelStorage.powerExplosionAntimatterBomb = p5.getInt(100);
	}

	public static boolean isSimulating() {
		return !FMLCommonHandler.instance().getEffectiveSide().isClient();
	}
	
	public static boolean isRendering() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		// Clearing all the stuff, so we don't occasionally do bad things
		// like wasting energy for nothing.
		// WirelessPowerSynchronizerRegistry.instance.registry.clear();
		// WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();
		((ServerCommandManager) MinecraftServer.getServer().getCommandManager())
				.registerCommand(new CommandChargeItems());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		// WirelessPowerSynchronizerRegistry.instance.registry.clear();
		// WirelessPowerSynchronizerRegistry.instance.registryChargers.clear();
		ArmorFunctions.speedTickerMap.clear();
		ArmorFunctions.onGroundMap.clear();
		FlightRegistry.instance.modEnabledFlights.clear();

	}

	@EventHandler
	public void onInterModComms(IMCEvent event) {
		List<IMCMessage> messages = event.getMessages();
		for (IMCMessage message : messages) {
			try {
				if (message.isStringMessage()) {
					String value = message.getStringValue();
					String key = message.key;
					LSIMCHandler.instance.handle(key, value);
				} else {
					throw new LSIMCHandler.LSIMCException(
							"Value must be string!");
				}
			} catch (Exception e) {
				LogHelper.warning("Mod " + message.getSender()
						+ " sent an invalid FMLInterModComms message.");
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		LogHelper.info("Initialization...");
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LogHelper.info("Post-Initialization...");
		proxy.postInit();
		LogHelper.info("Initialization took "
				+ (System.currentTimeMillis() - initTimeMeter) + " ms.");
		System.out.println(CommonHelper.getDistanceFloor(0, 0, 1, 2, 3, 2));
	}

	public static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}
}
