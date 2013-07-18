package makmods.levelstorage;

import makmods.levelstorage.api.XpRegistryAPI;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.packet.PacketHandler;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.proxy.GuiHandler;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		config.load();
		String itemXPBookId = "itemXPBookId";
		String blockXpGenId = "blockXpGenId";
		String blockXpChargerId = "blockXpChargerId";
		BlockItemIds.instance.addId(itemXPBookId,
				config.getItem(itemXPBookId, 2085).getInt());
		BlockItemIds.instance.addId(blockXpGenId,
				config.getBlock(blockXpGenId, 237).getInt());
		BlockItemIds.instance.addId(blockXpChargerId,
				config.getBlock(blockXpChargerId, 238).getInt());
		this.itemLevelStorageBookSpace = config.get(
				Configuration.CATEGORY_GENERAL, "XPBookCapacity", 16384)
				.getInt();
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
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
		XpStackRegistry.instance.initCriticalNodes();
		XpStackRegistry.instance.printRegistry();
		System.out.println(XpRegistryAPI.getConversions().toString());
	}

	public Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}
}
