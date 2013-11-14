package makmods.levelstorage.proxy;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.VersionChecker;
import makmods.levelstorage.dimension.AntimatterUniverseRayHandler;
import makmods.levelstorage.dimension.BiomeAntimatterField;
import makmods.levelstorage.dimension.LSDimensions;
import makmods.levelstorage.init.CompatibilityInitializer;
import makmods.levelstorage.init.Config;
import makmods.levelstorage.init.LSFluids;
import makmods.levelstorage.init.LocalizationInitializer;
import makmods.levelstorage.init.ModAchievements;
import makmods.levelstorage.init.ModTileEntities;
import makmods.levelstorage.init.ModUniversalInitializer;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.registry.FlightRegistry;
import makmods.levelstorage.registry.XPStackRegistry;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer.PowerSyncRegistry;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer.WChargerRegistry;
import makmods.levelstorage.worldgen.LSWorldGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	// public static final int WIRELESS_CHARGER_GUI_PLUS = 60;

	public static final int ARMOR_STORAGE = 80 * 1000 * 1000;
	public static final int ENH_LAPPACK_STORAGE = 2000000;
	public static int ARMOR_SUPERSONIC_RENDER_INDEX;
	public static int ARMOR_ENHANCED_LAPPACK_RENDER_INDEX;
	// Sorry for the dirty code, server didn't start without this
	public static final String SUPERSONIC_DUMMY = "supersonic";
	public static final String ANTIMATTER_DUMMY = "antimatter";

	public static final String ENH_LAPPACK_DUMMY = "enhlapp";

	public static BiomeAntimatterField biomeAntimatterField;

	// Special exceptional items (like cross-mod compatiblity) will land here
	// public static ItemUltimateWirelessAccessTerminal accessTerminal;

	public int getArmorIndexFor(String forWhat) {
		return 0;
	}

	public void preInit() {
		VersionChecker.checkVersion();
	}

	public void init() {
		NetworkRegistry.instance().registerGuiHandler(LevelStorage.instance,
				new LSGUIHandler());
		MinecraftForge.EVENT_BUS.register(new LevelStorageEventHandler());
		LocalizationInitializer.instance.init();
		SimpleItems.instance = new SimpleItems();
		// LSBlockItemList.init();
		// LSBlockItemList.init();
		ModUniversalInitializer.instance.init();
		SimpleRecipeAdder.addRecipesAfterMUIFinished();
		ModTileEntities.instance.init();
		LSFluids.instance.init();
		LSDimensions.init();
		biomeAntimatterField = new BiomeAntimatterField(
				LevelStorage.configuration.get("dimension",
						"biomeAntimatterFieldId", 40).getInt());
		PowerSyncRegistry.instance = new PowerSyncRegistry();
		WChargerRegistry.instance = new WChargerRegistry();
		LSWorldGenerator.instance = new LSWorldGenerator();
		GameRegistry.registerWorldGenerator(LSWorldGenerator.instance);
		//
		FlightRegistry.instance = new FlightRegistry();
		TickRegistry.registerTickHandler(new AntimatterUniverseRayHandler(),
				Side.SERVER);
		SimpleRecipeAdder.addSimpleCraftingRecipes();
	}

	public void postInit() {
		XPStackRegistry.instance.initCriticalNodes();
		// XPStackRegistry.instance.printRegistry();
		ModAchievements.instance.init();
		IVRegistry.instance.init();
		LevelStorage.configuration.save();
		Config.ACTIVE = false;
		CompatibilityInitializer.instance.init();
		// TODO: move this to an external compat class
		if (Loader.isModLoaded("gregtech_addon")) {
			LogHelper
					.severe("GregTech detected. Performing needed changes. (mostly nerfs.. you know the drill)");
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
