package makmods.levelstorage.logic.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public final class StackHelper {
	
	public static final String CLASSNAME = "ic2.core.util.StackUtil";
	private static Class utilClass;
	
	private static Method m_distrDrop;
	
	static {
		try {
	        utilClass = Class.forName(CLASSNAME);
	        m_distrDrop = utilClass.getMethod("distributeDrop", TileEntity.class, List.class);
        } catch (Exception e) {
        	FMLLog.severe("Something went wrong when tried to access IC2 Utils");
	        e.printStackTrace();
        }
	}
	
	public static void distributeDrop(TileEntity source, List<ItemStack> drops) {
		try {
	        m_distrDrop.invoke(null, source, drops);
        } catch (Exception e) {
	        FMLLog.severe("Something went wrong when tried to call distributeDrop()");
	        e.printStackTrace();
        }
	}
	
}