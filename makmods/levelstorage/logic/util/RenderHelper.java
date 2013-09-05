package makmods.levelstorage.logic.util;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.FMLClientHandler;

public class RenderHelper {
	public static void bindTexture(ResourceLocation resLoc) {
		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(resLoc);
	}
	
	public static void bindNoSlotsGUI() {
		bindTexture(ClientProxy.GUI_NO_SLOTS);
	}
	
	public static void bindSingleSlotGUI() {
		bindTexture(ClientProxy.GUI_SINGLE_SLOT);
	}
}