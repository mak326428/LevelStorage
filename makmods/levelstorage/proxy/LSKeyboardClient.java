package makmods.levelstorage.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class LSKeyboardClient extends LSKeyboard implements ITickHandler {

	public Map<String, Boolean> keyStatus = Maps.newHashMap();

	public Map<String, KeyBinding> modBindings = Maps.newHashMap();

	public LSKeyboardClient() {

		modBindings.put(RANGE_KEY_NAME,
		        new KeyBinding(StatCollector.translateToLocal("key.range"),
		                Keyboard.KEY_V));
		modBindings.put(RAY_SHOOT_KEY_NAME,
		        new KeyBinding(StatCollector.translateToLocal("key.shoot"),
		                Keyboard.KEY_R));
		modBindings.put(JETPACK_SWITCH_KEY_NAME, new KeyBinding(StatCollector.translateToLocal("key.jetpackSwitch"),
		                Keyboard.KEY_F));

		KeyBinding[] bsInternal = (KeyBinding[]) modBindings.values().toArray(
		        new KeyBinding[modBindings.values().size()]);
		KeyBindingRegistry
		        .registerKeyBinding(new KeyBindingRegistry.KeyHandler(
		                bsInternal) {
			        public String getLabel() {
				        return "LevelStorageKeyHandler";
			        }

			        public EnumSet<TickType> ticks() {
				        return EnumSet.of(TickType.CLIENT);
			        }

			        public void keyUp(EnumSet<TickType> types, KeyBinding kb,
			                boolean tickEnd) {
			        }

			        public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			                boolean tickEnd, boolean isRepeat) {
			        }
		        });
		TickRegistry.registerTickHandler(this, Side.CLIENT);
	}

	public void initiateKeyChange(String id, EntityPlayer player,
	        boolean isActive) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
	        dos.writeUTF(id);
	        dos.writeBoolean(isActive);
        } catch (IOException e) {
	        e.printStackTrace();
        }
		byte[] data = bos.toByteArray();
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = LSKeyboard.PACKET_KEYBOARD_CHANNEL;
		packet.isChunkDataPacket = false;
		packet.data = data;
		packet.length = data.length;
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (!(tickData[0] instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) tickData[0];
		for (Entry<String, KeyBinding> entry : modBindings.entrySet()) {
			boolean isActive = entry.getValue().pressed;
			if (!keyStatus.containsKey(entry.getKey())) {
				keyStatus.put(entry.getKey(), isActive);
				initiateKeyChange(entry.getKey(), player, isActive);
			} else {
				if (keyStatus.get(entry.getKey()) != isActive) {
					keyStatus.remove(entry.getKey());
					keyStatus.put(entry.getKey(), isActive);
					initiateKeyChange(entry.getKey(), player, isActive);
				}
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "LevelStorageKeyboard";
	}
}
