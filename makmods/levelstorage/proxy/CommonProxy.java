package makmods.levelstorage.proxy;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.ModFluids;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.ModTileEntities;
import makmods.levelstorage.armor.ArmorTicker;
import makmods.levelstorage.logic.Helper;
import makmods.levelstorage.logic.LevelStorageEventHandler;
import makmods.levelstorage.logic.uumsystem.UUMHelper;
import makmods.levelstorage.logic.uumsystem.UUMRecipeParser;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	public static final int WIRELESS_CHARGER_GUI_PLUS = 60;

	public static final int ARMOR_STORAGE = 8 * 1000 * 1000;

	// Special exceptional items (like cross-mod compatiblity) will land here
	// public static ItemUltimateWirelessAccessTerminal accessTerminal;

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
		TickRegistry.registerTickHandler(new ArmorTicker(), Side.SERVER);
		/*
		 * if (Loader.isModLoaded("AppliedEnergistics")) {
		 * FMLLog.info("Applied Energistics detected. Enabling compatibility.");
		 * accessTerminal = new ItemUltimateWirelessAccessTerminal();
		 * LanguageRegistry.addName(accessTerminal,
		 * ItemUltimateWirelessAccessTerminal.NAME);
		 * accessTerminal.addCraftingRecipe(); try { if
		 * (Util.getWirelessTermRegistery() != null) {
		 * Util.getWirelessTermRegistery().registerWirelessHandler(
		 * accessTerminal); } else { throw new
		 * Exception("failed to access registry, it's null"); } } catch
		 * (Exception e) { for (;;) FMLLog.severe(
		 * "Something went very wrong when tried to access AE's wireless terminal registry"
		 * ); //e.printStackTrace(); } }
		 */
		// res = UUMHelper.removeDuplicates(res);
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
		ItemStack[] inputs = UUMHelper.getUUMRecipe(new ItemStack(
		        Item.redstone, 24));
		System.out.println(inputs.length);
		for (ItemStack input : inputs) {
			System.out.println(Helper.getNiceStackName(input));
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
