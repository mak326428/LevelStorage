package makmods.levelstorage.proxy;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.ModFluids;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.ModTileEntities;
import makmods.levelstorage.armor.ArmorTicker;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.logic.uumsystem.UUMRecipeParser;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public static final int WIRELESS_CHARGER_GUI_PLUS = 60;

	public void init() {
		NetworkRegistry.instance().registerGuiHandler(LevelStorage.instance,
				new GuiHandler());
		// TODO: mess around with this neat thingy
		MinecraftForge.EVENT_BUS.register(new LevelStorageEventHandler());
		ModBlocks.instance.init();
		ModItems.instance.init();
		ModTileEntities.instance.init();
		ModFluids.instance.init();
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
		TickRegistry.registerTickHandler(new ArmorTicker(), Side.CLIENT);
		//res = UUMHelper.removeDuplicates(res);
	}

	public void postInit() {
		XpStackRegistry.instance.initCriticalNodes();
		XpStackRegistry.instance.printRegistry();

		LevelStorage.configuration.save();

		if (Loader.isModLoaded("gregtech_addon")) {
			FMLLog.info("GregTech detected. Performing needed changes.");
			LevelStorage.detectedGT = true;
			XpStackRegistry.UUM_XP_CONVERSION.setValue(1300);
		}
		UUMRecipeParser.instance.init();
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
