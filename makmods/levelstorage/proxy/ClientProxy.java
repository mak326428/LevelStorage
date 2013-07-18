package makmods.levelstorage.proxy;

import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

	public static final String BOOK_TEXTURE = Reference.MOD_ID.toLowerCase() + ":" + "itemLevelBook";
	public static final ResourceLocation GUI_SINGLE_SLOT = getResourceLocation("gui/singleSlot.png");
	public static final String XP_GEN_TEXTURE = Reference.MOD_ID.toLowerCase() + ":" + "blockXpGen";
	public static final String XP_CHARGER_TEXTURE = Reference.MOD_ID.toLowerCase() + ":" + "blockXpCharger";

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

	public void init() {
		super.init();
	}
}
