package makmods.levelstorage.registry;

import java.util.Map;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * I finally did it. Intended to create this class from beginning, but did it only today.
 * Superiorities over previous method:
 * 1. You have easier access over ALL the textures in the mod from any point of it (after initialization ofc)
 * 2. Cleaner code. Much cleaner.
 * @author mak326428
 */
public class BlockTextureRegistry {

	public static final BlockTextureRegistry instance = new BlockTextureRegistry();

	private Map<String, SimpleBlockTexture> textures = Maps.newHashMap();

	public void registerIcons(IconRegister iconRegister, String name) {
		Icon side = iconRegister.registerIcon(ClientProxy
		        .getTexturePathFor(name) + "Side");
		Icon up = iconRegister.registerIcon(ClientProxy.getTexturePathFor(name)
		        + "Up");
		Icon down = iconRegister.registerIcon(ClientProxy
		        .getTexturePathFor(name) + "Down");
		textures.put(name, new SimpleBlockTexture(up, down, side));
	}

	public SimpleBlockTexture getTextureFor(String name) {
		if (!textures.containsKey(name))
			return null;
		return textures.get(name);
	}

	public Icon getIcon(int side, String name) {
		if (!textures.containsKey(name))
			return null;
		ForgeDirection orientation = ForgeDirection.VALID_DIRECTIONS[side];
		SimpleBlockTexture t = textures.get(name);
		switch (orientation) {
			case UP:
				return t.upIcon;
			case DOWN:
				return t.downIcon;
			default:
				return t.sideIcon;
		}
	}

	public static class SimpleBlockTexture {
		private Icon sideIcon;
		private Icon upIcon;
		private Icon downIcon;

		public SimpleBlockTexture(Icon up, Icon down, Icon side) {
			this.sideIcon = side;
			this.downIcon = down;
			this.upIcon = up;
		}

		public SimpleBlockTexture() {
			sideIcon = null;
			downIcon = null;
			upIcon = null;
		}

		public Icon getSideIcon() {
			return sideIcon;
		}

		public void setSideIcon(Icon sideIcon) {
			this.sideIcon = sideIcon;
		}

		public Icon getUpIcon() {
			return upIcon;
		}

		public void setUpIcon(Icon upIcon) {
			this.upIcon = upIcon;
		}

		public Icon getDownIcon() {
			return downIcon;
		}

		public void setDownIcon(Icon downIcon) {
			this.downIcon = downIcon;
		}
	}

}
