package makmods.levelstorage.init;

import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.registry.XPStackRegistry;
import net.minecraft.item.ItemStack;

public class LSIMCHandler {

	public static final LSIMCHandler instance = new LSIMCHandler();

	private LSIMCHandler() {
		;
	}

	public static class LSIMCException extends Exception {
		public LSIMCException(String message) {
			super(message);
		}
	}

	public void handle(String key, String value) throws LSIMCException {
		try {
			if (key == "add-xp") {
				String[] stackAndValue = value.split(",");
				int stackValue = Integer.parseInt(stackAndValue[1]);
				String[] idAndMeta = stackAndValue[0].split(":");
				int id = Integer.parseInt(idAndMeta[0]);
				int meta = Integer.parseInt(idAndMeta[1]);
				XPStackRegistry.instance.pushToRegistry(new XPStack(
						new ItemStack(id, 1, meta), stackValue));
			}
		} catch (Exception e) {
			throw new LSIMCException("Unknown error.");
		}
	}
}
