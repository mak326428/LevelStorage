package makmods.levelstorage.logic.util;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
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

	public static void drawFluidWise(Icon icon, double x, double y,
			double width, double height, double z) {
		double iconWidthStep = (icon.getMaxU() - icon.getMinU()) / 16.0D;
		double iconHeightStep = (icon.getMaxV() - icon.getMinV()) / 16.0D;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		for (double cy = y; cy < y + height; cy += 16.0D) {
			double quadHeight = Math.min(16.0D, height + y - cy);
			double maxY = cy + quadHeight;
			double maxV = icon.getMinV() + iconHeightStep * quadHeight;

			for (double cx = x; cx < x + width; cx += 16.0D) {
				double quadWidth = Math.min(16.0D, width + x - cx);
				double maxX = cx + quadWidth;
				double maxU = icon.getMinU() + iconWidthStep * quadWidth;

				tessellator.addVertexWithUV(cx, maxY, z, icon.getMinU(), maxV);
				tessellator.addVertexWithUV(maxX, maxY, z, maxU, maxV);
				tessellator.addVertexWithUV(maxX, cy, z, maxU, icon.getMinV());
				tessellator.addVertexWithUV(cx, cy, z, icon.getMinU(),
						icon.getMinV());
			}
		}

		tessellator.draw();
	}

	public static void renderIcon(Icon icon, double size, double z, float nx,
			float ny, float nz) {
		renderIcon(icon, 0.0D, 0.0D, size, size, z, nx, ny, nz);
	}

	public static void renderIcon(Icon icon, double xStart, double yStart,
			double xEnd, double yEnd, double z, float nx, float ny, float nz) {
		if (icon == null) {
			LogHelper.severe("[RenderHelper] renderIcon: icon is null, that's a bug!");
			return;
		}

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();
		tessellator.setNormal(nx, ny, nz);

		if (nz > 0.0F) {
			tessellator.addVertexWithUV(xStart, yStart, z, icon.getMinU(),
					icon.getMinV());
			tessellator.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(),
					icon.getMinV());
			tessellator.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(),
					icon.getMaxV());
			tessellator.addVertexWithUV(xStart, yEnd, z, icon.getMinU(),
					icon.getMaxV());
		} else {
			tessellator.addVertexWithUV(xStart, yEnd, z, icon.getMinU(),
					icon.getMaxV());
			tessellator.addVertexWithUV(xEnd, yEnd, z, icon.getMaxU(),
					icon.getMaxV());
			tessellator.addVertexWithUV(xEnd, yStart, z, icon.getMaxU(),
					icon.getMinV());
			tessellator.addVertexWithUV(xStart, yStart, z, icon.getMinU(),
					icon.getMinV());
		}

		tessellator.draw();
	}
}