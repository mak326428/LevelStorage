package makmods.levelstorage;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.packet.PacketHandler;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.proxy.GuiHandler;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
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
	public static int currentIds = 250;
	
	public static boolean detectedGT = false;

	public static int getAndIncrementCurrId() {
		currentIds += 1;
		return currentIds;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Pre-Initialization...");
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		configuration = config;
		configuration.load();
		this.itemLevelStorageBookSpace = config.get(
				Configuration.CATEGORY_GENERAL, "bookCapacity",
				2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2) // 16384
				.getInt();
		Property p = config.get(Configuration.CATEGORY_GENERAL, "chargerOnlyUsesUUM", true);
		p.comment = "If set to true, chargers will consume UUM and only UUM (they will refuse to receive any energy), if set to false, chargers will receive energy and only energy (no UUM)";
		this.chargerOnlyUUM = p.getBoolean(true);
		
		Property p2 = config.get(Configuration.CATEGORY_GENERAL, "experienceRecipesEnabled", true);
		p2.comment = "Whether or not experience recipes are enabled";
		this.experienceRecipesOn = p2.getBoolean(true);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		WirelessPowerSynchronizerRegistry.instance.registry.clear();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		WirelessPowerSynchronizerRegistry.instance.registry.clear();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Initialization...");
		NetworkRegistry.instance().registerGuiHandler(instance,
				new GuiHandler());
		// TODO: mess around with this neat thingy
		MinecraftForge.EVENT_BUS.register(new LevelStorageEventHandler());
		ModBlocks.instance.init();
		ModItems.instance.init();
		ModTileEntities.instance.init();
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		FMLLog.info(Reference.MOD_NAME + ": Post-Initialization...");
		XpStackRegistry.instance.initCriticalNodes();
		XpStackRegistry.instance.printRegistry();

		configuration.save();
		
		if (Loader.isModLoaded("gregtech_addon")) {
			FMLLog.info("GregTech detected. Performing needed changes.");
			detectedGT = true;
			XpStackRegistry.UUM_XP_CONVERSION.setValue(1300);
		}
	}

	public static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}
}
