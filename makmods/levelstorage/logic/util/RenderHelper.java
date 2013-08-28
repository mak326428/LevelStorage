package makmods.levelstorage.logic.util;

import cpw.mods.fml.client.FMLClientHandler;
import makmods.levelstorage.proxy.ClientProxy;

public class RenderHelper {
	public static void bindTexture(String path) {
		FMLClientHandler.instance().getClient().renderEngine
		.func_110577_a(ClientProxy.getResourceLocation(path));
	}
}
