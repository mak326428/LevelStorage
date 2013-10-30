package makmods.levelstorage.client.render;

import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class StarRenderer extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float partialTicks) {
		GL11.glPushMatrix();
		// This is setting the initial location.
		GL11.glTranslatef((float) x, (float) y, (float) z);
		RenderHelper.bindTexture(ClientProxy.SUN_TEXTURE);
		// GL11.glPushMatrix();
		// Tessellator tessellator = Tessellator.instance;
		// tessellator.startDrawingQuads();
		// tessellator.addVertexWithUV(0.0D, 0.0D, z, 0.0D, 1.0D);
		// tessellator.addVertexWithUV(16.0D, 0.0D, z, (double) 1, (double) 1);
		// tessellator.addVertexWithUV(16.0D, 16.0D, z, (double) 1, (double) 0);
		// tessellator.addVertexWithUV(0.0D, 16.0D, z, (double) 0, (double) 0);
		// tessellator.draw();
		GL11.glPushMatrix();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Sphere sph = new Sphere();
		sph.draw(0.4F, 16, 16);
		GL11.glPopMatrix();
		// GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
