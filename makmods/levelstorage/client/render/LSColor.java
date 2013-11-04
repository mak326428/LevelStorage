package makmods.levelstorage.client.render;

import org.lwjgl.opengl.GL11;

public class LSColor {

	public enum StandartColor {
		BLACK(0x000000), DARK_BLUE(0x00002A), DARK_GREEN(0x002A00), DARK_AQUA(
				0x002A2A), DARK_RED(0x2A0000), DARK_PURPLE(0x2A002A), GOLD(
				0x2A2A00), GRAY(0x2A2A2A), DARK_GRAY(0x151515), BLUE(0x15153F), GREEN(
				0x153F15), AQUA(0x153F3F), RED(0x3F1515), LIGHT_PURPLE(0x3F153F), YELLOW(
				0x3F3F15), WHITE(0x3F3F3F);

		private final String name;
		private final LSColor actualColor;

		private StandartColor(String name, LSColor clr) {
			this.name = name;
			this.actualColor = clr;
		}

		private StandartColor(int hex) {
			this(LSColor.fromHex(hex));
		}

		private StandartColor(LSColor clr) {
			this.name = this.name().toLowerCase();
			this.actualColor = clr;
		}
		
		public String getName() {
			return name;
		}
		
		public LSColor getColor() {
			return actualColor;
		}
	}

	private final int r, g, b;

	public LSColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static LSColor fromHex(int hex) {
		int r = (hex & 0xFF0000) >> 16;
		int g = (hex & 0xFF00) >> 8;
		int b = (hex & 0xFF);
		return new LSColor(r, g, b);
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public void apply(float alpha) {
		GL11.glColor4f(getR(), getG(), getB(), alpha);
	}
}
