package makmods.levelstorage.client.render;

import makmods.levelstorage.logic.util.RenderHelper;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class TestParticleFX extends EntityFX {

	public TestParticleFX(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		this.particleAge = 100;
		setSize(0.02f, 0.02f);
	}

	public TestParticleFX(World par1World, double par2, double par4,
			double par6, double par8, double par10, double par12) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		// TODO Auto-generated constructor stub
	}
	
	public void renderParticle(Tessellator tessellator, float f, float f1,
	        float f2, float f3, float f4, float f5) {
		tessellator.draw();
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		RenderHelper.bindSingleSlotGUI();
		tessellator.startDrawingQuads();
		tessellator.setBrightness(200);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, 0.0D, 1.0D);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge) {
			setDead();
		}
	}

}
