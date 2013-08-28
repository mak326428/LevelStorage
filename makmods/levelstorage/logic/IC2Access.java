package makmods.levelstorage.logic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import makmods.levelstorage.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

/**
 * Access for the IC2 features that are not in API.
 * Most of these are hacky and really inefficient ways of
 * doing it (reflection is a perfomance penalty), but
 * like said earlier, there is no other way.
 * @author mak326428
 *
 */
public class IC2Access {

	public static final IC2Access instance = new IC2Access();
	
	public static final String IC2_CLASSNAME = "ic2.core.IC2";
	public static final String IC2_KEYBOARD_CLASS = "ic2.core.util.Keyboard";
	public static final String IC2_KEYBOARD_FIELDNAME = "keyboard";

	private Class ic2_class;
	private Class ic2_keyboard;
	private Object ic2_keyboard_instance;

	private IC2Access() {
		try {
			ic2_class = Class.forName(IC2_CLASSNAME);
			ic2_keyboard = Class.forName(IC2_KEYBOARD_CLASS);
			ic2_keyboard_instance = getKeyboard();
		} catch (ClassNotFoundException e) {
			FMLLog.severe(Reference.MOD_NAME + ": failed to access IC2 classes");
			e.printStackTrace();
		}
	}

	private Object getKeyboard() {
		Field f;
		try {
			f = ic2_class.getDeclaredField(IC2_KEYBOARD_FIELDNAME);
			return f.get(null);
		} catch (Exception e) {
			FMLLog.severe(Reference.MOD_NAME
					+ ": failed to access IC2 keyboard");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Whether or not is IC2-key down?
	 * (This method uses reflection, don't use it too much)
	 * @param keyName Name of the key (use JD-Gui to get names of them)
	 * Examples:
	 * "Jump"
	 * "Boost"
	 * "ModeSwitch"
	 * Case of the name <b>DOES</b> matter here.
	 * @param ep Player
	 * @return Whether or not IC2-key is pushed.
	 */
	public boolean isKeyDown(String keyName, EntityPlayer ep) {
		if (ic2_keyboard != null && ic2_keyboard_instance != null) {
			String methodName = "is" + keyName + "KeyDown";
			try {
				Method m = ic2_keyboard.getMethod(methodName,
						EntityPlayer.class);
				return ((Boolean) m.invoke(ic2_keyboard_instance, ep));
			} catch (Exception e) {
				FMLLog.severe(Reference.MOD_NAME
						+ ": failed to access IC2 keyboard");
				e.printStackTrace();
			}

		}
		return false;
	}

}
