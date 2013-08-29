package makmods.levelstorage.render;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class MassInfuserRender extends TileEntitySpecialRenderer {
	// Model is the same, i see no sense in simple codecloning
	public ConductorModel model = new ConductorModel();

	public MassInfuserRender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
	        float scale) {
		// The PushMatrix tells the renderer to "start" doing something.

		GL11.glPushMatrix();
		// This is setting the initial location.
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		// This is the texture of your block. It's pathed to be the same place
		// as your other blocks here.
		// BIND TEXTURE HERE
		FMLClientHandler.instance().getClient().renderEngine
		        .func_110577_a(ClientProxy.MASS_INFUSER_MODEL);
		// This rotation part is very important! Without it, your model will
		// render upside-down! And for some reason you DO need PushMatrix again!
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		// A reference to your Model file. Again, very important.
		this.model
		        .render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		// Tell it to stop rendering for both the PushMatrix's
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
