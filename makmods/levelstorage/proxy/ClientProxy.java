package makmods.levelstorage.proxy;

import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.render.ItemWirelessConductorRender;
import makmods.levelstorage.render.WirelessConductorRender;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

	public static final String BOOK_TEXTURE = Reference.MOD_ID.toLowerCase()
			+ ":" + "itemLevelBook";
	// That's a pretty nice trick BTW, you can use textures from other mods
	public static final String ADV_SCANNER_TEXTURE = "ic2:itemScannerAdv";
	public static final String COMPACT_TELEPORTER_TEXTURE = Reference.MOD_ID
			.toLowerCase() + ":" + "itemCompactTeleporter";

	public static final String FREQUENCY_CARD_TEXTURE = Reference.MOD_ID
			.toLowerCase() + ":" + "itemFreqCard";
	public static final ResourceLocation GUI_SINGLE_SLOT = getResourceLocation("gui/singleSlot.png");
	public static final ResourceLocation GUI_CHARGER = getResourceLocation("gui/charger.png");
	public static final ResourceLocation GUI_CHARGER_NO_UUM = getResourceLocation("gui/chargeroutd.png");

	public static final ResourceLocation GUI_NO_SLOTS = getResourceLocation("gui/noSlots.png");
	public static final ResourceLocation CONDUCTOR_MODEL = getResourceLocation("model/WirelessConductorModel.png");

	public static final String XP_GEN_TEXTURE = Reference.MOD_ID.toLowerCase()
			+ ":" + "blockXpGen";
	public static final String XP_CHARGER_TEXTURE = Reference.MOD_ID
			.toLowerCase() + ":" + "blockXpCharger";
	public static final String WIRELESS_POWER_SYNC_TEXTURE = Reference.MOD_ID
			.toLowerCase() + ":" + "blockWirelessPSync";
	public static final String WIRELESS_CHARGER_TEXTURE = Reference.MOD_ID.toLowerCase()
			+ ":" + "itemWirelessCharger";
	public static final String TESLA_STAFF_TEXTURE = Reference.MOD_ID
			.toLowerCase() + ":" + "itemTeslaWeatherStaff";

	@SideOnly(Side.CLIENT)
	public static CreativeTabs getCreativeTab(String name) {
		for (CreativeTabs t : CreativeTabs.creativeTabArray) {
			if (t.getTabLabel() == name)
				return t;
		}
		return null;
	}

	public static ResourceLocation getResourceLocation(String path) {

		return new ResourceLocation(Reference.MOD_ID.toLowerCase(), path);
	}

	@Override
	public void init() {
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(
				TileEntityWirelessConductor.class,
				new WirelessConductorRender());
		MinecraftForgeClient.registerItemRenderer(
				ModBlocks.instance.blockWlessConductor.blockID,
				new ItemWirelessConductorRender());
	}

	@Override
	public void messagePlayer(EntityPlayer player, String message, Object[] args) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().addTranslatedMessage(
				message, args);
	}
}
