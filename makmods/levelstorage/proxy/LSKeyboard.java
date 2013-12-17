package makmods.levelstorage.proxy;

import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.LevelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.Maps;

public class LSKeyboard {
	
	public Map<EntityPlayer, Map<String, Boolean>> keys = Maps.newHashMap();
	
	public static final String PACKET_KEYBOARD_CHANNEL = "lskeyboard";
	
	public static final String RANGE_KEY_NAME = "range";
	public static final String RAY_SHOOT_KEY_NAME = "rayShoot";
	public static final String JETPACK_SWITCH_KEY_NAME = "jetpackSwitch";
	public static final String ANTIMATTER_BOOTS_SPECIAL_FLIGHT = "antimatterBoots";
	
	public LSKeyboard() {
		;
	}
	
	public static LSKeyboard getInstance() {
		return LevelStorage.keyboard;
	}
	
	public void handleKeyChangeServer(EntityPlayerMP player, String keyName, boolean active) {
		if (!keys.containsKey(player)) {
			Map<String, Boolean> dummyMap = Maps.newHashMap();
			dummyMap.put(keyName, active);
			keys.put(player, dummyMap);
		} else {
			Map<String, Boolean> currPlayerKeys = keys.get(player);
			if (!currPlayerKeys.containsKey(keyName)) {
				currPlayerKeys.put(keyName, active);
			} else {
				currPlayerKeys.remove(keyName);
				currPlayerKeys.put(keyName, active);
			}
			keys.remove(player);
			keys.put(player, currPlayerKeys);
		}
	}
	
	public void printKeys() {
		for (Entry<EntityPlayer, Map<String, Boolean>> entry : keys.entrySet()) {
			System.out.println();
			System.out.println("Player: " + entry.getKey().username);
			for (Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
				System.out.println("    Key");
				System.out.println("        Name: " + entry2.getKey());
				System.out.println("        Active: " + entry2.getValue());
			}
			System.out.println();
		}
	}
	
	public boolean isKeyDown(EntityPlayer player, String keyName) {
		if (!keys.containsKey(player))
			return false;
		Map<String, Boolean> currentKeys = keys.get(player);
		if (!currentKeys.containsKey(keyName))
			return false;
		return currentKeys.get(keyName);
	}
}
