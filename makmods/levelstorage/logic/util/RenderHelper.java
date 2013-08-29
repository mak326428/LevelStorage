package makmods.levelstorage.logic.util;

import makmods.levelstorage.proxy.ClientProxy;
import cpw.mods.fml.client.FMLClientHandler;

public class RenderHelper {
	public static void bindTexture(String path) {
		FMLClientHandler.instance().getClient().renderEngine
		        .func_110577_a(ClientProxy.getResourceLocation(path));
	}
}
