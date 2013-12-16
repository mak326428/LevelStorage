package makmods.levelstorage.logic.util;

import java.util.List;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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

	private static int gaugeLiquidScaled(int i, FluidTank tank) {
		if (tank.getFluidAmount() <= 0)
			return 0;

		return tank.getFluidAmount() * i / tank.getCapacity();
	}

	public static void renderTank(FluidTank tank, int xOffset, int yOffset,
			int x, int y) {
		xOffset -= 1;
		yOffset -= 1;
		RenderHelper.bindTexture(ClientProxy.GUI_ELEMENTS);
		// background
		drawTexturedModalRect(xOffset + x, yOffset + y, 0, 0, 18, 62);
		// liquid
		if (tank.getFluidAmount() > 0) {
			Icon fluidIcon = tank.getFluid().getFluid().getIcon();

			if (fluidIcon != null) {
				Minecraft.getMinecraft().renderEngine
						.bindTexture(TextureMap.locationBlocksTexture);
				int liquidHeight = gaugeLiquidScaled(60, tank);
				RenderHelper.drawFluidWise(fluidIcon, xOffset + x + 1, yOffset
						+ y + 60 + 1 - liquidHeight, 16.0D, liquidHeight,
						zLevel);
			}
		}
		RenderHelper.bindTexture(ClientProxy.GUI_ELEMENTS);
		// gauge
		drawTexturedModalRect(xOffset + x, yOffset + y, 18, 0, 18, 62);
	}

	private static double zLevel = 0.0D;

	public static void drawTexturedModalRect(int par1, int par2, int par3,
			int par4, int par5, int par6) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double) (par1 + 0),
				(double) (par2 + par6), (double) zLevel,
				(double) ((float) (par3 + 0) * f),
				(double) ((float) (par4 + par6) * f1));
		tessellator.addVertexWithUV((double) (par1 + par5),
				(double) (par2 + par6), (double) zLevel,
				(double) ((float) (par3 + par5) * f),
				(double) ((float) (par4 + par6) * f1));
		tessellator.addVertexWithUV((double) (par1 + par5),
				(double) (par2 + 0), (double) zLevel,
				(double) ((float) (par3 + par5) * f),
				(double) ((float) (par4 + 0) * f1));
		tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0),
				(double) zLevel, (double) ((float) (par3 + 0) * f),
				(double) ((float) (par4 + 0) * f1));
		tessellator.draw();
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
			LogHelper
					.severe("[RenderHelper] renderIcon: icon is null, that's a bug!");
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

	public static void renderTooltip(int x, int y, List<String> tooltipData) {
		int color = 0x505000ff;
		int color2 = 0xf0100010;

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		net.minecraft.client.renderer.RenderHelper
				.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		if (!tooltipData.isEmpty()) {
			int var5 = 0;
			int var6;
			int var7;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			for (var6 = 0; var6 < tooltipData.size(); ++var6) {
				var7 = fontRenderer.getStringWidth(tooltipData.get(var6));
				if (var7 > var5)
					var5 = var7;
			}
			var6 = x + 12;
			var7 = y - 12;
			int var9 = 8;
			if (tooltipData.size() > 1)
				var9 += 2 + (tooltipData.size() - 1) * 10;
			float z = 300.0F;
			drawGradientRect(var6 - 3, var7 - 4, z, var6 + var5 + 3, var7 - 3,
					color2, color2);
			drawGradientRect(var6 - 3, var7 + var9 + 3, z, var6 + var5 + 3,
					var7 + var9 + 4, color2, color2);
			drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7
					+ var9 + 3, color2, color2);
			drawGradientRect(var6 - 4, var7 - 3, z, var6 - 3, var7 + var9 + 3,
					color2, color2);
			drawGradientRect(var6 + var5 + 3, var7 - 3, z, var6 + var5 + 4,
					var7 + var9 + 3, color2, color2);
			int var12 = (color & 0xFFFFFF) >> 1 | color & -16777216;
			drawGradientRect(var6 - 3, var7 - 3 + 1, z, var6 - 3 + 1, var7
					+ var9 + 3 - 1, color, var12);
			drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, z, var6 + var5 + 3,
					var7 + var9 + 3 - 1, color, var12);
			drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3,
					var7 - 3 + 1, color, color);
			drawGradientRect(var6 - 3, var7 + var9 + 2, z, var6 + var5 + 3,
					var7 + var9 + 3, var12, var12);
			for (int var13 = 0; var13 < tooltipData.size(); ++var13) {
				String var14 = tooltipData.get(var13);
				fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
				if (var13 == 0)
					var7 += 2;
				var7 += 10;
			}
		}
	}

	public static void drawGradientRect(int par1, int par2, float z, int par3,
			int par4, int par5, int par6) {
		float var7 = (par5 >> 24 & 255) / 255.0F;
		float var8 = (par5 >> 16 & 255) / 255.0F;
		float var9 = (par5 >> 8 & 255) / 255.0F;
		float var10 = (par5 & 255) / 255.0F;
		float var11 = (par6 >> 24 & 255) / 255.0F;
		float var12 = (par6 >> 16 & 255) / 255.0F;
		float var13 = (par6 >> 8 & 255) / 255.0F;
		float var14 = (par6 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator var15 = Tessellator.instance;
		var15.startDrawingQuads();
		var15.setColorRGBA_F(var8, var9, var10, var7);
		var15.addVertex(par3, par2, z);
		var15.addVertex(par1, par2, z);
		var15.setColorRGBA_F(var12, var13, var14, var11);
		var15.addVertex(par1, par4, z);
		var15.addVertex(par3, par4, z);
		var15.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}